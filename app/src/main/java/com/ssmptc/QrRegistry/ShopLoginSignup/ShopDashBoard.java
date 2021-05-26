package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.UserDashBoard;
import com.ssmptc.QrRegistry.DataBase.CustomersDataForShops;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.QRCodeScanner;
import com.ssmptc.QrRegistry.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShopDashBoard extends AppCompatActivity {

    TextView textView;
    ImageView btn_back;

    String shopId;
    String name;
    String email;
    String phoneNo;
    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());;
    String currentTime = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
    SessionManagerShop managerShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_dash_board);

        textView = findViewById(R.id.show_name);
        btn_back = findViewById(R.id.btn_back);

        //String shopName = getIntent().getStringExtra("shopName");
        //textView.setText(shopName);

        managerShop = new SessionManagerShop(getApplicationContext());
        String sName = managerShop.getShopName();
        textView.setText(sName);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopDashBoard.this, UserDashBoard.class));
                finish();
            }
        });

    }



    public void ScannerForShop(View view) {

      scanCode();
    }

    private void scanCode() {
        //Initialize intent integrator
        IntentIntegrator intentIntegrator = new IntentIntegrator(ShopDashBoard.this);

        //Set Prompt text
        intentIntegrator.setPrompt("For Flash Use Volume Up Key");

        //set beep
        intentIntegrator.setBeepEnabled(true);

        //set Camera
        intentIntegrator.setCameraId(0);

        //Locked Orientation
        intentIntegrator.setOrientationLocked(true);

        intentIntegrator.setBarcodeImageEnabled(true);

        //Set Capture Activity
        intentIntegrator.setCaptureActivity(QRCodeScanner.class);

        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        //Initiate Scan
        intentIntegrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Initiate Intent Result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);




            //Check Condition
            if (intentResult.getContents() != null && resultCode==RESULT_OK) {


                FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                DatabaseReference reference = rootNode.getReference("Shops");


                String output = intentResult.getContents();

                if (output.startsWith("QrRegistry")) {
                    String[] separated = output.split(":");

                    managerShop = new SessionManagerShop(getApplicationContext());
                    shopId = managerShop.getShopId();

                    name = separated[1];
                    email = separated[2];
                    phoneNo = separated[3];



                    String dbTime = new SimpleDateFormat("hh a", Locale.getDefault()).format(new Date());

                    CustomersDataForShops addNewUser = new CustomersDataForShops(name, email, phoneNo, currentDate, currentTime);

                    reference.child(shopId).child("Customers").child(currentDate).child(dbTime).child(phoneNo).setValue(addNewUser);


                    //Initialize Dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopDashBoard.this);

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


                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopDashBoard.this);
                    builder.setMessage("Wrong QR Code");
                    builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scanCode();
                        }
                    });
                    builder.show();


            }

        }
        else {

                Toast.makeText(getApplicationContext(), "OOPS... You did Not Scan Anything", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
    

    public void ShopQR(View view) {
        startActivity(new Intent(getApplicationContext(),ShopQRCode.class));
        finish();
    }

    public void UpdateShopProfile(View view) {
        startActivity(new Intent(getApplicationContext(),ShopProfile.class));
    }

    public void CustomerList(View view) {
        startActivity(new Intent(getApplicationContext(),ShopCustomersList.class));
    }

    public void CustomerReport(View view) {
        startActivity(new Intent(getApplicationContext(),ShopCustomersReport.class));
    }

    public void Logout(View view) {
        logout();
    }

    private void logout() {
        //managerShop = new SessionManagerShop(getApplicationContext());

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

                managerShop.setShopLogin(false);
                managerShop.setDetails("","","","","","","");
                //activity.finishAffinity();
               // dialog.dismiss();

                //Finish Activity
                startActivity(new Intent(getApplicationContext(), UserDashBoard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK ));
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