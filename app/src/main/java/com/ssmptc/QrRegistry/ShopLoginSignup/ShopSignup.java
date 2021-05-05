package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.CustomerDashBoard;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.CustomerSignup;
import com.ssmptc.QrRegistry.DataBase.CustomersModel;
import com.ssmptc.QrRegistry.DataBase.SessionManagerCustomer;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.DataBase.ShopsData;
import com.ssmptc.QrRegistry.R;

import java.util.concurrent.TimeUnit;

public class ShopSignup extends AppCompatActivity {

    SessionManagerShop managerShop;

    ImageView b1;
    long node = 0;
    private String nodeId;

    //Variables
    private TextInputLayout et_shopLocation,et_shopCategory,et_ownerName,et_password,et_shopName;
    private String phoneNumber,sName;
    TextView showPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_signup);

        et_shopName = findViewById(R.id.nameOfShop);
        et_shopLocation = findViewById(R.id.et_shopLocation);
        et_shopCategory = findViewById(R.id.et_category);
        et_ownerName = findViewById(R.id.et_ownerName);
        et_password = findViewById(R.id.et_shopPassword);

        showPhoneNo = findViewById(R.id.tv_ownerPhone);

        b1 = findViewById(R.id.btn_backToCd);


       managerShop = new SessionManagerShop(getApplicationContext());

        phoneNumber = getIntent().getStringExtra("phone");


        showPhoneNo.setText(phoneNumber);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopSignup.this,CustomerDashBoard.class));
                finish();
            }
        });

        setNode();


    }

    public void setNode(){

        DatabaseReference rff;
        rff = FirebaseDatabase.getInstance().getReference().child("Shops");
        rff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    node = (snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void ShopSignUp(View view) {


        if (!validateShopName() | !validateShopCategory() | !validateShopLocation() | !validateOwnerName() ) {

            return;
        }

        setNode();
        DatabaseReference rff;
        rff = FirebaseDatabase.getInstance().getReference().child("Shops");

        String shopName = et_shopName.getEditText().getText().toString();
        String location = et_shopLocation.getEditText().getText().toString();
        String category = et_shopCategory.getEditText().getText().toString();
        String ownerName = et_ownerName.getEditText().getText().toString();
        String password = et_password.getEditText().getText().toString();


        nodeId = String.valueOf(node+1000);


        Dialog dialog = new Dialog(ShopSignup.this);
        dialog.setContentView(R.layout.alert_dialog);
        CheckBox checkBox = dialog.findViewById(R.id.check_box);
        Button btCancel = dialog.findViewById(R.id.bt_cancel);
        Button btOk = dialog.findViewById(R.id.bt_ok);
        TextView shopId = dialog.findViewById(R.id.tv_shopId);

        shopId.setText(nodeId);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){

                    btOk.setBackgroundColor(getResources().getColor(R.color.light_green));
                    btOk.setEnabled(true);
                    btOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            rff.child(String.valueOf(node+1000)).child("shopId").setValue(nodeId);
                            ShopsData addNewShop = new ShopsData(nodeId,phoneNumber, shopName, location, category, ownerName, password);
                            rff.child(String.valueOf(node+1000)).child("Shop Profile").setValue(addNewShop);

                            Query shopData = FirebaseDatabase.getInstance().getReference("Shops").child(nodeId).child("Shop Profile");

                            shopData.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotData) {

                                    String _shopName = snapshotData.child("shopName").getValue(String.class);
                                    String _category = snapshotData.child("category").getValue(String.class);
                                    String _location = snapshotData.child("location").getValue(String.class);
                                    String _phoneNo = snapshotData.child("phoneNumber").getValue(String.class);
                                    String _ownerName = snapshotData.child("ownerName").getValue(String.class);
                                    String _password = snapshotData.child("password").getValue(String.class);

                                    managerShop.setShopLogin(true);
                                    managerShop.setDetails(_phoneNo, _shopName, _location, _category, _ownerName, _password);

                                    startActivity(new Intent(ShopSignup.this, ShopDashBoard.class));

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            dialog.dismiss();
                        }
                    });





                }else {
                    btOk.setEnabled(false);
                    btOk.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                }
            }
        });
        dialog.show();



    }




  /*  private boolean validateAll(){
        String val = et_shopName.getText().toString().trim();
        String checkspaces = "\\A\\w{4,20}\\z";

        if (val.isEmpty()){
            et_shopName.setError("Field can not be empty");
            return false;
        }else if(val.length()>25){
            et_shopName.setError("ShopName is too large!");
            return false;
        }else if (!val.matches(checkspaces)){
            et_shopName.setError("White spaces not allowed");
         return false;
        }else {
            et_shopName.setError(null);
            return true;
        }

    }*/
    private boolean validateShopName(){
        String val = et_shopName.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_shopName.setError("Field can not be empty");
            return false;
        }else if(val.length()>25){
            et_shopName.setError("ShopName is too large!");
            return false;
        }else {
            et_shopName.setError(null);
            et_shopName.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validateShopLocation(){
        String val = et_shopLocation.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_shopLocation.setError("Field can not be empty");
            return false;
        }else {
            et_shopLocation.setError(null);
            et_shopLocation.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validateShopCategory(){
        String val = et_shopCategory.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_shopCategory.setError("Field can not be empty");
            return false;
        }else {
            et_shopCategory.setError(null);
            et_shopCategory.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validateOwnerName(){
        String val = et_ownerName.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_ownerName.setError("Field can not be empty");
            return false;
        }else {
            et_ownerName.setError(null);
            et_ownerName.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validatePassword(){
        String val = et_password.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_password.setError("Field can not be empty");
            return false;
        }else if(val.length() < 8 ){
            et_password.setError("password should contain 8 characters!");
            return false;
        }else {
            et_password.setError(null);
            et_password.setErrorEnabled(false);
            return true;
        }

    }

}