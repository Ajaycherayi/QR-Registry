package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.util.AndroidUtilsLight;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssmptc.QrRegistry.DataBase.SessionManagerCustomer;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.DataBase.ShopsDataForCustomers;
import com.ssmptc.QrRegistry.QRCodeScanner;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.ShopLoginSignup.ShopDashBoard;
import com.ssmptc.QrRegistry.ShopLoginSignup.ShopLogin;
import com.ssmptc.QrRegistry.ToDoList.CustomerToDoList;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

 

public class CustomerDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final float END_SCALE = 0.7f;

    String AES = "AES";
    private String keyPass = "qrregistry@shop";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    TextView user_Name;

    String phoneNo,currentDate = new SimpleDateFormat("d-MMM-yyyy", Locale.getDefault()).format(new Date());;
    SessionManagerCustomer managerCustomer;
    SessionManagerShop managerShop;

    private String sName,sId,sCategory,sOwnerName,sLocation,sPhoneNumber,sEmail,sDays, sTime,sDescription,sImages;

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

        managerCustomer = new SessionManagerCustomer(getApplicationContext());
        String sName = managerCustomer.getName();
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
        navigationView.setNavigationItemSelectedListener(this);
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

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else
            super.onBackPressed();
    }

    @SuppressLint("NonConstantResourceId")
    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.nav_shop:
                Toast.makeText(getApplicationContext(), "Switch", Toast.LENGTH_SHORT).show();
                shopLogin();
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

    private void shopLogin() {

        managerCustomer = new SessionManagerCustomer(getApplicationContext());
        managerShop = new SessionManagerShop(getApplicationContext());
        String nPhone = managerCustomer.getPhone();

        if (managerShop.getShopLogin()) {
            startActivity(new Intent(getApplicationContext(), ShopDashBoard.class));
        } else {

            Dialog dialog = new Dialog(CustomerDashBoard.this);
            dialog.setContentView(R.layout.login_alert);
            Button btCancel = dialog.findViewById(R.id.bt_cancel);
            Button btOk = dialog.findViewById(R.id.bt_ok);

            btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(CustomerDashBoard.this, ShopLogin.class));
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void logout() {

        managerCustomer = new SessionManagerCustomer(getApplicationContext());
        managerShop = new SessionManagerShop(getApplicationContext());

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

              managerCustomer.setCustomerLogin(false);
              managerCustomer.setDetails("","","","");

              managerShop.setShopLogin(false);
              managerShop.setDetails("","","","","","","");

                //activity.finishAffinity();
                dialog.dismiss();

                //Finish Activity
                startActivity(new Intent(getApplicationContext(), CustomerSignup.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
        startActivity(new Intent(getApplicationContext(), QRCodeGeneration.class));
    }

    public void scanQR(View view) {
        scanCode();
    }

    private void scanCode() {
        //Initialize intent integrator
        IntentIntegrator intentIntegrator = new IntentIntegrator(CustomerDashBoard.this);

        intentIntegrator.setPrompt("For Flash Use Volume Up Key"); //Set Prompt text
        intentIntegrator.setCameraId(0); //set Camera
        intentIntegrator.setBeepEnabled(true); //set beep
        intentIntegrator.setOrientationLocked(true);    //Locked Orientation
        intentIntegrator.setCaptureActivity(QRCodeScanner.class);   //Set Capture Activity
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.initiateScan();    //Initiate Scan

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Initiate Intent Result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

//Check Condition
        if (intentResult.getContents() != null && resultCode == RESULT_OK) {

            managerShop = new SessionManagerShop(getApplicationContext());
            phoneNo  = managerCustomer.getPhone();

            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            DatabaseReference reference = rootNode.getReference("Users").child(phoneNo).child("Shops");

            //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            String output = intentResult.getContents();



            if (output.startsWith("QrRegistryShop")) {
                String[] separated = output.split(":");

                String shopName = separated[1];
                String shopDetails = separated[2];

                try {
                    sId = (String) decrypt(shopDetails);

                    Query checkData = FirebaseDatabase.getInstance().getReference("Users").child(phoneNo).child("Shops").orderByChild("shopId").equalTo(sId);
                    checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){

                                Toast.makeText(CustomerDashBoard.this, "This Shop Data Already Exist", Toast.LENGTH_SHORT).show();

                            }else {
                                String id = reference.push().getKey();
                                if (id != null){
                                    reference.child(id).child("shopId").setValue(sId);
                                    reference.child(id).child("shopName").setValue(shopName);
                                    reference.child(id).child("id").setValue(id);
                                }
                                Toast.makeText(CustomerDashBoard.this, "Added New Shop Data", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                } catch (Exception e) {
                    Toast.makeText(this, "Wrong Key", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                //Initialize Dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDashBoard.this);

                //Set Title
                builder.setTitle("Result");

                //Set Message
                builder.setMessage("Read Successfully");

                //set Positive Button
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
                    }
                }).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //Show Alert Dialog
                builder.show();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDashBoard.this);
                builder.setMessage("Wrong QR Code");
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
                    }
                });
                builder.show();

            }
        } else {

            Toast.makeText(getApplicationContext(), "OOPS... You did Not Scan Anything", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Object decrypt(String shopDetails) throws Exception {

        SecretKeySpec key = generateKey(keyPass);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodeValue = android.util.Base64.decode(shopDetails,android.util.Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        return new String(decValue);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private SecretKeySpec generateKey(String keyPass) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = keyPass.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key,"AES");
    }

    public void ScannedShops(View view) {
        startActivity(new Intent(getApplicationContext(),ShopDetails.class));
    }

    public void mapFind(View view) {
        startActivity(new Intent(CustomerDashBoard.this,CustomerMapFind.class));

    }

    public void TodoList(View view) {
        startActivity(new Intent(CustomerDashBoard.this, CustomerToDoList.class));

    }

    public void CustomerProfile(View view) {
        startActivity(new Intent(CustomerDashBoard.this, CustomerProfile.class));

    }
}