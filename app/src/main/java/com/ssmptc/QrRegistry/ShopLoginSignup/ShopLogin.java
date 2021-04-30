package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.CustomerDashBoard;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.R;

public class ShopLogin extends AppCompatActivity {

    SessionManagerShop sessionManager;

    EditText et_category, et_phoneNumber, et_password;
    Button b1,b2;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        et_category = findViewById(R.id.login_category);
        et_phoneNumber = findViewById(R.id.login_phone);
        et_password = findViewById(R.id.login_password);

        b1 = findViewById(R.id.bt_login);
        b2 = findViewById(R.id.bt_callSignUp);

       sessionManager = new SessionManagerShop(getApplicationContext());
        //String sName = sessionManager.getShopName();

    }

    public void login(View view) {

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopLogin.this);

        progressDialog.show();

        progressDialog.setContentView(R.layout.progress_dialog);

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String _category = et_category.getText().toString().trim();
        String _phoneNumber = et_phoneNumber.getText().toString().trim();
        String _password = et_password.getText().toString().trim();

        if (_phoneNumber.charAt(0) == '0') {

            _phoneNumber = _phoneNumber.substring(1);
        }

        String _completePhoneNumber = "+91" + _phoneNumber;

        //Database
        Query checkUser = FirebaseDatabase.getInstance().getReference("Shops").orderByChild("phoneNumber").equalTo(_completePhoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    et_phoneNumber.setError(null);
                    String shopCategory = snapshot.child(_completePhoneNumber).child("category").getValue(String.class);
                    String systemPassword = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);
                    if (shopCategory.equals(_category)) {
                        et_category.setError(null);

                        if (systemPassword.equals(_password)) {
                            et_password.setError(null);


                            String _phoneNo = snapshot.child(_completePhoneNumber).child("category").getValue(String.class);
                            String _shopName = snapshot.child(_completePhoneNumber).child("shopName").getValue(String.class);
                            String _location = snapshot.child(_completePhoneNumber).child("location").getValue(String.class);
                            String _category = snapshot.child(_completePhoneNumber).child("category").getValue(String.class);
                            String _ownerName = snapshot.child(_completePhoneNumber).child("ownerName").getValue(String.class);
                            String _password = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);


                            sessionManager.setShopLogin(true);

                           sessionManager.setDetails(_phoneNo, _shopName, _location ,_category,_ownerName, _password);

                            startActivity(new Intent(getApplicationContext(), ShopDashBoard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();


                            //   Toast.makeText(CustomerLogin.this, "Hai", Toast.LENGTH_SHORT).show();
                            //  Intent intent = new Intent(CustomerLogin.this, QRCodeGeneration.class);
                            //  intent.putExtra("name",_name);
                            // intent.putExtra("email",_email);
                            //  intent.putExtra("phoneNo",_phoneNo);
                            // startActivity(intent);

                        } else {
                            Toast.makeText(ShopLogin.this, "Password Doesn't Match!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ShopLogin.this, "User Does Not Exist!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

}