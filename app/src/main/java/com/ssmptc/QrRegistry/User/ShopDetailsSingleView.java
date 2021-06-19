package com.ssmptc.QrRegistry.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.User.SessionManagerUser;
import com.ssmptc.QrRegistry.R;


public class ShopDetailsSingleView extends AppCompatActivity {

    Button btn_img,btn_contact;
    ImageView btn_back;

    SessionManagerUser managerUser;

    ProgressDialog progressDialog;

    private TextView tv_shopName,tv_category,tv_ownerName,tv_location,tv_phoneNumber,tv_email,tv_wDays,tv_wTime,tv_description;

    private TextView hint_email,hint_wDays,hint_wTime,hint_description;

    private String shopId,pushKey,shopName,category,location,email,phoneNumber;

    private DatabaseReference shopDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_details_single_view);

        btn_back = findViewById(R.id.btn_back);

        btn_img = findViewById(R.id.btn_img);
        btn_contact = findViewById(R.id.btn_contact);

        tv_shopName = findViewById(R.id.tv_shopName);
        tv_category = findViewById(R.id.tv_category);
        tv_location = findViewById(R.id.tv_location);
        tv_ownerName = findViewById(R.id.tv_ownerName);
        tv_phoneNumber = findViewById(R.id.tv_phoneNumber);
        tv_email = findViewById(R.id.tv_email);
        tv_wDays = findViewById(R.id.tv_wDays);
        tv_wTime = findViewById(R.id.tv_wTime);
        tv_description = findViewById(R.id.tv_description);

        hint_email = findViewById(R.id.hint_email);
        hint_wDays = findViewById(R.id.hint_wDays);
        hint_wTime = findViewById(R.id.hint_wTime);
        hint_description = findViewById(R.id.hint_description);




        //---------------Get Data From Previous Activity----------------
        shopId = getIntent().getStringExtra("shopId");
        pushKey = getIntent().getStringExtra("key");

        managerUser = new SessionManagerUser(getApplicationContext());
        phoneNumber = managerUser.getPhone();

        //--------------- Internet Checking -----------
        if (!isConnected(ShopDetailsSingleView.this)){
            showCustomDialog();
        }

        //--------------- Initialize ProgressDialog -----------
        progressDialog = new ProgressDialog(ShopDetailsSingleView.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        shopDb = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Profile");
        shopDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //---------------Get Data From Shop DataBase----------------
                shopName = snapshot.child("shopName").getValue(String.class);
                tv_shopName.setText(shopName);
                category = snapshot.child("category").getValue(String.class);
                tv_category.setText(category);
                location = snapshot.child("location").getValue(String.class);
                tv_location.setText(location);
                tv_ownerName.setText(snapshot.child("ownerName").getValue(String.class));
                phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                tv_phoneNumber.setText(phoneNumber);
                email = snapshot.child("email").getValue(String.class);
                tv_email.setText(email);

                String wDay = snapshot.child("workingDays").getValue(String.class);
                String wTimes = snapshot.child("workingTime").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);

                if (email.equals("")){

                    tv_email.setVisibility(View.GONE);
                    hint_email.setVisibility(View.GONE);
                }else {
                    tv_email.setText(email);
                }

                if (wDay.equals("")){
                    tv_wDays.setVisibility(View.GONE);
                    hint_wDays.setVisibility(View.GONE);
                }else {
                    tv_wDays.setText(snapshot.child("workingDays").getValue(String.class));
                }

                if (wTimes.equals("")){

                    tv_wTime.setVisibility(View.GONE);
                    hint_wTime.setVisibility(View.GONE);
                }else {
                    tv_wTime.setText(snapshot.child("workingTime").getValue(String.class));
                }

                if (description.equals("")){

                    tv_description.setVisibility(View.GONE);
                    hint_description.setVisibility(View.GONE);
                }else {
                    tv_description.setText(snapshot.child("description").getValue(String.class));
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Shops").child(shopId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("Shop Images")){

                            //---------------Show All Images Of Shop Uploaded----------------
                            btn_img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ShopDetailsSingleView.this,ShowShopImages.class);
                                    intent.putExtra("shopId",shopId);
                                    startActivity(intent);
                                }
                            });
                        }
                        else {
                            btn_img.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog= new Dialog(ShopDetailsSingleView.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.contacts);

                Button btn_email = dialog.findViewById(R.id.btn_sendMail);
                Button btn_locate = dialog.findViewById(R.id.btn_locate);
                Button btn_call = dialog.findViewById(R.id.btn_call);
                Button btn_msg= dialog.findViewById(R.id.btn_msg);


                if (email.equals("")){

                    btn_email.setVisibility(View.GONE);

                }else{

                    //---------------Send Email To Shop----------------
                    btn_email.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                            Uri data = Uri.parse("mailto:?subject=" + ""+ "&body=" + "" + "&to=" + email);
                            emailIntent.setData(data);
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                        }
                    });

                }


                //---------------Locate Shop In Map----------------
                btn_locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Url pass ShopName, Category and Location
                        String strUri = "http://maps.google.com/maps?q=" + shopName + "," + category +  " (" + location + ")";
                        Intent locationIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                        locationIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(locationIntent);
                    }
                });

                //---------------Call Intent----------------
                btn_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SessionManagerUser managerUser;
                        managerUser = new SessionManagerUser(ShopDetailsSingleView.this);
                        String phoneNumber = managerUser.getPhone();

                        FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Shops").child(pushKey)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String number = snapshot.child("phoneNumber").getValue(String.class);
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                        callIntent.setData(Uri.parse("tel:" + number));
                                        startActivity(callIntent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });

                //---------------Message Intent----------------
                btn_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SessionManagerUser managerUser;
                        managerUser = new SessionManagerUser(ShopDetailsSingleView.this);
                        String phoneNumber = managerUser.getPhone();

                        FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Shops").child(pushKey)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String number = snapshot.child("phoneNumber").getValue(String.class);
                                        Intent MessageIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
                                        startActivity(MessageIntent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setWindowAnimations(R.style.BottomDialog);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });



        //---------------Back To Shop Details List----------------
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopDetailsSingleView.this,ShopDetails.class));
                finish();
            }
        });
    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailsSingleView.this);
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
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(ShopDetailsSingleView shopDetailsSingleView) {

        ConnectivityManager connectivityManager = (ConnectivityManager) shopDetailsSingleView.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }


}