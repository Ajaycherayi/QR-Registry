package com.ssmptc.QrRegistry.CustomerLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.ssmptc.QrRegistry.R;

public class CustomerLogin extends AppCompatActivity {

    EditText phoneNumber, password;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);


        phoneNumber = findViewById(R.id.log_phone);
        password = findViewById(R.id.log_password);
        b1 = findViewById(R.id.bt_login);


    }

    public void login(View view) {
        String _phoneNumber = phoneNumber.getText().toString().trim();
        String _password = password.getText().toString().trim();

        if (_phoneNumber.charAt(0) == '0') {

            _phoneNumber = _phoneNumber.substring(1);
        }

        String _completePhoneNumber = "+91" + _phoneNumber;

        //Database
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(_completePhoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    phoneNumber.setError(null);


                    String systemPassword = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);

                    if (systemPassword.equals(_password)) {
                        password.setError(null);


                        String _name = snapshot.child(_completePhoneNumber).child("name").getValue(String.class);
                        String _email = snapshot.child(_completePhoneNumber).child("email").getValue(String.class);
                        String _phoneNo = snapshot.child(_completePhoneNumber).child("phoneNo").getValue(String.class);

                        Toast.makeText(CustomerLogin.this, "Hai", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CustomerLogin.this, QRCodeGeneration.class);
                        intent.putExtra("name",_name);
                        intent.putExtra("email",_email);
                        intent.putExtra("phoneNo",_phoneNo);
                        startActivity(intent);

                    } else {
                        Toast.makeText(CustomerLogin.this, "Password Doesn't Match!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CustomerLogin.this, "User Does Not Exist!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerLogin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

