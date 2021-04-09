package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.CustomerAdapter;
import com.ssmptc.QrRegistry.DataBase.CustomersModel;
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

    CustomerAdapter adapter;

    private DatabaseReference mDatabaseRef;
    private List<CustomersModel> customersModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_customers_list);

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopCustomersList.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        recyclerView = (RecyclerView) findViewById(R.id.customersView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customersModels = new ArrayList<>();


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
        Calendar cal = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(new Date());
        String time = "10";
        String date = "02-04-21";
        String minusDate;


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Customer-Details-For-Shop").child(currentDate).child(time);
        list();
        cal.add(Calendar.DATE, -1);
        minusDate = sdf.format(cal.getTime());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Customer-Details-For-Shop").child(minusDate).child(time);
        list();



        for (int now = -1; !minusDate.equals(date); now=-1) {

            cal.add(Calendar.DATE, now);
            minusDate = sdf.format(cal.getTime());
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Customer-Details-For-Shop").child(minusDate).child(time);
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