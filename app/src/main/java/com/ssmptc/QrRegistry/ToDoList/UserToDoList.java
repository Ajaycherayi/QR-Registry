package com.ssmptc.QrRegistry.ToDoList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.Shop.EditShopProfile;
import com.ssmptc.QrRegistry.User.UserDashBoard;
import com.ssmptc.QrRegistry.DataBase.User.SessionManagerUser;
import com.ssmptc.QrRegistry.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserToDoList extends AppCompatActivity {

    SessionManagerUser managerUser;

    FloatingActionButton btn_add;
    ImageView btn_back;

    private RecyclerView recyclerView;

    private String selectedDate;
    private ProgressDialog progressDialog;
    private DatabaseReference todoDb;
    private List<TodoModel> todoModels;
    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_to_do_list);

        btn_back = findViewById(R.id.btn_backToCd);
        recyclerView = findViewById(R.id.toDoListView);
        btn_add = findViewById(R.id.btn_add);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        todoModels = new ArrayList<>();

        managerUser = new SessionManagerUser(getApplicationContext());
        String phoneNumber = managerUser.getPhone();

        todoDb = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Todo");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy>0){
                    btn_add.hide();
                }else {
                    btn_add.show();
                }

                super.onScrolled(recyclerView, dx, dy);

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserToDoList.this, UserDashBoard.class));
                finish();
            }
        });

        //Initialize ProgressDialog
        loadProgressDialog();

        list(); // Load all data

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                final Dialog dialog= new Dialog(UserToDoList.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_todo_add_and_update);

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setWindowAnimations(R.style.BottomDialog);
                dialog.getWindow().setGravity(Gravity.BOTTOM);

                Button btn_selectDate = dialog.findViewById(R.id.btn_selectDate);
                EditText et_title = dialog.findViewById(R.id.et_title);
                EditText et_desc = dialog.findViewById(R.id.et_description);
                Button btn_new  = dialog.findViewById(R.id.bt_ok);


                btn_selectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UserToDoList.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month  = month+1;

                        String fd= ""+day;
                        String fm = ""+month;
                        if(day<10){
                            fd = "0"+day;
                        }
                        if (month<10){
                            fm = "0"+month;
                        }

                        selectedDate = fd+"/"+fm+"/"+year;

                        btn_selectDate.setText(selectedDate);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });


                btn_new.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                        if (et_title.getText().toString().isEmpty() | et_desc.getText().toString().isEmpty()){
                            Toast.makeText(UserToDoList.this, "Do not empty Title and Description", Toast.LENGTH_SHORT).show();
                        }else if (btn_selectDate.getText().toString().isEmpty()){
                            Toast.makeText(UserToDoList.this, "Please select a date", Toast.LENGTH_SHORT).show();
                        }else {


                            String date = btn_selectDate.getText().toString();

                            String nTitle = et_title.getText().toString();
                            String nDesc = et_desc.getText().toString();

                            String[] separated = date.split("/");

                            String newDay = separated[0];
                            String newMonth = separated[1];
                            String newYear = separated[2];

                            String id = todoDb.push().getKey();
                            TodoModel model = new TodoModel(id, nTitle, nDesc, newDay, newMonth, newYear);

                            if (id != null) {
                                todoDb.child(id).setValue(model);
                            }

                            dialog.dismiss();
                            Toast.makeText(UserToDoList.this, "New item listed", Toast.LENGTH_SHORT).show();
                            loadProgressDialog();
                            list();

                        }

                    }
                });

            }

        });





    }

    //-----------------------Progress Dialog-------------------
    private void loadProgressDialog() {

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(UserToDoList.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    private void list() {

        todoDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                todoModels.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    TodoModel model = postSnapshot.getValue(TodoModel.class);
                    todoModels.add(model);
                }

                adapter = new TodoAdapter(UserToDoList.this, todoModels);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserToDoList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}