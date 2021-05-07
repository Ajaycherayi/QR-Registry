package com.ssmptc.QrRegistry.ShopLoginSignup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ssmptc.QrRegistry.DataBase.Model;
import com.ssmptc.QrRegistry.DataBase.SessionManagerShop;
import com.ssmptc.QrRegistry.R;

public class ShopProfile extends AppCompatActivity {

    AutoCompleteTextView getList1,getList2,getList3,getList4;

    private TextInputLayout et_ShopName,et_LicenseNumber,et_category,et_location,et_phone,et_email, et_openTime,et_closeTime,et_openDay,et_closeDay,et_description;
    private Button btnUpload,btnShowAll;
    private ImageView btnChooseImg;
    private ProgressDialog progressDialog;

    //vars
    private DatabaseReference root,reference ;
    private StorageReference storageReference;
    private Uri filePath;
    private String shopId;
    private String  _ShopName,_LicenseNumber,_category,_location,_phone,_email,_openTime,_closeTime,_openDay,_closeDay,_description;
    SessionManagerShop managerShop;



    EditText desc;

    // Minimum Android Version jellybean
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile);


        btnChooseImg = findViewById(R.id.btn_chooseImage);
        btnUpload= findViewById(R.id.btn_uploadImage);
        btnShowAll= findViewById(R.id.btn_showAllImage);

        getList1 = findViewById(R.id.shop_startTimes);
        getList2 = findViewById(R.id.shop_endTimes);
        getList3 = findViewById(R.id.shop_startDay);
        getList4 = findViewById(R.id.shop_endDay);

        // Update Details
        et_ShopName = findViewById(R.id.et_shopName);
        et_LicenseNumber = findViewById(R.id.et_LicenseNumber);
        et_category = findViewById(R.id.et_category);
        et_location = findViewById(R.id.et_shopLocation);
        et_phone = findViewById(R.id.et_phone2);
        et_email = findViewById(R.id.et_email);
        et_openTime = findViewById(R.id.et_openTime);
        et_closeTime = findViewById(R.id.et_closeTime);
        et_openDay = findViewById(R.id.et_openDay);
        et_closeDay = findViewById(R.id.et_closeDay);
        et_description = findViewById(R.id.et_description);

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


        managerShop = new SessionManagerShop(getApplicationContext());
        shopId = managerShop.getShopId();

        reference = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Profile");

        root = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Images");
        storageReference = FirebaseStorage.getInstance().getReference("ShopImages").child(shopId);

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filePath!= null){
                    uploadImage(filePath);
                }else{
                    Toast.makeText(ShopProfile.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopProfile.this,ShopImages.class));
            }
        });
        dbUpdate();

    }

    private void chooseImage() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            // checking request code and result code
            // if request code is PICK_IMAGE_REQUEST and
            // resultCode is RESULT_OK
            // then set image in the image view
            if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {

                // Get the Uri of data
                filePath = data.getData();
                btnChooseImg.setImageURI(filePath);

            }

        }

    // UploadImage method
    private void uploadImage(Uri uri)
    {
        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(ShopProfile.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

        fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        progressDialog.dismiss();

                        Model model = new Model(uri.toString());
                        String modelId = root.push().getKey();
                        if (modelId != null) {
                            root.child(modelId).setValue(model);
                        }
                        Toast.makeText(ShopProfile.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        btnChooseImg.setImageResource(R.drawable.add_image1);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ShopProfile.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    public void updateData(View view) {

        dbUpdate();

        if (isNameChanged() | isCategoryChanged() | isLocationChanged() | isPhoneNumberChanged() | isLicenseChanged() | isEmailChanged() | isDescriptionChanged()){

            //managerShop.setDetails("","",_ShopName,"","","","");
            Toast.makeText(ShopProfile.this, "Data has been Updated", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(ShopProfile.this, "Data is same and can not be Updated", Toast.LENGTH_SHORT).show();
    }

    private void dbUpdate(){
        Query getShopData = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Profile");
        getShopData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                _ShopName = dataSnapshot.child("shopName").getValue(String.class);
                et_ShopName.getEditText().setText(_ShopName);
                _category = dataSnapshot.child("category").getValue(String.class);
                et_category.getEditText().setText(_category);
                _location = dataSnapshot.child("location").getValue(String.class);
                et_location.getEditText().setText(_location);
                _phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                et_phone.getEditText().setText(_phone);
                _LicenseNumber = dataSnapshot.child("licenseNumber").getValue(String.class);
                et_LicenseNumber.getEditText().setText(_LicenseNumber);
                _email = dataSnapshot.child("email").getValue(String.class);
                et_email.getEditText().setText(_email);
                _description = dataSnapshot.child("description").getValue(String.class);
                et_description.getEditText().setText(_description);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private boolean isDescriptionChanged() {
        if (!_description.equals(et_description.getEditText().getText().toString())){

            reference.child("description").setValue(et_description.getEditText().getText().toString());
            return true;
        }else
            return false;
    }

    private boolean isEmailChanged() {
        if (!_email.equals(et_email.getEditText().getText().toString())){

            reference.child("email").setValue(et_email.getEditText().getText().toString());
            return true;
        }else
            return false;
    }

    private boolean isLicenseChanged() {
        if (!_LicenseNumber.equals(et_LicenseNumber.getEditText().getText().toString())){

            reference.child("licenseNumber").setValue(et_LicenseNumber.getEditText().getText().toString());
            return true;
        }else
            return false;
    }

    private boolean isPhoneNumberChanged() {
        if (!_phone.equals(et_phone.getEditText().getText().toString())){

            reference.child("phoneNumber").setValue(et_phone.getEditText().getText().toString());
            return true;
        }else
            return false;

    }

    private boolean isLocationChanged() {
        if (!_location.equals(et_location.getEditText().getText().toString())){

            reference.child("location").setValue(et_location.getEditText().getText().toString());
            return true;
        }else
            return false;
    }

    private boolean isCategoryChanged() {
        if (!_category.equals(et_category.getEditText().getText().toString())){

            reference.child("category").setValue(et_category.getEditText().getText().toString());
            return true;
        }else
            return false;
    }

    private boolean isNameChanged() {
        if (!_ShopName.equals(et_ShopName.getEditText().getText().toString())){

            reference.child("shopName").setValue(et_ShopName.getEditText().getText().toString());
            return true;
        }else
            return false;
    }



}