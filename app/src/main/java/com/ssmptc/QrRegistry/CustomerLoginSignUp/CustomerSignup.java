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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.R;

import java.util.concurrent.TimeUnit;

public class CustomerSignup extends AppCompatActivity {

    //Variable
    private EditText _name;
    private EditText _email;
    private EditText _password;
    private EditText _phone;

    ProgressDialog progressDialog;

    //FireBase Variables
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);

        _name = findViewById(R.id.et_name);
        _email = findViewById(R.id.et_email);
        _password = findViewById(R.id.et_password);
        _phone = findViewById(R.id.te_phone);

        Button getOtp = findViewById(R.id.get_Otp);
        Button login = findViewById(R.id.btn_callLogin);
        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CustomerLogin.class);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String>(findViewById(R.id.btn_callLogin),"transition_login");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CustomerSignup.this,pairs);
                    startActivity(intent,options.toBundle());
                }
                else{
                    finish();
                }
            }
        });

        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = _phone.getText().toString();
                String phoneNumber = "+91" + phone;

                if (!phone.isEmpty()) {
                    if (phone.length() == 10) {

                        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(phoneNumber);

                        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    Toast.makeText(CustomerSignup.this, "This User already Exist  Please Login", Toast.LENGTH_LONG).show();

                                } else {

                                    //Initialize ProgressDialog
                                    progressDialog = new ProgressDialog(CustomerSignup.this);

                                    progressDialog.show();

                                    progressDialog.setContentView(R.layout.progress_dialog);

                                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                                            .setPhoneNumber(phoneNumber)
                                            .setTimeout(60L, TimeUnit.SECONDS)
                                            .setActivity(CustomerSignup.this)
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



                                    Intent otpIntent = new Intent(CustomerSignup.this, CustomerVerification.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    otpIntent.putExtra("auth", s);
                                    String phoneNumber = "+91" + _phone.getText().toString();
                                    otpIntent.putExtra("phoneNumber", phoneNumber);

                                    String name = _name.getText().toString();
                                    String email = _email.getText().toString();
                                    String password = _password.getText().toString();
                                    otpIntent.putExtra("name", name);
                                    otpIntent.putExtra("email", email);
                                    otpIntent.putExtra("password", password);
                                    // otpIntent.putExtra("_email",s);
                                    // otpIntent.putExtra("_password",);
                                    startActivity(otpIntent);
                                    finish();
                                }


                }, 1);

            }
        };

    }
}

