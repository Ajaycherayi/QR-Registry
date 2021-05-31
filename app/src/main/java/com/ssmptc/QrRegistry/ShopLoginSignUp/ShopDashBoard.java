package com.ssmptc.QrRegistry.ShopLoginSignUp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.UserDashBoard;
import com.ssmptc.QrRegistry.DataBase.Shop.SessionManagerShop;
import com.ssmptc.QrRegistry.QRCodeScanner;
import com.ssmptc.QrRegistry.R;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ShopDashBoard extends AppCompatActivity {

    TextView textView;
    ImageView btn_back;

    String shopId,decodedData;

    SessionManagerShop managerShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_dash_board);

        textView = findViewById(R.id.show_name);
        btn_back = findViewById(R.id.btn_back);

        managerShop = new SessionManagerShop(getApplicationContext());
        String sName = managerShop.getShopName();
        textView.setText(sName);

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(ShopDashBoard.this, UserDashBoard.class));
            finish();
        });

    }

    public void ScannerForShop(View view) {

        //--------------- Internet Checking -----------
        if (!isConnected(ShopDashBoard.this)){
            showCustomDialog();
        }else {
            scanCode();
        }
    }

    private void scanCode() {

        IntentIntegrator intentIntegrator = new IntentIntegrator(ShopDashBoard.this); //Initialize intent integrator

        intentIntegrator.setPrompt("For Flash Use Volume Up Key");  //Set Prompt text
        intentIntegrator.setBeepEnabled(true);  //set beep
        intentIntegrator.setCameraId(0);  //set Camera
        intentIntegrator.setOrientationLocked(true);  //Locked Orientation
        intentIntegrator.setCaptureActivity(QRCodeScanner.class);  //Set Capture Activity
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.initiateScan();  //Initiate Scan

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Initiate Intent Result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        //Check Condition
        if (intentResult.getContents() != null && resultCode==RESULT_OK) {

            managerShop = new SessionManagerShop(getApplicationContext());
            shopId = managerShop.getShopId();

            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            DatabaseReference reference = rootNode.getReference("Shops").child(shopId).child("Customers");

            String output = intentResult.getContents();

            if (output.startsWith("QrRegistryUser")) {
                String[] separated = output.split(":");

                String name = separated[1];
                String userDetails = separated[2];

                try {
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String currentTime = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());

                    decodedData = (String) decrypt(userDetails);

                    String[] separateData = decodedData.split(":");
                    String phoneNumber = separateData[0];

                    String id = reference.push().getKey();
                    if (id != null){
                        reference.child(id).child("phoneNumber").setValue(phoneNumber);
                        reference.child(id).child("id").setValue(id);
                        reference.child(id).child("Name").setValue(name);
                        reference.child(id).child("currentDate").setValue(currentDate);
                        reference.child(id).child("currentTime").setValue(currentTime);
                    }
                    Toast.makeText(ShopDashBoard.this, "Customer data added", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                        e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                    //Initialize Dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopDashBoard.this);

                    builder.setTitle("Result"); //Set Title
                    builder.setMessage("Read Successfully");  //Set Message

                    //set Positive Button
                    builder.setPositiveButton("Scan Again", (dialog, which) -> scanCode()).setNegativeButton("OK", (dialog, which) -> dialog.dismiss());

                    builder.show();  //Show Alert Dialog

                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopDashBoard.this);
                    builder.setMessage("Wrong QR Code");
                    builder.setPositiveButton("Scan Again", (dialog, which) -> scanCode());
                    builder.show();
                }
            } else {

                Toast.makeText(getApplicationContext(), "OOPS... You did Not Scan Anything", Toast.LENGTH_SHORT).show();

            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //------------------------------------ Shop QR Code ------------------------
    public void ShopQR(View view) {
        startActivity(new Intent(getApplicationContext(),ShopQRCode.class));
        finish();
    }

    //------------------------------------ Update shop profile ------------------------
    public void UpdateShopProfile(View view) {
        startActivity(new Intent(getApplicationContext(), EditShopProfile.class));
    }

    //------------------------------------ Show Today's Customer List ------------------------
    public void CustomerList(View view) {
        startActivity(new Intent(getApplicationContext(), TodayCustomers.class));
    }

    //------------------------------------ Show all Customers ------------------------
    public void CustomerReport(View view) {
        startActivity(new Intent(getApplicationContext(), AllCustomers.class));
    }

    //------------------------------------ Logout Shop ------------------------
    public void Logout(View view) {
        logout();
    }

    private void logout() {
        //managerShop = new SessionManagerShop(getApplicationContext());

        //Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Set Title
        builder.setTitle("Log out");

        //set Message
        builder.setMessage("Are you sure to Log out ?");

        //positive YES button
        builder.setPositiveButton("YES", (dialog, which) -> {

            managerShop.setShopLogin(false);
            managerShop.setDetails("","","");
            //activity.finishAffinity();
           // dialog.dismiss();

            //Finish Activity
            startActivity(new Intent(getApplicationContext(), UserDashBoard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK ));
            finish();
        });

        //Negative NO button
        builder.setNegativeButton("NO", (dialog, which) -> {
            //Dismiss Dialog
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Object decrypt(String userDetails) throws Exception {

        String keyPass = "qrregistry@user";
        SecretKeySpec key = generateKey(keyPass);
        String AES = "AES";
        @SuppressLint("GetInstance") Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodeValue = android.util.Base64.decode(userDetails,android.util.Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        return new String(decValue);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private SecretKeySpec generateKey(String keyPass) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = keyPass.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key,"AES");
    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShopDashBoard.this);
        builder.setMessage("Please connect to the internet")
                //   .setCancelable(false)
                .setPositiveButton("Connect", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), ShopDashBoard.class));
                    finish();
                });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(ShopDashBoard shopDashBoard) {

        ConnectivityManager connectivityManager = (ConnectivityManager) shopDashBoard.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }

}