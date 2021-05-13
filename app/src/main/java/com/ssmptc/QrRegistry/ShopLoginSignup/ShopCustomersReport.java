package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

public class ShopCustomersReport extends AppCompatActivity {

    AutoCompleteTextView getTimeList;

    ImageView btn_back;

    Button et_Date;

    RecyclerView recyclerView;

    ProgressDialog progressDialog;

    CustomerAdapter adapter;

    String shopId;

    TextView display;

    String time,date;

    SessionManagerShop managerShop;

    DatePickerDialog.OnDateSetListener setListener;

    private DatabaseReference mDatabaseRef;
    private List<CustomersModel> customersModels;

    ArrayAdapter<String> arrayAdapter;

    String [] times = {"07 am","08 am","09 am","10 am","11 am","12 pm","01 pm","02 pm","03 pm","04 pm","05 pm",
            "06 pm","07 pm","08 pm","09 pm","10 pm"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_customers_report);

        btn_back = findViewById(R.id.btn_backToSd);

        et_Date = findViewById(R.id.sort_date);
        getTimeList = findViewById(R.id.sort_time);

        display = findViewById(R.id.show_time);

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopCustomersReport.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        recyclerView = findViewById(R.id.customersView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShopCustomersReport.this));

        customersModels = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String date = "02-04-2021";
        String minusDate;

        managerShop = new SessionManagerShop(getApplicationContext());
        shopId = managerShop.getShopId();

        arrayAdapter =new ArrayAdapter<String>(this,R.layout.shop_time_list,times);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopCustomersReport.this,ShopDashBoard.class));
                finish();
            }
        });

        getTimeList.setAdapter(arrayAdapter);

        for (int i=0; i<=15; i++) {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").child(currentDate).child(times[i]);
            list();
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        minusDate = sdf.format(cal.getTime());
        for (int i=0; i<=15; i++) {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").child(minusDate).child(times[i]);
            list();
        }

        for (int now = -1; !minusDate.equals(date); now=-1) {
            cal.add(Calendar.DATE, now);
            minusDate = sdf.format(cal.getTime());

            for (int i=0; i<=15; i++) {

                mDatabaseRef = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").child(minusDate).child(times[i]);
                list();
            }
        }

        filter();

    }

    private void filter(){

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ShopCustomersReport.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month+1;
                String fm = ""+month;
                String fd = ""+day;
                if(month<10){
                    fm = "0"+month;
                }
                if (day<10){
                    fd = "0"+day;
                }
                date = fd+"-"+fm+"-"+year;
                et_Date.setText(date);

                for (int i=0; i<=15; i++) {
                    Query q = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").child(date).child(times[i]);
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            showListener(snapshot);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                getTimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        time = arrayAdapter.getItem(position);

                        Query query= FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").child(date).child(time);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                showListener(snapshot);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

            }
        };

    }

    private void showListener(DataSnapshot snapshot) {

        customersModels.clear();

        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
            CustomersModel model = postSnapshot.getValue(CustomersModel.class);
            customersModels.add(model);
        }

        adapter = new CustomerAdapter(ShopCustomersReport.this, customersModels);
        recyclerView.setAdapter(adapter);
        progressDialog.dismiss();

    }


    private void list() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    CustomersModel model = postSnapshot.getValue(CustomersModel.class);
                    customersModels.add(model);
                }

                adapter = new CustomerAdapter(ShopCustomersReport.this, customersModels);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ShopCustomersReport.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}