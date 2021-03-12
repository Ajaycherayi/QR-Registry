package com.ssmptc.QrRegistry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.ssmptc.QrRegistry.CustomerLoginSignup.CustomerSignup;
import com.ssmptc.QrRegistry.ShopLoginSignup.ShopSignup;

public class Home extends AppCompatActivity {

    //For Animation
    boolean isCheckedDone = false;

    RelativeLayout shop_layout,customer_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Animation Variable initialization
        LottieAnimationView lottieAnimationView1 = findViewById(R.id.shop_animation);
        lottieAnimationView1.loop(true);
        LottieAnimationView lottieAnimationView2 = findViewById(R.id.customer_animation);
        lottieAnimationView2.loop(true);

        //Layout Variable initialization
        shop_layout = findViewById(R.id.shop_card);
        customer_layout = findViewById(R.id.customer_card);


        //Intent For Next Activity
        shop_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Home.this, ShopSignup.class);
                startActivity(intent1);
            }
        });

        customer_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Home.this, CustomerSignup.class);
                startActivity(intent2);
            }
        });


    }
}