package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
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
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.DataBase.ShopsData;
import com.ssmptc.QrRegistry.R;

import java.util.concurrent.TimeUnit;

public class ShopPhoneVerification extends AppCompatActivity {

     String getOtp,phoneNumber,shopName,location,category,ownerName,password;
     Button btn_signUp;
     EditText et_otp;
     TextView resendLabel,tv_counter,tv_resend,tv_phoneNo;

     FirebaseAuth firebaseAuth;
     PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    SessionManagerShop managerShop;

    long node = 0;
    private String nodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_phone_verification);

        et_otp = findViewById(R.id.et_otp);
        btn_signUp = findViewById(R.id.btn_signUp);

        tv_counter = findViewById(R.id.tv_counter);
        tv_resend = findViewById(R.id.tv_resend);
        tv_phoneNo = findViewById(R.id.tv_phoneNo);

        resendLabel = findViewById(R.id.btn_resend);
        getOtp = getIntent().getStringExtra("otp");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        shopName = getIntent().getStringExtra("shopName");
        location = getIntent().getStringExtra("location");
        category = getIntent().getStringExtra("category");
        ownerName = getIntent().getStringExtra("ownerName");
        password = getIntent().getStringExtra("password");

        resendLabel.setVisibility(View.INVISIBLE);
        tv_resend.setVisibility(View.INVISIBLE);

        managerShop = new SessionManagerShop(getApplicationContext());



        firebaseAuth = FirebaseAuth.getInstance();

        String mobile = phoneNumber;
        mobile = mobile.substring(0, 3) + "*****" + mobile.substring(9, mobile.length());
        tv_phoneNo.setText(mobile);

        CountTimer();



            //**********************************************************************************//
            DatabaseReference rff;
            rff = FirebaseDatabase.getInstance().getReference().child("Shops");
            rff.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        node = (snapshot.getChildrenCount());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        //**********************************************************************************//



        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredOtp = et_otp.getText().toString();

                if(getOtp != null){
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(getOtp,enteredOtp);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        nodeId = String.valueOf(node+1000);

                                        Dialog dialog = new Dialog(ShopPhoneVerification.this);
                                        dialog.setContentView(R.layout.alert_dialog);
                                        CheckBox checkBox = dialog.findViewById(R.id.check_box);
                                        Button btCancel = dialog.findViewById(R.id.bt_cancel);
                                        Button btOk = dialog.findViewById(R.id.bt_ok);
                                        TextView shopId = dialog.findViewById(R.id.tv_shopId);

                                        shopId.setText(nodeId);

                                        btCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.cancel();
                                            }
                                        });
                                        btOk.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                        checkBox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (checkBox.isChecked()){

                                                    btOk.setBackgroundColor(getResources().getColor(R.color.light_green));
                                                    btOk.setEnabled(true);
                                                    btOk.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            DatabaseReference rff;
                                                            rff = FirebaseDatabase.getInstance().getReference().child("Shops");

                                                            rff.child(String.valueOf(node+1000)).child("shopId").setValue(nodeId);
                                                            ShopsData addNewShop = new ShopsData(nodeId,phoneNumber, shopName, location, category, ownerName, password);
                                                            rff.child(String.valueOf(node+1000)).child("Shop Profile").setValue(addNewShop);
                                                            rff.child(String.valueOf(node+1000)).child("Shop Profile").child("licenseNumber").setValue("");
                                                            rff.child(String.valueOf(node+1000)).child("Shop Profile").child("email").setValue("");
                                                            rff.child(String.valueOf(node+1000)).child("Shop Profile").child("description").setValue("");
                                                            rff.child(String.valueOf(node+1000)).child("Shop Profile").child("working time").setValue("open time-close time");
                                                            rff.child(String.valueOf(node+1000)).child("Shop Profile").child("working days").setValue("day-day");


                                                            Query shopData = FirebaseDatabase.getInstance().getReference("Shops").child(nodeId).child("Shop Profile");

                                                            shopData.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshotData) {

                                                                    String _shopName = snapshotData.child("shopName").getValue(String.class);
                                                                    String _category = snapshotData.child("category").getValue(String.class);
                                                                    String _location = snapshotData.child("location").getValue(String.class);
                                                                    String _phoneNo = snapshotData.child("phoneNumber").getValue(String.class);
                                                                    String _ownerName = snapshotData.child("ownerName").getValue(String.class);
                                                                    String _password = snapshotData.child("password").getValue(String.class);
                                                                    String _shopId = snapshotData.child("id").getValue(String.class);

                                                                    managerShop.setShopLogin(true);
                                                                    managerShop.setDetails(_shopId,_phoneNo, _shopName, _location, _category, _ownerName, _password);

                                                                    Intent intent = new Intent(getApplicationContext(),ShopDashBoard.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intent);

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                            dialog.dismiss();
                                                        }
                                                    });

                                                }else {
                                                    btOk.setEnabled(false);
                                                    btOk.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                                                }
                                            }
                                        });
                                        dialog.show();

                                    }else {
                                        Toast.makeText(ShopPhoneVerification.this, "Enter The Correct OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

        resendLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L,TimeUnit.SECONDS) //Time Out Set
                        .setActivity(ShopPhoneVerification.this)
                        .setCallbacks(mCallbacks)
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(options);

                CountTimer();

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
                getOtp = s;
                Toast.makeText(ShopPhoneVerification.this, "OTP Send Successfully", Toast.LENGTH_SHORT).show();
            }
        };




    }

    private void CountTimer() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                long counter = millisUntilFinished / 1000;
                tv_counter.setText( counter + " Sec");
                if (counter<15){
                    tv_counter.setTextColor(getResources().getColor(R.color.light_red));
                }
            }

            public void onFinish() {

                tv_counter.setVisibility(View.INVISIBLE);
                resendLabel.setVisibility(View.VISIBLE);
                tv_resend.setVisibility(View.VISIBLE);
            }

        }.start();
    }
}