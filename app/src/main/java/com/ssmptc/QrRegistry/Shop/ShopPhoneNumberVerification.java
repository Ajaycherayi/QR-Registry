package com.ssmptc.QrRegistry.Shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.ssmptc.QrRegistry.DataBase.Shop.SessionManagerShop;
import com.ssmptc.QrRegistry.DataBase.Shop.ShopsData;
import com.ssmptc.QrRegistry.R;

import java.util.concurrent.TimeUnit;

public class ShopPhoneNumberVerification extends AppCompatActivity {

    Button btn_signUp;
    TextView tv_phoneNo;
    ImageView btn_back;

    SessionManagerShop managerShop;

    private TextView btn_resend,tv_counter,tv_resend;
    private EditText et_otp;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String getOtp,phoneNumber,shopName,location,category,ownerName,password;
    private long node = 0;
    private String nodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_phone_number_verification);

        et_otp = findViewById(R.id.et_otp);

        btn_back = findViewById(R.id.btn_back);
        btn_signUp = findViewById(R.id.btn_signUp);
        btn_resend = findViewById(R.id.btn_resend);

        tv_counter = findViewById(R.id.tv_counter);
        tv_resend = findViewById(R.id.tv_resend);
        tv_phoneNo = findViewById(R.id.tv_phoneNo);

        getOtp = getIntent().getStringExtra("otp");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        shopName = getIntent().getStringExtra("shopName");
        location = getIntent().getStringExtra("location");
        category = getIntent().getStringExtra("category");
        ownerName = getIntent().getStringExtra("ownerName");
        password = getIntent().getStringExtra("password");

        btn_resend.setVisibility(View.INVISIBLE);
        tv_resend.setVisibility(View.INVISIBLE);

        if (!isConnected(ShopPhoneNumberVerification.this)){
            showCustomDialog();
        }

        managerShop = new SessionManagerShop(getApplicationContext());

        firebaseAuth = FirebaseAuth.getInstance();

        String mobile = phoneNumber;
        mobile = mobile.substring(0, 3) + "*****" + mobile.substring(9);
        tv_phoneNo.setText(mobile);

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(ShopPhoneNumberVerification.this, ShopSignUp.class));
            finish();
        });

        CountTimer();


        //**********************************************************************************//
            DatabaseReference genNode;
            genNode = FirebaseDatabase.getInstance().getReference().child("Shops");
            genNode.addListenerForSingleValueEvent(new ValueEventListener() {
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


        btn_signUp.setOnClickListener(v -> {


            if (!validateOtp()) {
                return;
            }

            //Initialize ProgressDialog
            progressDialog = new ProgressDialog(ShopPhoneNumberVerification.this);
            progressDialog.show();
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

            if(getOtp != null){
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(getOtp,enteredOtp);
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        nodeId = String.valueOf(node+1000);

                        Dialog dialog = new Dialog(ShopPhoneNumberVerification.this);
                        dialog.setContentView(R.layout.id_alert_dialog);
                        CheckBox checkBox = dialog.findViewById(R.id.check_box);
                        Button btCancel = dialog.findViewById(R.id.bt_cancel);
                        Button btOk = dialog.findViewById(R.id.bt_ok);
                        TextView shopId = dialog.findViewById(R.id.tv_shopId);

                        shopId.setText(String.valueOf(node+1000));

                        shopId.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Shop Id",String.valueOf(node+1000));
                                Toast.makeText(ShopPhoneNumberVerification.this, "Copied", Toast.LENGTH_SHORT).show();
                                clipboard.setPrimaryClip(clip);
                            }
                        });
                        btCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                                progressDialog.dismiss();
                            }
                        });
                        btOk.setOnClickListener(v1 -> dialog.dismiss());
                        checkBox.setOnClickListener(v1 -> {
                            if (checkBox.isChecked()){

                                btOk.setBackgroundColor(getResources().getColor(R.color.light_green));
                                btOk.setEnabled(true);
                                btOk.setOnClickListener(v11 -> {


                                    DatabaseReference rff;
                                    rff = FirebaseDatabase.getInstance().getReference().child("Shops");

                                    String email = "";
                                    String description = "";
                                    String workingTime = "";
                                    String workingDays = "";

                                    rff.child(String.valueOf(node+1000)).child("shopId").setValue(String.valueOf(node+1000));
                                    ShopsData addNewShop = new ShopsData(nodeId,shopName,category,location,ownerName,phoneNumber,password,email,description,workingTime,workingDays);
                                    rff.child(String.valueOf(node+1000)).child("Shop Profile").setValue(addNewShop);

                                    Query shopData = FirebaseDatabase.getInstance().getReference("Shops").child(nodeId).child("Shop Profile");

                                    shopData.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshotData) {

                                            String _shopName = snapshotData.child("shopName").getValue(String.class);
                                            String _password = snapshotData.child("password").getValue(String.class);
                                            String _shopId = snapshotData.child("id").getValue(String.class);

                                            managerShop.setShopLogin(true);
                                            managerShop.setDetails(_shopId, _shopName, _password);

                                            progressDialog.dismiss();

                                            Intent intent = new Intent(getApplicationContext(),ShopDashBoard.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                    });

                                    dialog.dismiss();

                                });

                            }else{
                                btOk.setEnabled(false);
                                btOk.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_disabled));

                            }
                        });

                        dialog.show();

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(ShopPhoneNumberVerification.this, "Enter The Correct OTP", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        btn_resend.setOnClickListener(v -> {

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L,TimeUnit.SECONDS) //Time Out Set
                    .setActivity(ShopPhoneNumberVerification.this)
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
                Toast.makeText(ShopPhoneNumberVerification.this, "OTP Send Successfully", Toast.LENGTH_SHORT).show();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(ShopPhoneNumberVerification.this);
        builder.setMessage("Please connect to the internet")
                //.setCancelable(false)
                .setPositiveButton("Connect", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), ShopSignUp.class));
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(ShopPhoneNumberVerification verification) {

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

}