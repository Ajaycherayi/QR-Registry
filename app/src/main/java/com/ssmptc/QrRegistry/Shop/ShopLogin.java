package com.ssmptc.QrRegistry.Shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.User.UserDashBoard;
import com.ssmptc.QrRegistry.DataBase.Shop.SessionManagerShop;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.User.UserLogin;
import com.ssmptc.QrRegistry.User.UserSignUp;

import java.util.Objects;
public class ShopLogin extends AppCompatActivity {

    SessionManagerShop managerShop;
    ImageView btn_back;

    Button btn_login,btn_SignUp;

    private TextInputLayout et_shopId, et_phoneNumber, et_password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_login);

        et_shopId = findViewById(R.id.login_shopId);
        et_phoneNumber = findViewById(R.id.login_phone);
        et_password = findViewById(R.id.login_password);
        btn_back = findViewById(R.id.btn_backToCd);
        btn_login = findViewById(R.id.btn_login);
        btn_SignUp = findViewById(R.id.btn_SignUp);

       managerShop = new SessionManagerShop(getApplicationContext());

        if (isConnected(ShopLogin.this)){
            showCustomDialog();
        }

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(ShopLogin.this, UserDashBoard.class));
            finish();
        });

        btn_login.setOnClickListener(v -> login());

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShopSignUp.class);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String>(findViewById(R.id.btn_SignUp),"transition_shop_signUp");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ShopLogin.this,pairs);
                    startActivity(intent,options.toBundle());
                }
                else{
                    startActivity(intent);
                    finish();
                }
            }
        });




    }


    public void login() {

        if (isConnected(ShopLogin.this)){
            showCustomDialog();
        }



        if (!validateShopId() | !validatePhoneNumber() | !validatePassword()) {

            return;
        }

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopLogin.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String _shopId = Objects.requireNonNull(et_shopId.getEditText()).getText().toString().trim();
        String _phoneNumber = Objects.requireNonNull(et_phoneNumber.getEditText()).getText().toString().trim();
        String _password = Objects.requireNonNull(et_password.getEditText()).getText().toString().trim();

        if (_phoneNumber.charAt(0) == '0') {

            _phoneNumber = _phoneNumber.substring(1);
        }

        String _completePhoneNumber = "+91" + _phoneNumber;

        Query checkUser = FirebaseDatabase.getInstance().getReference("Shops").orderByChild("shopId").equalTo(_shopId);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    Query checkShop = FirebaseDatabase.getInstance().getReference("Shops").child(_shopId).child("Shop Profile");
                    checkShop.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String systemPassword = dataSnapshot.child("password").getValue(String.class);
                            String systemPhone = dataSnapshot.child("phoneNumber").getValue(String.class);

                            assert systemPhone != null;
                            if (systemPhone.equals(_completePhoneNumber)) {
                                et_password.setError(null);

                                assert systemPassword != null;
                                if (systemPassword.equals(_password)) {
                                    et_password.setError(null);
                                    
                                    String _shopName = dataSnapshot.child("shopName").getValue(String.class);
                                    String _password = dataSnapshot.child("password").getValue(String.class);
                                    String _shopId = dataSnapshot.child("id").getValue(String.class);

                                    managerShop.setShopLogin(true);

                                    managerShop.setDetails(_shopId, _shopName, _password);

                                    startActivity(new Intent(getApplicationContext(), ShopDashBoard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(ShopLogin.this, "Password Doesn't Match!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ShopLogin.this, "Phone Number Incorrect!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                } else {
                    progressDialog.dismiss();
                   Toast.makeText(ShopLogin.this, "User Does Not Exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(ShopLogin.this,UserDashBoard.class));
        finish();
        super.onBackPressed();
    }


    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ShopLogin.this);
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
    private boolean isConnected(ShopLogin shopLogin) {

        ConnectivityManager connectivityManager = (ConnectivityManager) shopLogin.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn == null || !wifiConn.isConnected()) && ((mobileConn == null || !mobileConn.isConnected()) && (bluetoothConn == null || !bluetoothConn.isConnected())); // if true ,  else false

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

    private boolean validateShopId(){
        String val = Objects.requireNonNull(et_shopId.getEditText()).getText().toString().trim();

        if (val.isEmpty()){
            et_shopId.setError("Field can not be empty");
            return false;
        }else if(val.length()>10){
            et_shopId.setError("Please Enter 10 Digit Phone Number");
            return false;
        }else if (!val.matches("\\w*")){
            et_shopId.setError("White spaces not allowed");
            return false;
        }else {
            et_shopId.setError(null);
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

}