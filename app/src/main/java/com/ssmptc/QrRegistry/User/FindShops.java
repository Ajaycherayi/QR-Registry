package com.ssmptc.QrRegistry.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.ssmptc.QrRegistry.R;

import java.util.Objects;

public class FindShops extends AppCompatActivity {

    private TextInputLayout et_category,et_location;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_shops);

        et_category = findViewById(R.id.et_category);
        et_location = findViewById(R.id.et_location);
        btn_back = findViewById(R.id.btn_backToCd);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindShops.this, UserDashBoard.class));
                finish();
            }
        });

    }

    public void FindShop(View view) {

        if (!isConnected(FindShops.this)){
            showCustomDialog();
        }else {

            if (!validateCategory()  | !validateLocation() ) {

                return;
            }
            String txt_location = Objects.requireNonNull(et_location.getEditText()).getText().toString();
            String txt_category = Objects.requireNonNull(et_category.getEditText()).getText().toString();
            // ---------------------Search Shops using category and location---------------------------
            String strUri = "http://maps.google.com/maps?q=" + txt_category + "," + " (" + txt_location + ")";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        }

    }

    //--------------- Validate location EditText -----------
    private boolean validateLocation() {
        String val = et_location.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_location.setError("Field can not be empty");
            return false;
        }else if(val.length()>30){
            et_location.setError("Name is Too Large");
            return false;
        }else {
            et_location.setError(null);
            return true;
        }
    }

    //--------------- Validate category EditText -----------
    private boolean validateCategory() {

        String val = et_category.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_category.setError("Field can not be empty");
            return false;
        }else if(val.length()>25){
            et_category.setError("Name is Too Large");
            return false;
        }else {
            et_category.setError(null);
            return true;
        }

    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(FindShops.this);
        builder.setMessage("Please connect to the internet")
                //.setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),FindShops.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(FindShops findShops) {

        ConnectivityManager connectivityManager = (ConnectivityManager) findShops.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }


}