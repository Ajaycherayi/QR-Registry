package com.ssmptc.QrRegistry;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class AboutQrRegistry extends AppCompatActivity {

    ImageView source_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_qr_registry);

        source_code = findViewById(R.id.source_code);

        source_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String url = "https://github.com/AjayCherayi/QR-Registry";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                Toast.makeText(AboutQrRegistry.this, "QR Registry Source code", Toast.LENGTH_SHORT).show();

            }
        });


    }








    
}