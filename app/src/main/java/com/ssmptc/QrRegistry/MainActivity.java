package com.ssmptc.QrRegistry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssmptc.QrRegistry.CustomerLoginSignUp.UserDashBoard;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.UserSignUp;
import com.ssmptc.QrRegistry.DataBase.SessionManagerCustomer;


public class MainActivity extends AppCompatActivity {

    Animation bottom,side;

    private static int SPLASH_TIMER = 3400;

    ImageView qr_img;
    TextView wcm,qr,powered;
    SessionManagerCustomer managerCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qr=findViewById(R.id.qr_txt);
        powered=findViewById(R.id.powered);

        //Splash Animation
        side= AnimationUtils.loadAnimation(this,R.anim.side_anim);
        bottom= AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        //Set The Animation
        //qr.setAnimation(bottom);
       // qr_img.setAnimation(bottom);
        powered.setAnimation(bottom);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

              //  startActivity( new Intent(MainActivity.this, ShopSignup.class));
                //Initialize SessionManager
                managerCustomer = new SessionManagerCustomer(getApplicationContext());

                if (managerCustomer.getCustomerLogin()){
                    startActivity(new Intent(getApplicationContext(), UserDashBoard.class));
                }else {
                    Intent intent = new Intent(MainActivity.this, UserSignUp.class);
                    startActivity(intent);
                }
                finish();

            }
        },SPLASH_TIMER);


    }
}