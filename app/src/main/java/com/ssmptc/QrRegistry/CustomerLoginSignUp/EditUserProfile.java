package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.User.SessionManagerUser;
import com.ssmptc.QrRegistry.R;

public class EditUserProfile extends AppCompatActivity {

    ImageView btn_back;
    Button btn_update;

    SessionManagerUser managerUser;

    private TextInputLayout et_name,et_location,et_age,et_email,et_address;
    private RadioGroup radioGroup;
    private RadioButton rb_male,rb_female,rb_other,rb_selected;
    private TextView tv_name;
    private ProgressDialog progressDialog;

    private String phoneNumber;
    private DatabaseReference userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);

        btn_back = findViewById(R.id.btn_backToCd);
        et_name = findViewById(R.id.et_name);
        et_location = findViewById(R.id.et_location);
        et_age = findViewById(R.id.et_age);
        et_email = findViewById(R.id.et_email);
        et_address = findViewById(R.id.et_address);
        radioGroup = findViewById(R.id.radio_group);
        rb_male = findViewById(R.id.radio_male);
        rb_female = findViewById(R.id.radio_female);
        rb_other = findViewById(R.id.radio_other);
        tv_name = findViewById(R.id.tv_UserName);
        btn_update = findViewById(R.id.btn_update);

        managerUser = new SessionManagerUser(getApplicationContext());
        phoneNumber = managerUser.getPhone();

        //--------------- Internet Checking -----------
        if (!isConnected(EditUserProfile.this)){
            showCustomDialog();
        }

        callProgressDialog();

        loadData();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditUserProfile.this, UserDashBoard.class));
                finish();
            }
        });

    }

    private void callProgressDialog() {
        //--------------- Initialize ProgressDialog -----------
        progressDialog = new ProgressDialog(EditUserProfile.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }
    //-------------------------------Load data to EditTexts------------------------------------------
    private void loadData() {

        userDb = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Profile");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String _name = snapshot.child("name").getValue(String.class);
                et_name.getEditText().setText(_name);
                tv_name.setText(_name);
                String _location = snapshot.child("location").getValue(String.class);
                et_location.getEditText().setText(_location);
                String _age = snapshot.child("age").getValue(String.class);
                et_age.getEditText().setText(_age);
                String _gender = snapshot.child("gender").getValue(String.class);


                assert _gender != null;
                if (_gender.equals("Male")){
                    rb_male.setChecked(true);
                }
                if (_gender.equals("Female")){
                    rb_female.setChecked(true);
                }
                if (_gender.equals("Other")){
                    rb_other.setChecked(true);
                }

                String _email = snapshot.child("email").getValue(String.class);
                et_email.getEditText().setText(_email);
                String _address = snapshot.child("address").getValue(String.class);
                et_address.getEditText().setText(_address);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void updateData(View view) {

        //EditText Validations
        if (!validateName()  | !validateLocation() | !validateAge() | !validateGender()) {

            return;
        }

        callProgressDialog();

        userDb = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Profile");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String _name = snapshot.child("name").getValue(String.class);
                String _location = snapshot.child("location").getValue(String.class);
                String _age = snapshot.child("age").getValue(String.class);
                String _gender = snapshot.child("gender").getValue(String.class);
                String _email = snapshot.child("email").getValue(String.class);
                String _address = snapshot.child("address").getValue(String.class);

                rb_selected = findViewById(radioGroup.getCheckedRadioButtonId());


                if (_name.equals(et_name.getEditText().getText().toString()) &&
                        _location.equals(et_location.getEditText().getText().toString()) &&
                        _age.equals(et_age.getEditText().getText().toString()) &&
                        _gender.equals(rb_selected.getText().toString()) &&
                        _email.equals(et_email.getEditText().getText().toString()) &&
                        _address.equals(et_address.getEditText().getText().toString())){

                    Toast.makeText(EditUserProfile.this, "Same data no changes", Toast.LENGTH_SHORT).show();

                }else {

                    userDb.child("name").setValue(et_name.getEditText().getText().toString());
                    userDb.child("location").setValue(et_location.getEditText().getText().toString());
                    userDb.child("age").setValue(et_age.getEditText().getText().toString());
                    userDb.child("gender").setValue(rb_selected.getText().toString());
                    userDb.child("email").setValue(et_email.getEditText().getText().toString());
                    userDb.child("address").setValue(et_address.getEditText().getText().toString());

                    Toast.makeText(EditUserProfile.this, "Data Updated", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        progressDialog.dismiss();

    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserProfile.this);
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
                        startActivity(new Intent(getApplicationContext(),UserDashBoard.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(EditUserProfile editUserProfile) {

        ConnectivityManager connectivityManager = (ConnectivityManager) editUserProfile.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }

    private boolean validateName() {
        String val = et_name.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_name.setError("Field can not be empty");
            return false;
        }else if(val.length()>25){
            et_name.setError("Name is Too Large");
            return false;
        }else {
            et_name.setError(null);
            return true;
        }
    }
    private boolean validateLocation() {
        String val = et_location.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_location.setError("Field can not be empty");
            return false;
        }else {
            et_location.setError(null);
            return true;
        }
    }
    private boolean validateAge() {
        // String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String val = et_age.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_age.setError("Field can not be empty");
            return false;
        }else if(val.length()>3){
            et_age.setError("Enter Valid Age");
            return false;
        }else {
            et_age.setError(null);
            return true;
        }

    }
    private boolean validateGender(){

        if (radioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        }else
            return true;
    }

}