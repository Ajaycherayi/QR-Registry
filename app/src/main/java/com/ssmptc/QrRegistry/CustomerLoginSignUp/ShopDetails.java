package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.User.SessionManagerUser;
import com.ssmptc.QrRegistry.DataBase.User.ShopDetailsAdapter;
import com.ssmptc.QrRegistry.DataBase.User.ShopsDataForUser;
import com.ssmptc.QrRegistry.R;
import java.util.ArrayList;
import java.util.List;

public class ShopDetails extends AppCompatActivity {

    ImageView btn_back;
    EditText et_search;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private String phoneNumber;

    SessionManagerUser managerUser;
    DatabaseReference userDb;
    private DatabaseReference shopDb;
    private ShopDetailsAdapter adapter;
    private List<ShopsDataForUser> dataForUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_details);

        btn_back = findViewById(R.id.btn_back);
        et_search = findViewById(R.id.et_search);
        recyclerView = findViewById(R.id.rv_shopDetails);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataForUser = new ArrayList<>();

        managerUser = new SessionManagerUser(getApplicationContext());
        phoneNumber = managerUser.getPhone();

        adapter = new ShopDetailsAdapter(this,dataForUser);

        //--------------- Internet Checking -----------
        if (!isConnected(ShopDetails.this)){
            showCustomDialog();
        }

        //--------------- Initialize ProgressDialog -----------
        progressDialog = new ProgressDialog(ShopDetails.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);



        shopList();

        //----------------------Search Shop Details----------------
        if (et_search != null){

            et_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.Search(s);
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        //------------------------back to user dashboard--------------
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopDetails.this,UserDashBoard.class));
                finish();
            }
        });
    }

    private void shopList() {

        userDb = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Shops");

        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot idSnapshot) {

                dataForUser.clear();

                for (DataSnapshot postSnapshot : idSnapshot.getChildren()){

                    shopDb = FirebaseDatabase.getInstance().getReference("Shops").child(postSnapshot.child("shopId").getValue(String.class)).child("Shop Profile");
                    shopDb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //--------------- Get data from shop database and put into ShopsDataForCustomers model ------------------------
                            dataForUser.add(new ShopsDataForUser((postSnapshot.child("id").getValue(String.class)),dataSnapshot.child("id").getValue(String.class),
                                    dataSnapshot.child("shopName").getValue(String.class),
                                    dataSnapshot.child("category").getValue(String.class),
                                    dataSnapshot.child("ownerName").getValue(String.class),
                                    dataSnapshot.child("location").getValue(String.class),
                                    dataSnapshot.child("phoneNumber").getValue(String.class),
                                    dataSnapshot.child("email").getValue(String.class),
                                    dataSnapshot.child("working days").getValue(String.class),
                                    dataSnapshot.child("working time").getValue(String.class),
                                    dataSnapshot.child("description").getValue(String.class),
                                    dataSnapshot.child("name").getValue(String.class)));

                            adapter = new ShopDetailsAdapter(ShopDetails.this,dataForUser);
                            recyclerView.setAdapter(adapter);
                            adapter.setOnItemClickListener(new ShopDetailsAdapter.OnItemClickListener(){

                                @Override
                                public void onCallClick(int position) {
                                    CallToShop(position);
                                }

                                @Override
                                public void onMessageClick(int position) {
                                    MessageToShop(position);
                                }

                                @Override
                                public void onMoreClick(int position) {
                                    MoreShopDetails(position);
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShopDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    //------------------------Show More About Shop----------------
    private void MoreShopDetails(int position) {

        ShopsDataForUser data = dataForUser.get(position);
        String id = data.getShopId(); // Get Shop Id from ShopsDataForCustomers ShopImageUrl
        String keyId = data.getId(); // Get User DataBase Key

        Intent intent = new Intent(ShopDetails.this,ShopDetailsSingleView.class);
        intent.putExtra("shopId",id); // Pass Shop Id value To ShopDetailsSingleView
        intent.putExtra("key",keyId); // Pass key value To ShopDetailsSingleView
        startActivity(intent);
        adapter.notifyDataSetChanged();

    }

    //-------------Intent to Massage-------------
    private void MessageToShop(int position) {

        ShopsDataForUser data = dataForUser.get(position);
        String id = data.getShopId();

        shopDb = FirebaseDatabase.getInstance().getReference("Shops").child(id).child("Shop Profile");
        shopDb.addValueEventListener(new ValueEventListener() {
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
    //-------------Intent Dialer With Phone Number------
    private void CallToShop(int position) {

        ShopsDataForUser data = dataForUser.get(position);
        String id = data.getShopId();

        shopDb = FirebaseDatabase.getInstance().getReference("Shops").child(id).child("Shop Profile");
        shopDb.addValueEventListener(new ValueEventListener() {
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

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShopDetails.this);
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
    private boolean isConnected(ShopDetails shopDetails) {

        ConnectivityManager connectivityManager = (ConnectivityManager) shopDetails.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }


}