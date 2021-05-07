package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssmptc.QrRegistry.DataBase.Model;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.DataBase.ShopAdapter;
import com.ssmptc.QrRegistry.R;

import java.util.ArrayList;

public class ShopImages extends AppCompatActivity implements ShopAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ArrayList<Model> list;
    private ShopAdapter shopAdapter;
    private SessionManagerShop managerShop;
    private String shopId;
    ProgressDialog progressDialog;


    private FirebaseStorage mStorage;
    private DatabaseReference root ;

    private ValueEventListener mDBListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_images);

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopImages.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        managerShop = new SessionManagerShop(getApplicationContext());
        shopId = managerShop.getShopId();

        mStorage= FirebaseStorage.getInstance();
        root = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Images");

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        shopAdapter = new ShopAdapter(ShopImages.this,list);

        recyclerView.setAdapter(shopAdapter);

        shopAdapter.setOnItemClickListener(ShopImages.this);


        //shopAdapter = new ShopAdapter(this,list);


       mDBListener = root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Model model = dataSnapshot.getValue(Model.class);
                    model.setKey(dataSnapshot.getKey());
                    list.add(model);

                }

                shopAdapter.notifyDataSetChanged();



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
        Model selectedItem = list.get(position);
        String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());

        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                root.child(selectedKey).removeValue();
                Toast.makeText(ShopImages.this, "Item Deleted..", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        root.removeEventListener(mDBListener);
    }
}