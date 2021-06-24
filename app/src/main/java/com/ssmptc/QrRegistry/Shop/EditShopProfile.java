package com.ssmptc.QrRegistry.Shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssmptc.QrRegistry.DataBase.Shop.SessionManagerShop;
import com.ssmptc.QrRegistry.DataBase.Shop.ShopImageUrl;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.User.ShopDetailsSingleView;
import com.ssmptc.QrRegistry.User.ShowShopImages;

import java.util.Objects;

public class EditShopProfile extends AppCompatActivity {


    TextView tv_shopName,tv_shopId;
    ImageView btn_back;
    Button btn_upload,btn_showAll,btn_update;

    private TextInputLayout et_ShopName,et_category,et_location,et_ownerName,et_email,et_time,et_days ,et_description;
    private ImageView btn_chooseImg;
    private ProgressDialog progressDialog;

    //vars
    private DatabaseReference root,reference ;
    private StorageReference storageReference;
    private Uri filePath;
    private String shopId;
    private String  _ShopName,_category,_location,_ownerName,_email,_time,_days,_description;
    SessionManagerShop managerShop;


    // Minimum Android Version jellybean
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_shop_profile);

        tv_shopName = findViewById(R.id.tv_shopName);
        tv_shopId = findViewById(R.id.tv_shopId);
        btn_back = findViewById(R.id.btn_backToSd);

        btn_update = findViewById(R.id.btn_update);
        btn_chooseImg = findViewById(R.id.btn_chooseImage);
        btn_upload= findViewById(R.id.btn_uploadImage);
        btn_showAll= findViewById(R.id.btn_showAllImage);

        // Update Details
        et_ShopName = findViewById(R.id.et_shopName);
        et_category = findViewById(R.id.et_category);
        et_location = findViewById(R.id.et_shopLocation);
        et_ownerName = findViewById(R.id.et_ownerName);
        et_email = findViewById(R.id.et_email);
        et_time = findViewById(R.id.et_time);
        et_days = findViewById(R.id.et_Days);
        et_description = findViewById(R.id.et_description);

        //--------------- Internet Checking -----------
        if (!isConnected(EditShopProfile.this)){
            showCustomDialog();
        }

        managerShop = new SessionManagerShop(getApplicationContext());
        shopId = managerShop.getShopId();

        tv_shopName.setText(managerShop.getShopName());
        tv_shopId.setText(managerShop.getShopId());

        reference = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Profile");

        root = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Images");
        storageReference = FirebaseStorage.getInstance().getReference("ShopImages").child(shopId);

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(EditShopProfile.this,ShopDashBoard.class));
            finishAffinity();
        });

        btn_update.setOnClickListener(v -> {

            loadProgressDialog();

            if (!validateShopName() | !validateCategory() | !validateOwnerName() | !validateLocation() | !validateEmail()) {

                return;
            }

            dbUpdate();

            if (isNameChanged() | isCategoryChanged() | isLocationChanged() | isPhoneNumberChanged() | isEmailChanged() | isDescriptionChanged() | isTimeChanged() | isDayChanged()){

                Toast.makeText(EditShopProfile.this, "Data has been Updated", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(EditShopProfile.this, "Data is same and can not be Updated", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        });

        btn_chooseImg.setOnClickListener(v -> chooseImage());

        btn_upload.setOnClickListener(v -> {

            if (filePath!= null){
                uploadImage(filePath);
            }else{
                Toast.makeText(EditShopProfile.this, "Please Select Image", Toast.LENGTH_SHORT).show();
            }
        });

        btn_showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference("Shops").child(shopId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("Shop Images")){
                                    startActivity(new Intent(EditShopProfile.this,ShopImages.class));
                                }
                                else {
                                    Toast.makeText(EditShopProfile.this, "Images are not uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        loadProgressDialog();

        dbUpdate();

    }

    //-----------------------Progress Dialog-------------------
    private void loadProgressDialog() {

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(EditShopProfile.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    //---------------------Choose Image -----------------------
    private void chooseImage() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//-- checking request code and result code if request code is PICK_IMAGE_REQUEST and resultCode is RESULT_OK then set image in the image view--

            if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                // Get the Uri of data
                filePath = data.getData();
                btn_chooseImg.setImageURI(filePath);

            }

        }

    // UploadImage method
    private void uploadImage(Uri uri) {

        loadProgressDialog();

        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));


        /*-----------------------------------------------------------------------------------------------------------------------------
               fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
       -------------------------------------------------------------------------------------------------------------------------------*/

        fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {

            ShopImageUrl shopImageUrl = new ShopImageUrl(uri1.toString());
            String modelId = root.push().getKey();
            if (modelId != null) {
                root.child(modelId).setValue(shopImageUrl);
            }

            progressDialog.dismiss();
            Toast.makeText(EditShopProfile.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
            filePath = null;
            btn_chooseImg.setImageResource(R.drawable.ic_add_image);

            /*-------------------------------------------------------------------------------
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
             -------------------------------------------------------------------------------*/

        })).addOnProgressListener(snapshot -> {

        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(EditShopProfile.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
        });
    }

    //--------------------Get image file extension------------------
    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    //--------------------Update Shop Data------------------
    private void dbUpdate(){


        Query getShopData = FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Shop Profile");
        getShopData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                _ShopName = dataSnapshot.child("shopName").getValue(String.class);
                tv_shopName.setText(_ShopName);
                Objects.requireNonNull(et_ShopName.getEditText()).setText(_ShopName);

                _category = dataSnapshot.child("category").getValue(String.class);
                Objects.requireNonNull(et_category.getEditText()).setText(_category);

                _location = dataSnapshot.child("location").getValue(String.class);
                Objects.requireNonNull(et_location.getEditText()).setText(_location);

                _ownerName = dataSnapshot.child("ownerName").getValue(String.class);
                Objects.requireNonNull(et_ownerName.getEditText()).setText(_ownerName);

                _email = dataSnapshot.child("email").getValue(String.class);
                Objects.requireNonNull(et_email.getEditText()).setText(_email);

                _description = dataSnapshot.child("description").getValue(String.class);
                Objects.requireNonNull(et_description.getEditText()).setText(_description);

                _time = dataSnapshot.child("workingTime").getValue(String.class);
                Objects.requireNonNull(et_time.getEditText()).setText(_time);

                _days = dataSnapshot.child("workingDays").getValue(String.class);
                Objects.requireNonNull(et_days.getEditText()).setText(_days);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //-----------------------------Check data are changed or updated --------------------------
    private boolean isTimeChanged() {

        if (!_time.equals(Objects.requireNonNull(et_time.getEditText()).getText().toString().trim())){
            reference.child("workingTime").setValue(et_time.getEditText().getText().toString().trim());
            return true;
        }else
            return false;
    }
    private boolean isDayChanged() {

        if (!_days.equals(Objects.requireNonNull(et_days.getEditText()).getText().toString().trim())){
            reference.child("workingDays").setValue(et_days.getEditText().getText().toString().trim());
            return true;
        }else
            return false;

    }
    private boolean isDescriptionChanged() {
        if (!_description.equals(Objects.requireNonNull(et_description.getEditText()).getText().toString().trim())){

            reference.child("description").setValue(et_description.getEditText().getText().toString().trim());
            return true;
        }else
            return false;
    }
    private boolean validateEmail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String val = Objects.requireNonNull(et_email.getEditText()).getText().toString().trim();

        if (val.equals("")){
            et_email.setError(null);
            return true;
        }else if (!val.matches(emailPattern)){
            et_email.setError("Enter Valid Email");
            return false;
        } else {
            et_email.setError(null);
            return true;
        }

    }
    private boolean isEmailChanged() {

        if (!_email.equals(Objects.requireNonNull(et_email.getEditText()).getText().toString().trim())){

            reference.child("email").setValue(et_email.getEditText().getText().toString().trim());
            return true;
        }else
            return false;
    }
    private boolean isPhoneNumberChanged() {
        if (!_ownerName.equals(Objects.requireNonNull(et_ownerName.getEditText()).getText().toString().trim())){

            reference.child("ownerNameNumber").setValue(et_ownerName.getEditText().getText().toString().trim());
            return true;
        }else
            return false;

    }
    private boolean isLocationChanged() {
        if (!_location.equals(Objects.requireNonNull(et_location.getEditText()).getText().toString().trim())){

            reference.child("location").setValue(et_location.getEditText().getText().toString().trim());
            return true;
        }else
            return false;
    }
    private boolean isCategoryChanged() {
        if (!_category.equals(Objects.requireNonNull(et_category.getEditText()).getText().toString().trim())){

            reference.child("category").setValue(et_category.getEditText().getText().toString().trim());
            return true;
        }else
            return false;
    }
    private boolean isNameChanged() {
        if (!_ShopName.equals(Objects.requireNonNull(et_ShopName.getEditText()).getText().toString().trim())){

            reference.child("shopName").setValue(et_ShopName.getEditText().getText().toString().trim());
            return true;
        }else
            return false;
    }

    //----------------------------- Validate input Text Required ------------------------------

    private boolean validateShopName(){
        String val1 = Objects.requireNonNull(et_ShopName.getEditText()).getText().toString().trim();

        if (val1.isEmpty()){
            et_ShopName.setError("Field can not be empty");
            return false;
        }
        else{
            et_ShopName.setError(null);
            et_ShopName.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validateLocation(){
        String val1 = Objects.requireNonNull(et_location.getEditText()).getText().toString().trim();

        if (val1.isEmpty()){
            et_location.setError("Field can not be empty");
            return false;
        }
        else{
            et_location.setError(null);
            et_location.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validateCategory(){
        String val1 = Objects.requireNonNull(et_category.getEditText()).getText().toString().trim();

        if (val1.isEmpty()){
            et_category.setError("Field can not be empty");
            return false;
        }
        else{
            et_category.setError(null);
            et_category.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validateOwnerName(){
        String val1 = Objects.requireNonNull(et_ownerName.getEditText()).getText().toString().trim();

        if (val1.isEmpty()){
            et_ownerName.setError("Field can not be empty");
            return false;
        }
        else{
            et_ownerName.setError(null);
            et_ownerName.setErrorEnabled(false);
            return true;
        }

    }

    //--------------- Internet Error Dialog Box -----------
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditShopProfile.this);
        builder.setMessage("Please connect to the internet")
                //.setCancelable(false)
                .setPositiveButton("Connect", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), ShopDashBoard.class));
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------- Check Internet Is Connected -----------
    private boolean isConnected(EditShopProfile editShopProfile) {

        ConnectivityManager connectivityManager = (ConnectivityManager) editShopProfile.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetoothConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected() || (bluetoothConn != null && bluetoothConn.isConnected())); // if true ,  else false

    }

}