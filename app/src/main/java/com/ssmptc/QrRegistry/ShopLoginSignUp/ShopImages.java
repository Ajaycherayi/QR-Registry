package com.ssmptc.QrRegistry.ShopLoginSignUp;

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
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssmptc.QrRegistry.DataBase.Shop.SessionManagerShop;
import com.ssmptc.QrRegistry.DataBase.Shop.ShopImageAdapter;
import com.ssmptc.QrRegistry.DataBase.Shop.ShopImageUrl;
import com.ssmptc.QrRegistry.R;

import java.util.ArrayList;

public class ShopImages extends AppCompatActivity implements ShopImageAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    SessionManagerShop managerShop;

    private ArrayList<ShopImageUrl> list;
    private ShopImageAdapter shopImageAdapter;
    private FirebaseStorage ImgStorage;
    private DatabaseReference shopDb ;

    private ValueEventListener mDBListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_images);

        //--------------- Internet Checking -----------
        if (!isConnected(ShopImages.this)){
            showCustomDialog();
        }


        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopImages.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        managerShop = new SessionManagerShop(getApplicationContext());
        String shopId = managerShop.getShopId();

        ImgStorage= FirebaseStorage.getInstance();
        shopDb = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Images");

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        shopImageAdapter = new ShopImageAdapter(ShopImages.this,list);

        recyclerView.setAdapter(shopImageAdapter);

        shopImageAdapter.setOnItemClickListener(ShopImages.this);

        mDBListener = shopDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    ShopImageUrl shopImageUrl = dataSnapshot.getValue(ShopImageUrl.class);
                    assert shopImageUrl != null;
                    shopImageUrl.setKey(dataSnapshot.getKey());
                    list.add(shopImageUrl);

                }

                shopImageAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();

                Toast.makeText(ShopImages.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal Click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        Toast.makeText(this, "Delete Click", Toast.LENGTH_SHORT).show();
        ShopImageUrl selectedItem = list.get(position);
        String selectedKey = selectedItem.getKey();

        StorageReference imageRef = ImgStorage.getReferenceFromUrl(selectedItem.getImageUrl());

        imageRef.delete().addOnSuccessListener(aVoid -> {

            shopDb.child(selectedKey).removeValue();
            Toast.makeText(ShopImages.this, "Item Deleted..", Toast.LENGTH_SHORT).show();

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shopDb.removeEventListener(mDBListener);
    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ShopImages.this);
        builder.setMessage("Please connect to the internet")
                //   .setCancelable(false)
                .setPositiveButton("Connect", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), EditShopProfile.class));
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(ShopImages shopImages) {

        ConnectivityManager connectivityManager = (ConnectivityManager) shopImages.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }


}