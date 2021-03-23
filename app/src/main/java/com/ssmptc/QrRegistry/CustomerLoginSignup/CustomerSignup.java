package com.ssmptc.QrRegistry.CustomerLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Script;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ssmptc.QrRegistry.R;

import java.util.concurrent.TimeUnit;

public class CustomerSignup extends AppCompatActivity {


    private EditText _name;
    private EditText _email;
    private EditText _password;
    private EditText _phone;
    private Button getOtp;
    private TextView login;


    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);

        _name = findViewById(R.id.et_name);
        _email = findViewById(R.id.et_email);
        _password = findViewById(R.id.et_password);
        _phone = findViewById(R.id.te_phone);
        getOtp = findViewById(R.id.get_Otp);
        login = findViewById(R.id.btn_login);
        auth = FirebaseAuth.getInstance();


        LottieAnimationView lottieAnimationView1 = findViewById(R.id.customer_animation);
        lottieAnimationView1.loop(true);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerSignup.this,CustomerLogin.class);
                startActivity(intent);
            }
        });

        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = _phone.getText().toString();
                String phoneNumber = "+91" + phone;


                if (!phone.isEmpty()) {
                    if (phone.length() == 10) {


                        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(phoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(CustomerSignup.this)
                                .setCallbacks(mCallBacks)
                                .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);


                    } else {
                        Toast.makeText(CustomerSignup.this, "Please Enter Correct Mobile Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CustomerSignup.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(CustomerSignup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                //sometime the code is not detected automatically
                //so user has to manually enter the code

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent otpIntent = new Intent(CustomerSignup.this, CustomerVerification.class);
                        otpIntent.putExtra("auth", s);
                        String phoneNumber = "+91"+_phone.getText().toString();
                        otpIntent.putExtra("phoneNumber", phoneNumber);

                        String name = _name.getText().toString();
                        String email = _email.getText().toString();
                        String password = _password.getText().toString();
                        otpIntent.putExtra("name",name);
                        otpIntent.putExtra("email",email);
                        otpIntent.putExtra("password",password);
                       // otpIntent.putExtra("_email",s);
                       // otpIntent.putExtra("_password",);
                        startActivity(otpIntent);
                    }
                }, 1);

            }
        };

    }
}

