package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ssmptc.QrRegistry.DataBase.SessionManager;
import com.ssmptc.QrRegistry.R;

public class ShopQRCode extends AppCompatActivity {

    ImageView output;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_q_r_code);

        output = findViewById(R.id.iv_output);
        back = findViewById(R.id.btn_backToDashBoard);

        SessionManager sessionManager = new SessionManager(getApplicationContext());


        String _name = sessionManager.getName();
        String _email = sessionManager.getEmail();
        String _phoneNo = sessionManager.getPhone();




        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode( _name + ":" + _email + ":" + _phoneNo,
                    BarcodeFormat.QR_CODE,350,350);

            BarcodeEncoder encoder =new BarcodeEncoder();

            Bitmap bitmap = encoder.createBitmap(matrix);

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



}
