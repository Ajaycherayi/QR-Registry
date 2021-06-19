package com.ssmptc.QrRegistry.Shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.Shop.CustomersDetailsAdapter;
import com.ssmptc.QrRegistry.DataBase.Shop.CustomerDataForShopList;
import com.ssmptc.QrRegistry.DataBase.Shop.SessionManagerShop;
import com.ssmptc.QrRegistry.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AllCustomers extends AppCompatActivity {

    ImageView btn_back;

    SessionManagerShop managerShop;

    private EditText et_search;
    private Button et_Date;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private DatePickerDialog.OnDateSetListener setListener;

    private CustomersDetailsAdapter adapter;
    private String shopId,date;
    private List<CustomerDataForShopList> customerDataForShopLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_customers_list);

        btn_back = findViewById(R.id.btn_backToSd);

        et_Date = findViewById(R.id.sort_date);
        et_search = findViewById(R.id.et_search);

        //--------------- Internet Checking -----------
        if (!isConnected(AllCustomers.this)){
            showCustomDialog();
        }


        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(AllCustomers.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        recyclerView = findViewById(R.id.customersView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllCustomers.this));

        customerDataForShopLists = new ArrayList<>();



        btn_back.setOnClickListener(v ->  {
            startActivity(new Intent(AllCustomers.this,ShopDashBoard.class));
            finish();
        });

        search();

        filterByDate();

        list();

    }

    //----------------------Search Shop Details---------
    private void search() {

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

    }

    //------------------Filter By Date-----------------
    private void filterByDate(){

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        //---------------Date button onClick event----------------
        et_Date.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AllCustomers.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    setListener,year,month,day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        //---------------Date Pick Listener----------------
        setListener = (view, year1, month1, day1) -> {
            month1 = month1 +1;
            String fm = ""+ month1;
            String fd = ""+ day1;
            if(month1 <10){
                fm = "0"+ month1;
            }
            if (day1 <10){
                fd = "0"+ day1;
            }
            date = fd+"-"+fm+"-"+ year1;
            et_Date.setText(date);

            managerShop = new SessionManagerShop(getApplicationContext());
            shopId = managerShop.getShopId();

            FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").orderByChild("currentDate").equalTo(date)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    customerDataForShopLists.clear();

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        CustomerDataForShopList model = postSnapshot.getValue(CustomerDataForShopList.class);
                        customerDataForShopLists.add(model);

                        adapter = new CustomersDetailsAdapter(AllCustomers.this, customerDataForShopLists);
                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();

                    }
                    progressDialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(AllCustomers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        };

    }

    //---------------List All Customers----------------
    private void list() {

        managerShop = new SessionManagerShop(getApplicationContext());
        shopId = managerShop.getShopId();

        Query ShopDb = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers");
        ShopDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    CustomerDataForShopList model = postSnapshot.getValue(CustomerDataForShopList.class);
                    customerDataForShopLists.add(model);

                    adapter = new CustomersDetailsAdapter(AllCustomers.this, customerDataForShopLists);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();

                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AllCustomers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AllCustomers.this);
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
    private boolean isConnected(AllCustomers allCustomers) {

        ConnectivityManager connectivityManager = (ConnectivityManager) allCustomers.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }



}