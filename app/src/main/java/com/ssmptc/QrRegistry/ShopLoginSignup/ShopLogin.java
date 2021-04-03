package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ssmptc.QrRegistry.DataBase.SessionManager;
import com.ssmptc.QrRegistry.R;

public class ShopLogin extends AppCompatActivity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        sessionManager = new SessionManager(getApplicationContext());
        String sName = sessionManager.getName();

    }
}