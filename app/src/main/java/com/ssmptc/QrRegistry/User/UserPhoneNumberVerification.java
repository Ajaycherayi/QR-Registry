package com.ssmptc.QrRegistry.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.ssmptc.QrRegistry.DataBase.User.SessionManagerUser;
import com.ssmptc.QrRegistry.DataBase.User.UserData;
import com.ssmptc.QrRegistry.R;
import java.util.concurrent.TimeUnit;

public class UserPhoneNumberVerification extends AppCompatActivity {

    Button btn_signUp;
    TextView tv_phoneNo;
    ImageView btn_back;

    SessionManagerUser managerCustomer;

    private TextView btn_resend,tv_counter,tv_resend;
    private EditText et_otp;
    private ProgressDialog progressDialog;

    private String name,age,gender,password,phoneNumber,location,getOtp;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_phone_number_verification);

        et_otp = findViewById(R.id.et_otp);

        btn_back = findViewById(R.id.btn_back);
        btn_signUp = findViewById(R.id.btn_signUp);
        btn_resend = findViewById(R.id.btn_resend);

        tv_counter = findViewById(R.id.tv_counter);
        tv_resend = findViewById(R.id.tv_resend);
        tv_phoneNo = findViewById(R.id.tv_phoneNo);

        getOtp = getIntent().getStringExtra("auth");
        name = getIntent().getStringExtra("name");
        location = getIntent().getStringExtra("location");
        age = getIntent().getStringExtra("age");
        gender = getIntent().getStringExtra("gender");
        password = getIntent().getStringExtra("password");
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        btn_resend.setVisibility(View.INVISIBLE);
        tv_resend.setVisibility(View.INVISIBLE);

        if (!isConnected(UserPhoneNumberVerification.this)){
            showCustomDialog();
        }

        managerCustomer = new SessionManagerUser(getApplicationContext());

        firebaseAuth = FirebaseAuth.getInstance();

        String mobile = phoneNumber;
        mobile = mobile.substring(0, 3) + "*****" + mobile.substring(9);
        tv_phoneNo.setText(mobile);

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(UserPhoneNumberVerification.this, UserSignUp.class));
            finish();
        });

        CountTimer();

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateOtp()) {
                    return;
                }
                //Initialize ProgressDialog
                progressDialog = new ProgressDialog(UserPhoneNumberVerification.this);
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                String enteredOtp = et_otp.getText().toString();

                /*                if(getOtp != null){
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(getOtp,enteredOtp);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                nodeId = String.valueOf(node+1000);
            */

                if (getOtp != null) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(getOtp, enteredOtp);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            storeNewUserData();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(UserPhoneNumberVerification.this, "Error occur", Toast.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UserPhoneNumberVerification.this, "Enter The Correct OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_resend.setOnClickListener(v -> {

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS) //Time Out Set
                    .setActivity(UserPhoneNumberVerification.this)
                    .setCallbacks(mCallbacks)
                    .build();

            PhoneAuthProvider.verifyPhoneNumber(options);

            btn_resend.setVisibility(View.GONE);
            tv_resend.setVisibility(View.GONE);

            tv_counter.setVisibility(View.VISIBLE);


            CountTimer();
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
                getOtp = s;
                Toast.makeText(UserPhoneNumberVerification.this, "OTP Send Successfully", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void CountTimer() {
        new CountDownTimer(60000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                long counter = millisUntilFinished / 1000;
                tv_counter.setText( counter + " Sec");
                if (counter<=15){
                    tv_counter.setTextColor(getResources().getColor(R.color.light_red));
                }else {
                    tv_counter.setTextColor(getResources().getColor(R.color.light_green));
                }
            }

            public void onFinish() {

                tv_counter.setVisibility(View.INVISIBLE);
                btn_resend.setVisibility(View.VISIBLE);
                tv_resend.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        /*                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), ShopSignUp.class));
                        finish();
                    }
                */

        AlertDialog.Builder builder = new AlertDialog.Builder(UserPhoneNumberVerification.this);
        builder.setMessage("Please connect to the internet")
                //.setCancelable(false)
                .setPositiveButton("Connect", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), UserSignUp.class));
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(UserPhoneNumberVerification verification) {

        ConnectivityManager connectivityManager = (ConnectivityManager) verification.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }

    private boolean validateOtp(){
        String val = et_otp.getText().toString().trim();

        if (val.isEmpty()){
            et_otp.setError("Field can not be empty");
            return false;
        }else {
            et_otp.setError(null);
            return true;
        }

    }


    private void storeNewUserData() {

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");

        String email = "";
        String address = "";

        UserData addNewUser = new UserData(name,phoneNumber,age,gender,password,location,email,address);
        reference.child(phoneNumber).child("Profile").setValue(addNewUser);
        reference.child(phoneNumber).child("phoneNumber").setValue(phoneNumber);

        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNumber").equalTo(phoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String _name = snapshot.child(phoneNumber).child("Profile").child("name").getValue(String.class);
                String _phoneNo = snapshot.child(phoneNumber).child("Profile").child("phoneNumber").getValue(String.class);
                String _password = snapshot.child(phoneNumber).child("Profile").child("password").getValue(String.class);

                managerCustomer.setCustomerLogin(true);
                managerCustomer.setDetails(_name, _phoneNo, _password);

                progressDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), UserDashBoard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
