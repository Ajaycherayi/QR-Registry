package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.ssmptc.QrRegistry.DataBase.SessionManagerCustomer;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.DataBase.ShopsData;
import com.ssmptc.QrRegistry.R;

import java.util.concurrent.TimeUnit;

public class ShopSignup extends AppCompatActivity {
    
    //SessionManagerCustomer managerCustomer;
    //SessionManagerShop managerShop;

    //Variables
    private TextInputLayout et_shopLocation,et_shopCategory,et_ownerName,et_password,et_shopName;
    private Button shopLogin,shop_SignUp;
    private String phoneNumber;
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

        shopLogin = findViewById(R.id.btn_goShopLogin);
        showPhoneNo = findViewById(R.id.tv_ownerPhone);
        shop_SignUp= findViewById(R.id.bt_signUp);

      // managerCustomer = new SessionManagerCustomer(getApplicationContext());
       // managerShop = new SessionManagerShop(getApplicationContext());

        phoneNumber = getIntent().getStringExtra("phone");

        showPhoneNo.setText(phoneNumber);

       // String _ownerName = managerCustomer.getName();

       // et_ownerName.setText(_ownerName);
        //showPhoneNo.setText(phoneNumber);



        shopLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ShopLogin.class));
            }
        });

    }

    public void ShopSignUp(View view) {


        Query checkUser = FirebaseDatabase.getInstance().getReference("Shops").orderByChild("phoneNumber").equalTo(phoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    Toast.makeText(ShopSignup.this, "This User already Exist  Please Login", Toast.LENGTH_LONG).show();

                } else {

                    if (!validateShopName() | !validateShopCategory() | !validateShopLocation() | !validateOwnerName() | !validatePassword()) {

                        return;
                    }


                    String shopName = et_shopName.getEditText().getText().toString();
                    String location = et_shopLocation.getEditText().getText().toString();
                    String category = et_shopCategory.getEditText().getText().toString();
                    String ownerName = et_ownerName.getEditText().getText().toString();
                    String password = et_password.getEditText().getText().toString();



                    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                    DatabaseReference reference = rootNode.getReference("Shops");

                    ShopsData addNewShop = new ShopsData(phoneNumber, shopName, location, category, ownerName, password);
                    reference.child(phoneNumber).setValue(addNewShop);

                    Query checkShop = FirebaseDatabase.getInstance().getReference("Shops").orderByChild("phoneNumber").equalTo(phoneNumber);

                    checkShop.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String _shopName = snapshot.child(phoneNumber).child("shopName").getValue(String.class);
                            String _category = snapshot.child(phoneNumber).child("category").getValue(String.class);
                            String _location = snapshot.child(phoneNumber).child("location").getValue(String.class);
                            String _phoneNo = snapshot.child(phoneNumber).child("ownerName").getValue(String.class);
                            String _ownerName = snapshot.child(phoneNumber).child("phoneNumber").getValue(String.class);
                            String _password = snapshot.child(phoneNumber).child("password").getValue(String.class);


                            //managerShop.setShopLogin(true);

                            // managerShop.setDetails(_phoneNo, _shopName, _location ,_category,_ownerName, _password);

                            Intent intent = new Intent(ShopSignup.this, ShopDashBoard.class);
                            intent.putExtra("shopName", _shopName);
                            startActivity(intent);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






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