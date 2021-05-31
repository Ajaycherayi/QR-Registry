package com.ssmptc.QrRegistry.ShopLoginSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
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

public class ShopQRCode extends AppCompatActivity {


    ImageView btn_back,btn_screenshot;
    TextView tv_ShoName;

    private ProgressDialog progressDialog;
    private LinearLayout layout_qrCode,bg_screenShot;
    private ImageView qr_output;
    private TextView txt_screenShot;

    //--------------- Encryption Variables -----------
    String AES = "AES";
    private String data;
    //------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_qr_code);

        qr_output = findViewById(R.id.qr_output);
        btn_back = findViewById(R.id.btn_backToSd);
        tv_ShoName = findViewById(R.id.tv_shopName);
        layout_qrCode = findViewById(R.id.layout_qrCode);
        btn_screenshot = findViewById(R.id.btn_screenshot);
        bg_screenShot = findViewById(R.id.bg_screenShot);
        txt_screenShot = findViewById(R.id.txt_screenShot);

        SessionManagerShop managerShop = new SessionManagerShop(getApplicationContext());
        String shopId = managerShop.getShopId();
        String shopName = managerShop.getShopName();
        tv_ShoName.setText(shopName);

        layout_qrCode.setVisibility(View.GONE);
        bg_screenShot.setVisibility(View.GONE);
        txt_screenShot.setVisibility(View.GONE);

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),ShopDashBoard.class));
            finish();
        });

        //--------------- Internet Checking -----------
        if (!isConnected(ShopQRCode.this)){
            showCustomDialog();
        }

        //--------------- Initialize ProgressDialog -----------
        progressDialog = new ProgressDialog(ShopQRCode.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Query getShopData = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Profile");
        getShopData.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //--------------- Access shop Id to Strings -----------
                String appName = "QrRegistryShop";
                String  id = snapshot.child("id").getValue(String.class);
                String shopName  = snapshot.child("shopName").getValue(String.class);

                try {
                    assert id != null;
                    data = encrypt(id);
                    MultiFormatWriter writer = new MultiFormatWriter();
                    try {
                        BitMatrix matrix = writer.encode( appName + ":"+shopName+":"+ data,
                                BarcodeFormat.QR_CODE,350,350);

                        BarcodeEncoder encoder =new BarcodeEncoder();

                        Bitmap bitmap = encoder.createBitmap(matrix);

                        progressDialog.dismiss();

                        layout_qrCode.setVisibility(View.VISIBLE);
                        bg_screenShot.setVisibility(View.VISIBLE);
                        txt_screenShot.setVisibility(View.VISIBLE);

                        qr_output.setImageBitmap(bitmap);

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

        //--------------- Button for Screen Shot -----------
        btn_screenshot.setOnClickListener(v -> {
            verifyStoragePermission(ShopQRCode.this);
            takeScreenshot();
        });

    }

    //--------------- Encode Data -----------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String encrypt(String total) throws Exception{
        String keyPass = "qrregistry@shop";
        SecretKeySpec key = generateKey(keyPass);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(total.getBytes());
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

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ShopQRCode.this);
        builder.setMessage("Please connect to the internet")
                //.setCancelable(false)
                .setPositiveButton("Connect", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), ShopDashBoard.class));
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(ShopQRCode shopQRCode) {

        ConnectivityManager connectivityManager = (ConnectivityManager) shopQRCode.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

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

            Toast.makeText(ShopQRCode.this, "Screen Shot Saved in Download Folder", Toast.LENGTH_SHORT).show();

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
