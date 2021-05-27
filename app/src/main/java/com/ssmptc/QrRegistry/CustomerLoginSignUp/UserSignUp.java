package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.R;

import java.util.concurrent.TimeUnit;

public class UserSignUp extends AppCompatActivity {

    //Variable
    private TextInputLayout et_userName,et_age,et_phoneNumber,et_password;
    Button btn_getOtp,btn_login;

    RadioGroup rg_gender;
    RadioButton rb_selectedGender;

    ProgressDialog progressDialog;

    //FireBase Variables
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup);

        et_userName = findViewById(R.id.et_userName);
        et_age = findViewById(R.id.et_age);
        et_password = findViewById(R.id.et_password);
        et_phoneNumber = findViewById(R.id.et_phoneNumber);
        rg_gender = findViewById(R.id.radio_group);

        btn_getOtp = findViewById(R.id.btn_getOtp);
        btn_login = findViewById(R.id.btn_backToLogin);

        auth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserLogin.class);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String>(findViewById(R.id.btn_backToLogin),"transition_login");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserSignUp.this,pairs);
                    startActivity(intent,options.toBundle());
                }
                else{
                    finish();
                }
            }
        });

        btn_getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EditText Validations
                if (!validatePhoneNumber()  | !validateUserName() | !validateAge() | !validatePassword() | !validateGender()) {

                    return;
                }

                rb_selectedGender = findViewById(rg_gender.getCheckedRadioButtonId());
                String gender =  rb_selectedGender.getText().toString();

                String phone = et_phoneNumber.getEditText().getText().toString();
                String phoneNumber = "+91" + phone;

                if (!phone.isEmpty()) {
                    if (phone.length() == 10) {

                        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(phoneNumber);

                        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    Toast.makeText(UserSignUp.this, "This User already Exist  Please Login", Toast.LENGTH_LONG).show();

                                } else {

                                    //Initialize ProgressDialog
                                    progressDialog = new ProgressDialog(UserSignUp.this);
                                    progressDialog.show();
                                    progressDialog.setContentView(R.layout.progress_dialog);
                                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    progressDialog.setCancelable(false);

                                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                                            .setPhoneNumber(phoneNumber)
                                            .setTimeout(60L, TimeUnit.SECONDS)
                                            .setActivity(UserSignUp.this)
                                            .setCallbacks(mCallBacks)
                                            .build();
                                    PhoneAuthProvider.verifyPhoneNumber(options);

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(UserSignUp.this, "Please Enter Correct Mobile Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UserSignUp.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(UserSignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                //sometime the code is not detected automatically
                //so user has to manually enter the code

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                                    Intent otpIntent = new Intent(UserSignUp.this, CustomerVerification.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    otpIntent.putExtra("auth", s);
                                    String phoneNumber = "+91" + et_phoneNumber.getEditText().getText().toString();
                                    otpIntent.putExtra("phoneNumber", phoneNumber);

                                    String name = et_userName.getEditText().getText().toString();
                                    String age = et_age.getEditText().getText().toString();
                                    String gender = rb_selectedGender.getText().toString();
                                    String password = et_password.getEditText().getText().toString();
                                    otpIntent.putExtra("name", name);
                                    otpIntent.putExtra("age", age);
                                    otpIntent.putExtra("gender", gender);
                                    otpIntent.putExtra("password", password);
                                    startActivity(otpIntent);
                                    finish();
                                }

                }, 1);

            }
        };

    }

    private boolean validatePhoneNumber() {

        String val = et_phoneNumber.getEditText().getText().toString().trim();

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

    private boolean validateUserName() {
        String val = et_userName.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_userName.setError("Field can not be empty");
            return false;
        }else if(val.length()>25){
            et_userName.setError("Name is Too Large");
            return false;
        }else {
            et_userName.setError(null);
            return true;
        }
    }

    private boolean validateAge() {
       // String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String val = et_age.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_age.setError("Field can not be empty");
            return false;
        }else if(val.length()>3){
            et_age.setError("Enter Valid Age");
            return false;
        }else {
            et_age.setError(null);
            return true;
        }

    }

    private boolean validatePassword() {
        String val = et_password.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_password.setError("Field can not be empty");
            return false;
        }else if(val.length()<8) {
            et_password.setError("Password minimum 8 Characters");
            return false;
        }else if (!val.matches("\\w*")){
            et_password.setError("White spaces not allowed");
            return false;
        }else {
            et_password.setError(null);
            return true;
        }

    }

    private boolean validateGender(){

        if (rg_gender.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        }else
            return true;
    }
}

