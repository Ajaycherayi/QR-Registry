package com.ssmptc.QrRegistry.CustomerLoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.BarcodeView;
import com.ssmptc.QrRegistry.DataBase.SessionManager;
import com.ssmptc.QrRegistry.R;

public class QRCodeGeneration extends AppCompatActivity {


    ImageView output;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code_genaration);

        output = findViewById(R.id.iv_output);

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
            }




}