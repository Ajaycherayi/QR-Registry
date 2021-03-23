package com.ssmptc.QrRegistry.CustomerLoginSignup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssmptc.QrRegistry.DataBase.SessionManager;
import com.ssmptc.QrRegistry.DataBase.ShopHelperClass;
import com.ssmptc.QrRegistry.DataBase.UserHelperClass;
import com.ssmptc.QrRegistry.Home;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.ShopLoginSignup.QRCodeScanner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CustomerDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    static final float END_SCALE = 0.7f;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    TextView user_Name;

    String name,email,phoneNo,currentDate,currentTime;
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
                logout();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void logout() {

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
              sessionManager.setDetails("","","","");
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


    public void generateQR(View view) {
        startActivity(new Intent(getApplicationContext(),QRCodeGeneration.class));
    }

    public void scanQR(View view) {
        //Initialize intent integrator
        IntentIntegrator intentIntegrator = new IntentIntegrator(CustomerDashBoard.this);

        //Set Prompt text
        intentIntegrator.setPrompt("For Flash Use Volume Up Key");

        //set beep
        intentIntegrator.setBeepEnabled(true);

        //Locked Orientation
        intentIntegrator.setOrientationLocked(true);

        //Set Capture Activity
        intentIntegrator.setCaptureActivity(QRCodeScanner.class);

        //Initiate Scan
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       //Initiate Intent Result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("New");

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String output = intentResult.getContents();

        String[] separated = output.split(":");



        currentDate = new SimpleDateFormat("d-MMM-yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        name = separated[0];
        email= separated[1];
        phoneNo = separated[2];


        ShopHelperClass addNewUser = new ShopHelperClass(name,email,phoneNo,currentDate,currentTime);

        reference.child(currentDate).child(currentTime).child(phoneNo).setValue(addNewUser);


        //Check Condition
        if(intentResult.getContents() != null){

            //Initialize Dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDashBoard.this);

            //Set Title
            builder.setTitle("Result");

            //Set Message
            builder.setMessage("Read Successfully");

            //set Positive Button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            //Show Alert Dialog
            builder.show();
        }else {
            Toast.makeText(getApplicationContext(), "OOPS... You did Not Scan Anything", Toast.LENGTH_SHORT).show();
        }



    }
}