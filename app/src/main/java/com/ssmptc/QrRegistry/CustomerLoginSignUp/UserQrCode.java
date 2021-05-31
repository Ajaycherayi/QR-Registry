package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ssmptc.QrRegistry.DataBase.SessionManagerUser;
import com.ssmptc.QrRegistry.R;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class UserQrCode extends AppCompatActivity {

    LinearLayout bg_screenShot;
    TextView txt_screenShot;

    ImageView btn_screenshot,btn_backToCd,qr_output;
    ProgressDialog progressDialog;

    //--------------- Encryption Variables -----------
    String AES = "AES";
    String keyPass = "qrregistry@user";
    //------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_qr_code);

        qr_output = findViewById(R.id.qr_output);
        btn_screenshot = findViewById(R.id.btn_screenshot);
        btn_backToCd  = findViewById(R.id.btn_backToCd);
        bg_screenShot = findViewById(R.id.bg_screenShot);
        txt_screenShot = findViewById(R.id.txt_screenShot);

        SessionManagerUser managerCustomer = new SessionManagerUser(getApplicationContext());
        String phoneNo = managerCustomer.getPhone(); //Get Phone Number from session

        //--------------- Internet Checking -----------
        if (!isConnected(UserQrCode.this)){
            showCustomDialog();
        }

        //--------------- Initialize ProgressDialog -----------
        progressDialog = new ProgressDialog(UserQrCode.this);
        progressDialog.show();
        //progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        bg_screenShot.setVisibility(View.GONE);
        txt_screenShot.setVisibility(View.GONE);

        //--------------- Data Access from User Database -----------
        Query getCustomerDetails = FirebaseDatabase.getInstance().getReference("Users").child(phoneNo).child("Profile");
        getCustomerDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //--------------- Access Data to Strings -----------
                String appName = "QrRegistryUser";
                String name = snapshot.child("name").getValue(String.class);
                String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);

                //--------------- Encoding Data -----------
                try {
                   // assert phoneNumber != null;
                    String encodedData = encrypt(phoneNumber);
                    MultiFormatWriter writer = new MultiFormatWriter();

                    //--------------- Create QR code -----------
                    try {
                        BitMatrix matrix = writer.encode( appName+":"+ name + ":" + encodedData , BarcodeFormat.QR_CODE,350,350);

                        BarcodeEncoder encoder =new BarcodeEncoder();
                        Bitmap bitmap = encoder.createBitmap(matrix);
                        qr_output.setImageBitmap(bitmap);

                        progressDialog.dismiss();

                        bg_screenShot.setVisibility(View.VISIBLE);
                        txt_screenShot.setVisibility(View.VISIBLE);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //--------------- Button for back to User Dashboard -----------
        btn_backToCd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserQrCode.this,UserDashBoard.class));
                finish();
            }
        });

        //--------------- Button for Screen Shot -----------
        btn_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermission(UserQrCode.this);
                takeScreenshot();
            }
        });

    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserQrCode.this);
        builder.setMessage("Please connect to the internet")
             //   .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),UserDashBoard.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(UserQrCode userQrCode) {

        ConnectivityManager connectivityManager = (ConnectivityManager) userQrCode.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }

    //--------------- Encode Data -----------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String encrypt(String forEncode) throws Exception{
        SecretKeySpec key = generateKey(keyPass);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(forEncode.getBytes());
        return Base64.encodeToString(encVal,Base64.DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private SecretKeySpec generateKey(String keyPass) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = keyPass.getBytes(StandardCharsets.UTF_8); //"UTF-8"
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key, "AES"); //SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");

    }

    //--------------- Screen Shot Function -----------
    private void takeScreenshot() {
        String currentDate = new SimpleDateFormat("d-MMM-yy_HH-mm", Locale.getDefault()).format(new Date());
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/"+ "QrRegistry_" + currentDate + ".jpeg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(UserQrCode.this, "Screen Shot Saved in Download Folder", Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //--------------- Permission for write screenshot to storage -----------
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String [] PERMISSION_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermission(Activity activity){

        int permission = ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permission != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(activity,PERMISSION_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }


    }



}