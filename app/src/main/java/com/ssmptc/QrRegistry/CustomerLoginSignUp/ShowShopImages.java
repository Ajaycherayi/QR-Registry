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
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.Model;
import com.ssmptc.QrRegistry.DataBase.ShowImageAdapter;
import com.ssmptc.QrRegistry.R;
import java.util.ArrayList;

public class ShowShopImages extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    String shopId;
    DatabaseReference shopDb ;

    private ArrayList<Model> list;
    private ShowImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_shop_images);

        recyclerView = findViewById(R.id.recycleView);

        shopId = getIntent().getStringExtra("shopId");

        //--------------- Internet Checking -----------
        if (!isConnected(ShowShopImages.this)){
            showCustomDialog();
        }

        //--------------- Initialize ProgressDialog -----------
        progressDialog = new ProgressDialog(ShowShopImages.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        shopDb = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Images");


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        imageAdapter = new ShowImageAdapter(ShowShopImages.this,list);

        recyclerView.setAdapter(imageAdapter); // Set Adapter Into RecyclerView

        //-----------------Get All Image keys-------------------------------
        shopDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Model model = dataSnapshot.getValue(Model.class);
                    assert model != null; // Model Not Null
                    model.setKey(dataSnapshot.getKey());
                    list.add(model);
                }
                imageAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ShowShopImages.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShowShopImages.this);
        builder.setMessage("Please connect to the internet")
                .setCancelable(false)
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
    private boolean isConnected(ShowShopImages showShopImages) {

        ConnectivityManager connectivityManager = (ConnectivityManager) showShopImages.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected()); // if true ,  else false

    }

}