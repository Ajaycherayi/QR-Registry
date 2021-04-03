package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.ssmptc.QrRegistry.R;

public class ShopProfile extends AppCompatActivity {

    AutoCompleteTextView getList1,getList2,getList3,getList4;
    
    private Button upload;
    private ImageView imageView;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private Uri imageUri;


    EditText desc;

    // Minimum Android Version jellybean
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile);
        
        imageView = findViewById(R.id.btn_chooseImage);
        upload = findViewById(R.id.btn_uploadImage);

        getList1 = findViewById(R.id.shop_startTimes);
        getList2 = findViewById(R.id.shop_endTimes);
        getList3 = findViewById(R.id.shop_startDay);
        getList4 = findViewById(R.id.shop_endDay);



        String [] start = {"7:00 AM" , "8:00 AM" , "9:00 AM" , "10:00 AM"};
        String [] end = {"5:00 PM" , "6:00 PM" , "7:00 PM" , "8:00 PM" , "9:00 PM" , "10:00 PM"};

        String [] startDay = {"Monday" , "Tuesday" , "Wednesday" , "Thursday", "Friday", "Saturday", "Sunday"};
        String [] endDay = {"Monday" , "Tuesday" , "Wednesday" , "Thursday", "Friday", "Saturday", "Sunday"};


        ArrayAdapter<String> arrayAdapter1 =new ArrayAdapter<String>(this,R.layout.shop_time_list,start);
        ArrayAdapter<String> arrayAdapter2 =new ArrayAdapter<String>(this,R.layout.shop_time_list,end);
        ArrayAdapter<String> arrayAdapter3 =new ArrayAdapter<String>(this,R.layout.shop_time_list,startDay);
        ArrayAdapter<String> arrayAdapter4 =new ArrayAdapter<String>(this,R.layout.shop_time_list,endDay);

        getList1.setAdapter(arrayAdapter1);
        getList2.setAdapter(arrayAdapter2);
        getList3.setAdapter(arrayAdapter3);
        getList4.setAdapter(arrayAdapter4);
        
        
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (imageUri != null){

                    uploadImage(imageUri);

                }else {

                    Toast.makeText(ShopProfile.this, "Please Select Image", Toast.LENGTH_SHORT).show();

                }
            }
        });



    }

    private void uploadImage(Uri uri) {

       // StorageReference fleRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
    }





    private void chooseImage() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

    }
}