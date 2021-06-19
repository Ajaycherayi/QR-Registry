package com.ssmptc.QrRegistry.Shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ssmptc.QrRegistry.User.UserDashBoard;
import com.ssmptc.QrRegistry.R;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ShopSignUp extends AppCompatActivity {

   private FirebaseAuth firebaseAuth;
   private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

   private String codeSend,shopName,location,category,ownerName,password,cPhoneNumber;

   private ProgressDialog progressDialog;

    Button btn_getOtp,btn_ToLogin;
    ImageView
    btn_back;

    //Variables
    private TextInputLayout et_shopLocation,et_shopCategory,et_ownerName,et_password,et_shopName,et_phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_signup);

        et_shopName = findViewById(R.id.et_shopName);
        et_shopLocation = findViewById(R.id.et_shopLocation);
        et_shopCategory = findViewById(R.id.et_category);
        et_ownerName = findViewById(R.id.et_ownerName);
        et_password = findViewById(R.id.et_shopPassword);
        et_phoneNumber = findViewById(R.id.et_phone);

        btn_getOtp = findViewById(R.id.btn_getOtp);
        btn_ToLogin = findViewById(R.id.btn_ToLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_back = findViewById(R.id.btn_backToCd);

        if (!isConnected(ShopSignUp.this)){
            showCustomDialog();
        }

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(ShopSignUp.this, UserDashBoard.class));
            finish();

        });

        btn_ToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ShopSignUp.this,ShopLogin.class));
            finish();

        });

        btn_getOtp.setOnClickListener(v -> {

            if (!validateShopName() | !validateShopCategory() | !validateShopLocation() | !validateOwnerName() | !validatePassword() | !validatePhoneNumber()) {

                return;
            }

            //Initialize ProgressDialog
            progressDialog = new ProgressDialog(ShopSignUp.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            shopName = Objects.requireNonNull(et_shopName.getEditText()).getText().toString();
            location = Objects.requireNonNull(et_shopLocation.getEditText()).getText().toString();
            category = Objects.requireNonNull(et_shopCategory.getEditText()).getText().toString();
            ownerName = Objects.requireNonNull(et_ownerName.getEditText()).getText().toString();
            password = Objects.requireNonNull(et_password.getEditText()).getText().toString();
            String phoneNumber = Objects.requireNonNull(et_phoneNumber.getEditText()).getText().toString();

            cPhoneNumber = "+91"+phoneNumber;

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(cPhoneNumber)
                    .setTimeout(60L,TimeUnit.SECONDS) //Time Out Set
                    .setActivity(ShopSignUp.this)
                    .setCallbacks(mCallbacks)
                    .build();

            PhoneAuthProvider.verifyPhoneNumber(options);
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // Automatic Verification
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                Toast.makeText(ShopSignUp.this, "OTP is Send", Toast.LENGTH_SHORT).show();
                codeSend = s;

                progressDialog.dismiss();

                Intent intent = new Intent(ShopSignUp.this, ShopPhoneNumberVerification.class);
                intent.putExtra("otp",codeSend);
                intent.putExtra("shopName",shopName);
                intent.putExtra("location",location);
                intent.putExtra("category",category);
                intent.putExtra("ownerName",ownerName);
                intent.putExtra("password",password);
                intent.putExtra("phoneNumber",cPhoneNumber);
                startActivity(intent);

            }
        };
    }


    private boolean validateShopName(){
        String val = Objects.requireNonNull(et_shopName.getEditText()).getText().toString().trim();

        if (val.isEmpty()){
            et_shopName.setError("Field can not be empty");
            return false;
        }else if(val.length()>25){
            et_shopName.setError("ShopName is too large!");
            return false;
        }else {
            et_shopName.setError(null);
            et_shopName.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validateShopLocation(){
        String val = Objects.requireNonNull(et_shopLocation.getEditText()).getText().toString().trim();

        if (val.isEmpty()){
            et_shopLocation.setError("Field can not be empty");
            return false;
        }else {
            et_shopLocation.setError(null);
            et_shopLocation.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validateShopCategory(){
        String val = Objects.requireNonNull(et_shopCategory.getEditText()).getText().toString().trim();

        if (val.isEmpty()){
            et_shopCategory.setError("Field can not be empty");
            return false;
        }else {
            et_shopCategory.setError(null);
            et_shopCategory.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validatePhoneNumber(){
        String val = Objects.requireNonNull(et_phoneNumber.getEditText()).getText().toString().trim();

        if (val.isEmpty()){
            et_phoneNumber.setError("Field can not be empty");
            return false;
        }else if(val.length()>10 | val.length()<10){
            et_phoneNumber.setError("Please Enter 10 Digit Phone Number");
            return false;
        }else if (!val.matches("\\w*")){
            et_phoneNumber.setError("White spaces not allowed");
            return false;
        }else {
            et_phoneNumber.setError(null);
            return true;
        }
    }
    private boolean validateOwnerName(){
        String val = Objects.requireNonNull(et_ownerName.getEditText()).getText().toString().trim();

        if (val.isEmpty()){
            et_ownerName.setError("Field can not be empty");
            return false;
        }else {
            et_ownerName.setError(null);
            et_ownerName.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validatePassword(){
        String val = Objects.requireNonNull(et_password.getEditText()).getText().toString().trim();

        if (val.isEmpty()){
            et_password.setError("Field can not be empty");
            return false;
        }else if(val.length() < 8 ){
            et_password.setError("password should contain 8 characters!");
            return false;
        }else {
            et_password.setError(null);
            et_password.setErrorEnabled(false);
            return true;
        }

    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ShopSignUp.this);
        builder.setMessage("Please connect to the internet")
                .setCancelable(false)
                .setPositiveButton("Connect", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(),UserDashBoard.class));
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(ShopSignUp shopSignup) {

        ConnectivityManager connectivityManager = (ConnectivityManager) shopSignup.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }

}