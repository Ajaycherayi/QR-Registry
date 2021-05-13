package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ssmptc.QrRegistry.DataBase.SessionManagerCustomer;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.R;

public class ShopQRCode extends AppCompatActivity {

    ImageView output;
    ImageView back;
    TextView tv_ShoName;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_q_r_code);

        output = findViewById(R.id.iv_output);
        back = findViewById(R.id.btn_backToSd);
        tv_ShoName = findViewById(R.id.tv_shopName);
        SessionManagerShop managerShop = new SessionManagerShop(getApplicationContext());
        String shopId = managerShop.getShopId();
        String shopName = managerShop.getShopName();
        tv_ShoName.setText(shopName);
        String category = managerShop.getCategory();

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopQRCode.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Query getShopData = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Profile");
        getShopData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                
                String shopLicense = snapshot.child("licenseNumber").getValue(String.class);
                String ownerName = snapshot.child("ownerName").getValue(String.class);
                String location = snapshot.child("location").getValue(String.class);
                String email = snapshot.child("location").getValue(String.class);
                String wTime = snapshot.child("working time").getValue(String.class);
                String wDays = snapshot.child("working days").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);

                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writer.encode( shopName + ":" + category + ":" + shopLicense + ":" + ownerName
                            + ":" + location + ":" + email + ":" + wDays + ":" + wTime + ":" + description + ":" + shopId,
                            BarcodeFormat.QR_CODE,350,350);

                    BarcodeEncoder encoder =new BarcodeEncoder();

                    Bitmap bitmap = encoder.createBitmap(matrix);

                    progressDialog.dismiss();

                    output.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    e.printStackTrace();
                }

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),ShopDashBoard.class));
                        finish();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







    }



}
