package com.ssmptc.QrRegistry;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.net.URLEncoder;

public class ContactUs extends AppCompatActivity {

    ImageView to_gmail,to_whatsapp,to_instagram,to_gitHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);

        to_gitHub = findViewById(R.id.to_gitHub);
        to_whatsapp = findViewById(R.id.to_whatsapp);
        to_gmail = findViewById(R.id.to_gMail);
        to_instagram = findViewById(R.id.to_instagram);

        to_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone="+ "+919847832419" +"&text=" + URLEncoder.encode("Hai,\n", "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e){
                    e.printStackTrace();
                }
                Toast.makeText(ContactUs.this, "Contact Nabeeh P P via Whatsapp", Toast.LENGTH_SHORT).show();
            }
        });

        to_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://instagram.com/pp_nabee_h_0?utm_medium=copy_link");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://instagram.com/anas_mohmd__?utm_medium=copy_link")));
                }
                Toast.makeText(ContactUs.this, "Contact Mohamed Anas K P via Instagram", Toast.LENGTH_SHORT).show();

            }
        });

        to_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" +"QR Registry"+ "&body=" + "Hai,\n" + "&to=" + "salmanulfariscc1999@gmail.com");
                emailIntent.setData(data);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                Toast.makeText(ContactUs.this, "Contact Salmanul Faris C C via E-mail", Toast.LENGTH_SHORT).show();

            }
        });

        to_gitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://github.com/arshidabdulazeez";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                Toast.makeText(ContactUs.this, "Contact Arshid Abdul Azeez M P via GitHub", Toast.LENGTH_SHORT).show();

            }
        });
    }
}