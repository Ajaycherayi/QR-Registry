package com.ssmptc.QrRegistry.CustomerLoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ssmptc.QrRegistry.R;

import java.util.Objects;

public class CustomerVerification extends AppCompatActivity {

    private PinView get_otp;
    private Button verify_Btn;
    private String back_otp;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_verification);

        get_otp = findViewById(R.id.input_pin);

        verify_Btn = findViewById(R.id.submit_btn);

        firebaseAuth = FirebaseAuth.getInstance();

       // String phone_No = getIntent().getStringExtra("phoneNo");
        back_otp = getIntent().getStringExtra("auth");

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

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(CustomerVerification.this,CustomerDashBoard.class);
                            startActivity(intent);
                            // ...
                        } else {

                            Toast.makeText(CustomerVerification.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }



}
