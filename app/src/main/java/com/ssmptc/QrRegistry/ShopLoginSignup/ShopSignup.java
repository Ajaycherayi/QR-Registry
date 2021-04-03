package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ssmptc.QrRegistry.DataBase.SessionManager;
import com.ssmptc.QrRegistry.R;

public class ShopSignup extends AppCompatActivity {
    
    SessionManager sessionManager;
    EditText Password,name;
    TextView phoneNo;
    Button shopLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_signup);

        name = findViewById(R.id.et_ownerName);
        Password = findViewById(R.id.et_shopPassword);
        phoneNo = findViewById(R.id.tv_ownerPhone);
        shopLogin = findViewById(R.id.btn_goShopLogin);

        sessionManager = new SessionManager(getApplicationContext());
        String ownerName = sessionManager.getName();
        String ownerPhone = sessionManager.getPhone();
        String ownerPassword = sessionManager.getPassword();

        name.setText(ownerName);
        Password.setText(ownerPassword);
        phoneNo.setText(ownerPhone);

        shopLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ShopDashBoard.class));
            }
        });
    }
}