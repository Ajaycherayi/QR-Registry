package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.UserDashBoard;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.R;

public class ShopLogin extends AppCompatActivity {

    SessionManagerShop managerShop;

    EditText et_shopId, et_phoneNumber, et_password;

    ImageView b2;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        et_shopId = findViewById(R.id.login_shopId);
        et_phoneNumber = findViewById(R.id.login_phone);
        et_password = findViewById(R.id.login_password);
        b2 = findViewById(R.id.btn_backToCd);


       managerShop = new SessionManagerShop(getApplicationContext());
        //String sName = sessionManager.getShopName();

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopLogin.this, UserDashBoard.class));
                finish();
            }
        });


    }

    public void login(View view) {


        String _shopId = et_shopId.getText().toString().trim();
        String _phoneNumber = et_phoneNumber.getText().toString().trim();
        String _password = et_password.getText().toString().trim();

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

                            if (systemPhone.equals(_completePhoneNumber)) {
                                et_password.setError(null);

                                if (systemPassword.equals(_password)) {
                                    et_password.setError(null);

                                    //Initialize ProgressDialog
                                    progressDialog = new ProgressDialog(ShopLogin.this);
                                    progressDialog.show();
                                    progressDialog.setContentView(R.layout.progress_dialog);
                                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                                    String _phoneNo = dataSnapshot.child("category").getValue(String.class);
                                    String _shopName = dataSnapshot.child("shopName").getValue(String.class);
                                    String _location = dataSnapshot.child("location").getValue(String.class);
                                    String _category = dataSnapshot.child("category").getValue(String.class);
                                    String _ownerName = dataSnapshot.child("ownerName").getValue(String.class);
                                    String _password = dataSnapshot.child("password").getValue(String.class);
                                    String _shopId = dataSnapshot.child("id").getValue(String.class);

                                    managerShop.setShopLogin(true);

                                    managerShop.setDetails(_shopId,_phoneNo, _shopName, _location ,_category,_ownerName, _password);



                                    startActivity(new Intent(getApplicationContext(), ShopDashBoard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();

                                } else {
                                    Toast.makeText(ShopLogin.this, "Password Doesn't Match!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ShopLogin.this, "Phone Number Incorrect!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                } else {
                   Toast.makeText(ShopLogin.this, "User Does Not Exist!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ShopLogin.this, "The User Does Not Exist", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void SignUp(View view) {
        startActivity(new Intent(ShopLogin.this,ShopSignup.class));
        finish();
    }
}