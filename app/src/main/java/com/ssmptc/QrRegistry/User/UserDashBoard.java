package com.ssmptc.QrRegistry.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssmptc.QrRegistry.AboutQrRegistry;
import com.ssmptc.QrRegistry.ContactUs;
import com.ssmptc.QrRegistry.DataBase.User.SessionManagerUser;
import com.ssmptc.QrRegistry.DataBase.Shop.SessionManagerShop;
import com.ssmptc.QrRegistry.QRCodeScanner;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.R.layout;
import com.ssmptc.QrRegistry.Shop.ShopDashBoard;
import com.ssmptc.QrRegistry.Shop.ShopLogin;
import com.ssmptc.QrRegistry.ToDoList.UserToDoList;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class UserDashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final float END_SCALE = 0.7f;

    String AES = "AES";
    String keyPass = "qrregistry@shop";

    MenuItem  menuItem;

    MaterialCardView btn_CustomerProfile,btn_TodoList,btn_mapFind,btn_ScannedShops,btn_scanQR,btn_generateQR;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    TextView user_Name;
    View nav_headerView;

    TextView tv_date;
    TextClock tv_time;


    String phoneNo,currentDate = new SimpleDateFormat("d-MMM-yyyy", Locale.getDefault()).format(new Date());
    String view_date= new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date());
    SessionManagerUser managerCustomer;
    SessionManagerShop managerShop;

    private String sName,ShopData,sCategory,sOwnerName,sLocation,sPhoneNumber,sEmail,sDays, sTime,sDescription,sImages;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.user_dash_board);

        LottieAnimationView lottieAnimationView1 = findViewById(R.id.drawer_btn);
        //lottieAnimationView1.setSpeed(3f);



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.linear_content);
        user_Name = findViewById(R.id.get_name);

        btn_CustomerProfile = findViewById(R.id.btn_CustomerProfile);
        btn_TodoList = findViewById(R.id.btn_TodoList);
        btn_mapFind = findViewById(R.id.btn_mapFind);
        btn_ScannedShops = findViewById(R.id.btn_ScannedShops);
        btn_scanQR = findViewById(R.id.btn_scanQR);
        btn_generateQR = findViewById(R.id.btn_generateQR);

        Menu menuNav = navigationView.getMenu();
        MenuItem nav_shop = menuNav.findItem(R.id.nav_shop);

        nav_headerView = navigationView.inflateHeaderView(layout.menu_header);
        tv_date = nav_headerView.findViewById(R.id.tv_date);
        tv_time = nav_headerView.findViewById(R.id.tv_time);
        tv_date.setText(view_date);
        tv_time.setFormat12Hour("hh:mm:ss a");
        tv_time.setFormat24Hour(null);

        managerCustomer = new SessionManagerUser(getApplicationContext());
        String sName = managerCustomer.getName();
        user_Name.setText(sName);

        navigationDrawer();

        nav_shop.setVisible(!managerCustomer.getShopButton());

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

        btn_CustomerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashBoard.this, EditUserProfile.class));
            }
        });

        btn_TodoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashBoard.this, UserToDoList.class));
            }
        });

        btn_mapFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashBoard.this, FindShops.class));
            }
        });

        btn_ScannedShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ShopDetails.class));
            }
        });

        btn_scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--------------- Internet Checking -----------
                if (!isConnected(UserDashBoard.this)){
                    showCustomDialog();
                }else {
                    scanCode();
                }
            }
        });

        btn_generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserQrCode.class));
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
                Toast.makeText(getApplicationContext(), "Shop", Toast.LENGTH_SHORT).show();
                shopLogin();
                break;

            case R.id.nav_home:
                Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_contactUs:
                Toast.makeText(getApplicationContext(), "Contact Us", Toast.LENGTH_SHORT).show();
                contactUs();
                break;

            case R.id.nav_share:
                Toast.makeText(getApplicationContext(), "Share", Toast.LENGTH_SHORT).show();
                share();
                break;

            case R.id.nav_about:
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                about();
                break;

            case R.id.logout:
                logout();
                break;

            case R.id.exit:
                finishAffinity();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
    private void share() {

        try {

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT,"QR Registry");
            i.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
            startActivity(Intent.createChooser(i,"Share With"));

        }catch (Exception e){
            Toast.makeText(this, "Unable to share this app.", Toast.LENGTH_SHORT).show();
        }


    }

    private void contactUs() {
        startActivity(new Intent(getApplicationContext(), ContactUs.class));
    }

    private void about() {
        startActivity(new Intent(getApplicationContext(), AboutQrRegistry.class));
    }


    private void shopLogin() {

        managerCustomer = new SessionManagerUser(getApplicationContext());
        managerShop = new SessionManagerShop(getApplicationContext());

        if (managerShop.getShopLogin()) {
            startActivity(new Intent(getApplicationContext(), ShopDashBoard.class));
        } else {

            Dialog dialog = new Dialog(UserDashBoard.this);
            dialog.setContentView(layout.login_alert);
            Button btCancel = dialog.findViewById(R.id.bt_cancel);
            Button btOk = dialog.findViewById(R.id.bt_ok);
            Button btn_disable = dialog.findViewById(R.id.btn_disable);

            btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(UserDashBoard.this, ShopLogin.class));
                    dialog.dismiss();
                }
            });
            btn_disable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Menu menuNav = navigationView.getMenu();
                    MenuItem nav_shop = menuNav.findItem(R.id.nav_shop);
                    managerCustomer.setShopButton(true);
                    nav_shop.setVisible(false);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void logout() {

        managerCustomer = new SessionManagerUser(getApplicationContext());
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
              managerCustomer.setShopButton(false);
              managerCustomer.setDetails("","","");

              managerShop.setShopLogin(false);
              managerShop.setDetails("","","");

                //activity.finishAffinity();
                dialog.dismiss();

                //Finish Activity
                startActivity(new Intent(getApplicationContext(), UserSignUp.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

    private void scanCode() {

            //Initialize intent integrator
            IntentIntegrator intentIntegrator = new IntentIntegrator(UserDashBoard.this);

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
                    ShopData = (String) decrypt(shopDetails);

                    String[] separated0 = ShopData.split(":");

                    String sId = separated0[0];
                    String phoneNumber = separated0[1];

                    Query checkData = FirebaseDatabase.getInstance().getReference("Users").child(phoneNo).child("Shops").orderByChild("shopId").equalTo(sId);
                    checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){

                                Toast.makeText(UserDashBoard.this, "This Shop Data Already Exist", Toast.LENGTH_SHORT).show();

                            }else {
                                String id = reference.push().getKey();
                                if (id != null){
                                    reference.child(id).child("shopId").setValue(sId);
                                    reference.child(id).child("phoneNumber").setValue(phoneNumber);
                                    reference.child(id).child("shopName").setValue(shopName);
                                    reference.child(id).child("id").setValue(id);
                                }
                                Toast.makeText(UserDashBoard.this, "Added New Shop Data", Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(UserDashBoard.this);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(UserDashBoard.this);
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

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UserDashBoard.this);
        builder.setMessage("Please connect to the internet")
                //.setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),UserDashBoard.class));
                        finish();
                    }
                });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(UserDashBoard userDashBoard) {

        ConnectivityManager connectivityManager = (ConnectivityManager) userDashBoard.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }


}