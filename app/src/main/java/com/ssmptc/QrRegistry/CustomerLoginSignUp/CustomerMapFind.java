package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.ToDoList.CustomerToDoList;

import java.util.Objects;

public class CustomerMapFind extends AppCompatActivity {

    private TextInputLayout category,location;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map_find);

        category = findViewById(R.id.et_category);
        location = findViewById(R.id.et_location);
        btn_back = findViewById(R.id.btn_backToCd);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerMapFind.this, CustomerDashBoard.class));
                finish();
            }
        });

    }

    public void FindShop(View view) {

        String txt_location = Objects.requireNonNull(location.getEditText()).getText().toString();
        String txt_category = Objects.requireNonNull(category.getEditText()).getText().toString();

        String strUri = "http://maps.google.com/maps?q=" + txt_category + "," +  " (" + txt_location + ")";

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        startActivity(intent);


    }
}