package com.ssmptc.QrRegistry.CustomerLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssmptc.QrRegistry.DataBase.SessionManager;
import com.ssmptc.QrRegistry.Home;
import com.ssmptc.QrRegistry.R;

import java.util.HashMap;

public class CustomerDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    static final float END_SCALE = 0.7f;

    boolean isCheckedDone = false;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    TextView user_Name;

    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dash_board);

        LottieAnimationView lottieAnimationView1 = findViewById(R.id.drawer_btn);
        //lottieAnimationView1.setSpeed(3f);



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.linear_content);
        user_Name = findViewById(R.id.get_name);


        // HashMap<String,String> userDetails = sessionManager.getUserDetailsFromSession();
        sessionManager = new SessionManager(getApplicationContext());
        String sName = sessionManager.getName();
        user_Name.setText(sName);

        navigationDrawer();

       // SessionManager sessionManager = new SessionManager(this);
        //HashMap<String,String> userDetails = sessionManager.getUserDetailsFromSession();


        //Animation set onclick
        lottieAnimationView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    lottieAnimationView1.playAnimation();
                    lottieAnimationView1.loop(true);

                    if (drawerLayout.isDrawerVisible(GravityCompat.START))
                        drawerLayout.closeDrawer(GravityCompat.START);
                    else drawerLayout.openDrawer(GravityCompat.START);
            }

        });

    }

    // Navigation Drawer Functions
    private void navigationDrawer() {

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener( this);
        navigationView.setCheckedItem(R.id.nav_home);



        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {

        //Add any color or remove it to use the default one!
       //drawerLayout.setScrimColor(getResources().getColor(R.color.red));
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
            super.onBackPressed();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.nav_Switch:
                Toast.makeText(getApplicationContext(), "Switch", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.profile:
                Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.details:
                Toast.makeText(getApplicationContext(), "Details", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logout:
                logout(this);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void logout(Activity activity) {

        sessionManager = new SessionManager(getApplicationContext());

        //Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Set Title
        builder.setTitle("Log out");

        //set Message
        builder.setMessage("Are you sure to Log out ?");

        //positive YES button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

              sessionManager.setLogin(false);
              sessionManager.setName("");
                //activity.finishAffinity();
                dialog.dismiss();

                //Finish Activity
                startActivity(new Intent(getApplicationContext(), Home.class));
               finish();
            }
        });

        //Negative NO button
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss Dialog
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }



}