package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.SessionManagerUser;
import com.ssmptc.QrRegistry.DataBase.UserData;
import com.ssmptc.QrRegistry.R;

public class CustomerVerification extends AppCompatActivity {

    private PinView get_otp;
    private Button verify_Btn;
    private String back_otp;
    private TextView show_name;
    private ImageView b1;

    SessionManagerUser managerCustomer;

    private String name,age,gender,password,phoneNo;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_verification);

        get_otp = findViewById(R.id.input_pin);
        verify_Btn = findViewById(R.id.submit_btn);
        show_name = findViewById(R.id.show);
        b1 = findViewById(R.id.btn_backSignUpPage);

        firebaseAuth = FirebaseAuth.getInstance();

       // String phone_No = getIntent().getStringExtra("phoneNo");
        back_otp = getIntent().getStringExtra("auth");

        name = getIntent().getStringExtra("name");
        age = getIntent().getStringExtra("age");
        gender = getIntent().getStringExtra("gender");
        password = getIntent().getStringExtra("password");
        phoneNo = getIntent().getStringExtra("phoneNumber");
        show_name.setText(phoneNo);

        managerCustomer = new SessionManagerUser(getApplicationContext());

        verify_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ver_code = get_otp.getText().toString();
                if (!ver_code.isEmpty()){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(back_otp,ver_code);
                    signInWithPhoneAuthCredential(credential);
                }else {

                    Toast.makeText(CustomerVerification.this, "Please Enter oTp", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CustomerVerification.this, UserSignUp.class));
                finish();

            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            storeNewUserData();

                        } else {

                            Toast.makeText(CustomerVerification.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }


    private void storeNewUserData() {

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        UserData addNewUser = new UserData(name,phoneNo,age,gender,password);
        reference.child(phoneNo).child("Profile").setValue(addNewUser);
        reference.child(phoneNo).child("Profile").child("email").setValue("");
        reference.child(phoneNo).child("Profile").child("address").setValue("");
        reference.child(phoneNo).child("phoneNo").setValue(phoneNo);



        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(phoneNo);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String _name = snapshot.child(phoneNo).child("Profile").child("name").getValue(String.class);
                String _phoneNo = snapshot.child(phoneNo).child("Profile").child("phoneNumber").getValue(String.class);
                String _password = snapshot.child(phoneNo).child("Profile").child("password").getValue(String.class);


                managerCustomer.setCustomerLogin(true);
                managerCustomer.setDetails(_name, _phoneNo, _password);

                startActivity(new Intent(getApplicationContext(), UserDashBoard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
