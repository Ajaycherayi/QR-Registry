package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ssmptc.QrRegistry.R;

public class CustomerProfile extends AppCompatActivity {

    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        btn_back = findViewById(R.id.btn_backToCd);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerProfile.this,CustomerDashBoard.class));
                finish();
            }
        });

    }

    public void updateData(View view) {
    }
}