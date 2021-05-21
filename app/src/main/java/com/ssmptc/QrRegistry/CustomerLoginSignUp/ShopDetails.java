package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.SessionManagerCustomer;
import com.ssmptc.QrRegistry.DataBase.ShopDetailsAdapter;
import com.ssmptc.QrRegistry.DataBase.ShopDetailsModel;
import com.ssmptc.QrRegistry.DataBase.ShopsDataForCustomers;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.ToDoList.CustomerToDoList;
import com.ssmptc.QrRegistry.ToDoList.TodoAdapter;
import com.ssmptc.QrRegistry.ToDoList.TodoModel;

import java.util.ArrayList;
import java.util.List;

public class ShopDetails extends AppCompatActivity {

    SessionManagerCustomer managerCustomer;

    String phone,email,shopName,category,location,imageKey;

    RecyclerView recyclerView;
    ShopDetailsAdapter adapter;

    private DatabaseReference db,mDatabaseRef;
    private List<ShopsDataForCustomers> dataForCustomers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);


        recyclerView = findViewById(R.id.rv_shopDetails);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataForCustomers = new ArrayList<>();

        managerCustomer = new SessionManagerCustomer(getApplicationContext());
        phone = managerCustomer.getPhone();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Shops");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dataForCustomers.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()){

                    ShopsDataForCustomers model = postSnapshot.getValue(ShopsDataForCustomers.class);
                    dataForCustomers.add(model);
                }

                adapter = new ShopDetailsAdapter(ShopDetails.this,dataForCustomers);
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
                Toast.makeText(ShopDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void MoreShopDetails(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.shop_more_details,null);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btn_email,btn_locate,btn_delete,btn_img;
        TextView tv_shopName,tv_category,tv_ownerName,tv_location,tv_phoneNumber,tv_email,tv_wDays,tv_wTime,tv_description;


        btn_email = view.findViewById(R.id.btn_sendMail);
        btn_locate = view.findViewById(R.id.btn_locate);
        btn_delete = view.findViewById(R.id.btn_delete);
        btn_img = view.findViewById(R.id.btn_img);

        tv_shopName = view.findViewById(R.id.tv_shopName);
        tv_category = view.findViewById(R.id.tv_category);
        tv_location = view.findViewById(R.id.tv_location);
        tv_ownerName = view.findViewById(R.id.tv_ownerName);
        tv_phoneNumber = view.findViewById(R.id.tv_phoneNumber);
        tv_email = view.findViewById(R.id.tv_email);
        tv_wDays = view.findViewById(R.id.tv_wDays);
        tv_wTime = view.findViewById(R.id.tv_wTime);
        tv_description = view.findViewById(R.id.tv_description);


        ShopsDataForCustomers data = dataForCustomers.get(position);
        String id = data.getId();

        db = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Shops").child(id);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shopName = snapshot.child("name").getValue(String.class);
                tv_shopName.setText(shopName);
                category = snapshot.child("category").getValue(String.class);
                tv_category.setText(category);
                location = snapshot.child("location").getValue(String.class);
                tv_location.setText(location);
                tv_ownerName.setText(snapshot.child("ownerName").getValue(String.class));
                tv_phoneNumber.setText(snapshot.child("phoneNumber").getValue(String.class));
                email = snapshot.child("email").getValue(String.class);
                tv_email.setText(email);
                tv_wDays.setText(snapshot.child("wDays").getValue(String.class));
                tv_wTime.setText(snapshot.child("wTime").getValue(String.class));
                tv_description.setText(snapshot.child("description").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.equals(" ")){
                    Toast.makeText(ShopDetails.this, "Shop Email Not Provided", Toast.LENGTH_SHORT).show();
                }else{
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse("mailto:?subject=" + ""+ "&body=" + "" + "&to=" + email);
                    emailIntent.setData(data);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                }
            }
        });

        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUri = "http://maps.google.com/maps?q=" + shopName + "," + category +  " (" + location + ")";

                Intent locationIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                locationIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                startActivity(locationIntent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Shops").child(id);
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                        dialog.cancel();
                        Toast.makeText(ShopDetails.this, "Shop Details Deleted", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ShopDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void MessageToShop(int position) {
        ShopsDataForCustomers data = dataForCustomers.get(position);
        String id = data.getId();

        db = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Shops").child(id);
        db.addValueEventListener(new ValueEventListener() {
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

    private void CallToShop(int position) {

        ShopsDataForCustomers data = dataForCustomers.get(position);
        String id = data.getId();

        db = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Shops").child(id);
        db.addValueEventListener(new ValueEventListener() {
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
}