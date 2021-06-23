package com.ssmptc.QrRegistry.Shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.Shop.CustomersDetailsAdapter;
import com.ssmptc.QrRegistry.DataBase.Shop.CustomerDataForShopList;
import com.ssmptc.QrRegistry.DataBase.Shop.SessionManagerShop;
import com.ssmptc.QrRegistry.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodayCustomers extends AppCompatActivity {

    ImageView btn_back;

    String shopId;
    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    SessionManagerShop managerShop;

    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private CustomersDetailsAdapter adapter;
    private List<CustomerDataForShopList> customerDataForShopLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todays_customers_list);

        btn_back = findViewById(R.id.btn_backToSd);
        recyclerView = findViewById(R.id.customersView);


        //--------------- Internet Checking -----------
        if (!isConnected(TodayCustomers.this)){
            showCustomDialog();
        }

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(TodayCustomers.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customerDataForShopLists = new ArrayList<>();

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(TodayCustomers.this,ShopDashBoard.class));
            finishAffinity();
        });

        list();
    }

    private void list() {

        managerShop = new SessionManagerShop(getApplicationContext());
        shopId = managerShop.getShopId();

       FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").orderByChild("currentDate").equalTo(currentDate)
       .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    CustomerDataForShopList model = postSnapshot.getValue(CustomerDataForShopList.class);
                    customerDataForShopLists.add(model);

                    adapter = new CustomersDetailsAdapter(TodayCustomers.this, customerDataForShopLists);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(TodayCustomers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TodayCustomers.this);
        builder.setMessage("Please connect to the internet")
                //   .setCancelable(false)
                .setPositiveButton("Connect", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), ShopDashBoard.class));
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(TodayCustomers todayCustomers) {

        ConnectivityManager connectivityManager = (ConnectivityManager) todayCustomers.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }

}