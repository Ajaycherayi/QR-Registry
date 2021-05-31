package com.ssmptc.QrRegistry.ShopLoginSignUp;

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
import android.net.Uri;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.CustomersDetailsAdapter;
import com.ssmptc.QrRegistry.DataBase.CustomerDataForShopList;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

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
    private DatabaseReference userDb;
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

            Query ShopDb = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").orderByChild("currentDate").equalTo(date);
            ShopDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    customerDataForShopLists.clear();

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        userDb = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(postSnapshot.child("phoneNumber").getValue(String.class))).child("Profile");
                        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                customerDataForShopLists.add(new CustomerDataForShopList((postSnapshot.child("id").getValue(String.class)),
                                        dataSnapshot.child("name").getValue(String.class),
                                        dataSnapshot.child("phoneNumber").getValue(String.class),
                                        dataSnapshot.child("email").getValue(String.class),
                                        dataSnapshot.child("age").getValue(String.class),
                                        dataSnapshot.child("gender").getValue(String.class),
                                        dataSnapshot.child("address").getValue(String.class),
                                        postSnapshot.child("currentDate").getValue(String.class),
                                        postSnapshot.child("currentTime").getValue(String.class)));

                                adapter = new CustomersDetailsAdapter(AllCustomers.this, customerDataForShopLists);
                                recyclerView.setAdapter(adapter);
                                progressDialog.dismiss();
                                adapter.setOnItemClickListener(new CustomersDetailsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onCallClick(int position) {
                                        CallToCustomer(position);
                                    }

                                    @Override
                                    public void onMessageClick(int position) {
                                        MessageToCustomer(position);
                                    }

                                    @Override
                                    public void onEmailClick(int position) {
                                        EmailToCustomer(position);
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

                    userDb = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(postSnapshot.child("phoneNumber").getValue(String.class))).child("Profile");
                    userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            customerDataForShopLists.add(new CustomerDataForShopList((postSnapshot.child("id").getValue(String.class)),
                                    dataSnapshot.child("name").getValue(String.class),
                                    dataSnapshot.child("phoneNumber").getValue(String.class),
                                    dataSnapshot.child("email").getValue(String.class),
                                    dataSnapshot.child("age").getValue(String.class),
                                    dataSnapshot.child("gender").getValue(String.class),
                                    dataSnapshot.child("address").getValue(String.class),
                                    postSnapshot.child("currentDate").getValue(String.class),
                                    postSnapshot.child("currentTime").getValue(String.class)));

                            adapter = new CustomersDetailsAdapter(AllCustomers.this, customerDataForShopLists);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                            adapter.setOnItemClickListener(new CustomersDetailsAdapter.OnItemClickListener() {
                                @Override
                                public void onCallClick(int position) {

                                    CallToCustomer(position);
                                }

                                @Override
                                public void onMessageClick(int position) {
                                    MessageToCustomer(position);
                                }

                                @Override
                                public void onEmailClick(int position) {
                                    EmailToCustomer(position);
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

                Toast.makeText(AllCustomers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //---------------Send Email To Shop----------------
    private void EmailToCustomer(int position) {

        CustomerDataForShopList data = customerDataForShopLists.get(position);
        String phoneNumber = data.getPhoneNumber();

        Query Db_email = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Profile");
        Db_email.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue(String.class);
                assert email != null;
                if (email.equals("")){
                    Toast.makeText(AllCustomers.this, "Shop Email Not Provided", Toast.LENGTH_SHORT).show();
                }else{
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse("mailto:?subject=" + ""+ "&body=" + "" + "&to=" + email);
                    emailIntent.setData(data);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //-------------Intent to Massage-------------------
    private void MessageToCustomer(int position) {

        CustomerDataForShopList data = customerDataForShopLists.get(position);
        String phoneNumber = data.getPhoneNumber();

        Query customerDb = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Profile");
        customerDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String number = snapshot.child("phoneNumber").getValue(String.class);
                Intent MessageIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
                startActivity(MessageIntent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //-------------Intent Dialer With Phone Number------
    private void CallToCustomer(int position) {

        CustomerDataForShopList data = customerDataForShopLists.get(position);
        String phoneNumber = data.getPhoneNumber();

        Query customerDb = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Profile");
        customerDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String number = snapshot.child("phoneNumber").getValue(String.class);
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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