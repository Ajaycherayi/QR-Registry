package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ShopQRCode extends AppCompatActivity {

    ImageView output;
    ImageView back;
    TextView tv_ShoName;
    ProgressDialog progressDialog;

    String AES = "AES";
    private final String keyPass = "qrregistry@shop";
    String data;


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

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopQRCode.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Query getShopData = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Profile");
        getShopData.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String appName = "QrRegistryShop";
                String  id = snapshot.child("id").getValue(String.class);
                String shopName  = snapshot.child("shopName").getValue(String.class);

                try {
                    data = encrypt(id);
                    MultiFormatWriter writer = new MultiFormatWriter();
                    try {
                        BitMatrix matrix = writer.encode( appName + ":"+shopName+":"+ data,
                                BarcodeFormat.QR_CODE,350,350);

                        BarcodeEncoder encoder =new BarcodeEncoder();

                        Bitmap bitmap = encoder.createBitmap(matrix);

                        progressDialog.dismiss();

                        output.setImageBitmap(bitmap);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String encrypt(String total) throws Exception{
        SecretKeySpec key = generateKey(keyPass);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(total.getBytes());
        return Base64.encodeToString(encVal,Base64.DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private SecretKeySpec generateKey(String keyPass) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = keyPass.getBytes(StandardCharsets.UTF_8); //"UTF-8"
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key, "AES"); //SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");

    }
}
