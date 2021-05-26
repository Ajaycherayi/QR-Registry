package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.UserDashBoard;
import com.ssmptc.QrRegistry.R;

import java.util.concurrent.TimeUnit;

public class ShopSignup extends AppCompatActivity {

   private FirebaseAuth firebaseAuth;
   private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

   String codeSend,shopName,location,category,ownerName,password,cPhoneNumber;



    Button btn_getOtp;
    ImageView b1;

    //Variables
    private TextInputLayout et_shopLocation,et_shopCategory,et_ownerName,et_password,et_shopName,et_phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_signup);

        et_shopName = findViewById(R.id.nameOfShop);
        et_shopLocation = findViewById(R.id.et_shopLocation);
        et_shopCategory = findViewById(R.id.et_category);
        et_ownerName = findViewById(R.id.et_ownerName);
        et_password = findViewById(R.id.et_shopPassword);
        et_phoneNumber = findViewById(R.id.et_phone);

        btn_getOtp = findViewById(R.id.btn_getOtp);

        firebaseAuth = FirebaseAuth.getInstance();


        b1 = findViewById(R.id.btn_backToCd);








        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopSignup.this, UserDashBoard.class));
                finish();

            }
        });

        btn_getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateShopName() | !validateShopCategory() | !validateShopLocation() | !validateOwnerName() ) {

                    return;
                }

                shopName = et_shopName.getEditText().getText().toString();
                location = et_shopLocation.getEditText().getText().toString();
                category = et_shopCategory.getEditText().getText().toString();
                ownerName = et_ownerName.getEditText().getText().toString();
                password = et_password.getEditText().getText().toString();
                String phoneNumber = et_phoneNumber.getEditText().getText().toString();

                cPhoneNumber = "+91"+phoneNumber;

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(cPhoneNumber)
                        .setTimeout(60L,TimeUnit.SECONDS) //Time Out Set
                        .setActivity(ShopSignup.this)
                        .setCallbacks(mCallbacks)
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(options);

            }
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

                Toast.makeText(ShopSignup.this, "OTP is Send", Toast.LENGTH_SHORT).show();
                codeSend = s;

                Intent intent = new Intent(ShopSignup.this,ShopPhoneVerification.class);
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

    public void ToLogin(View view) {

        startActivity(new Intent(ShopSignup.this,ShopLogin.class));
        finish();

    }

  /*  private boolean validateAll(){
        String val = et_shopName.getText().toString().trim();
        String checkspaces = "\\A\\w{4,20}\\z";

        if (val.isEmpty()){
            et_shopName.setError("Field can not be empty");
            return false;
        }else if(val.length()>25){
            et_shopName.setError("ShopName is too large!");
            return false;
        }else if (!val.matches(checkspaces)){
            et_shopName.setError("White spaces not allowed");
         return false;
        }else {
            et_shopName.setError(null);
            return true;
        }

    }*/
    private boolean validateShopName(){
        String val = et_shopName.getEditText().getText().toString().trim();

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
        String val = et_shopLocation.getEditText().getText().toString().trim();

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
        String val = et_shopCategory.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_shopCategory.setError("Field can not be empty");
            return false;
        }else {
            et_shopCategory.setError(null);
            et_shopCategory.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validateOwnerName(){
        String val = et_ownerName.getEditText().getText().toString().trim();

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
        String val = et_password.getEditText().getText().toString().trim();

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


}