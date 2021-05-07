package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.CustomerAdapter;
import com.ssmptc.QrRegistry.DataBase.CustomersModel;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShopCustomersList extends AppCompatActivity {

    RecyclerView recyclerView;

    ProgressDialog progressDialog;

    TextView tw_times;

    CustomerAdapter adapter;

    String shopId;

    SessionManagerShop managerShop;

    private DatabaseReference mDatabaseRef;
    private List<CustomersModel> customersModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_customers_list);

        tw_times = findViewById(R.id.timer);

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopCustomersList.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        recyclerView = findViewById(R.id.customersView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customersModels = new ArrayList<>();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        String [] times = {"12 am","01 am","02 am","03 am","04 am","05 am","06 am","07 am","08 am","09 am","10 am","11 am",
                           "12 pm","01 pm","02 pm","03 pm","04 pm","05 pm","06 pm","07 pm","08 pm","09 pm","10 pm","11 pm",};

        managerShop = new SessionManagerShop(getApplicationContext());
        shopId = managerShop.getShopId();

        for (int i=0; i<16; i++) {

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").child(currentDate).child(times[i]);
            list();
        }

    }

    private void list() {

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    CustomersModel model = postSnapshot.getValue(CustomersModel.class);
                    customersModels.add(model);
                }

                adapter = new CustomerAdapter(ShopCustomersList.this, customersModels);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ShopCustomersList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}