package com.ssmptc.QrRegistry.ToDoList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.UserDashBoard;
import com.ssmptc.QrRegistry.DataBase.User.SessionManagerUser;
import com.ssmptc.QrRegistry.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class UserToDoList extends AppCompatActivity {

    SessionManagerUser managerUser;

    FloatingActionButton btn_add;
    ImageView btn_back;

    private Button btn_selectDate;
    private TextInputLayout et_title;
    private EditText et_desc;
    private RecyclerView recyclerView;

    private String phoneNumber,selectedDate,idUpdate="";
    public boolean isUpdate = false; //flag to check is update or is add new

    private DatabaseReference db,todoDb;
    private List<TodoModel> todoModels;
    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_to_do_list);

        btn_back = findViewById(R.id.btn_backToCd);
        et_title = findViewById(R.id.et_title);
        et_desc = findViewById(R.id.et_description);
        btn_add  = findViewById(R.id.btn_add);
        btn_selectDate  = findViewById(R.id.btn_selectDate);
        recyclerView = findViewById(R.id.toDoListView);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

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

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        todoModels = new ArrayList<>();

        managerUser = new SessionManagerUser(getApplicationContext());
        phoneNumber = managerUser.getPhone();

        todoDb = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Todo");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserToDoList.this, UserDashBoard.class));
                finish();
            }
        });

        list(); // Load all data

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isUpdate){
                    addNewList();
                }
                else{
                    updateList();
                    isUpdate = !isUpdate; //Reset Flag
                }

            }
        });

    }

    private void updateList() {

        //EditText Validations
        if (validateTitle() | validateDescription() | validateDate()) {

            return;
        }

        String date = btn_selectDate.getText().toString();

        String nTitle = et_title.getEditText().getText().toString();
        String nDesc = et_desc.getText().toString();

        String[] separated = date.split("/");

        String day = separated[0];
        String month = separated[1];
        String year = separated[2];

        db = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Todo").child(String.valueOf(idUpdate));
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String tempTitle = snapshot.child("title").getValue(String.class);
                String tempDesc = snapshot.child("description").getValue(String.class);
                String tempDay = snapshot.child("day").getValue(String.class);
                String tempMonth = snapshot.child("month").getValue(String.class);
                String tempYear = snapshot.child("year").getValue(String.class);

                if ((tempDesc.equals(nDesc)) && (tempTitle.equals(nTitle)) && (tempDay.equals(day)) && (tempMonth.equals(month)) && (tempYear.equals(year))){

                    et_desc.setText("");
                    et_title.getEditText().setText("");
                    btn_selectDate.setText("");
                    Toast.makeText(UserToDoList.this, "Data are same", Toast.LENGTH_SHORT).show();

                }else {

                    db.child("title").setValue(nTitle);
                    db.child("description").setValue(nDesc);
                    db.child("day").setValue(day);
                    db.child("month").setValue(month);
                    db.child("year").setValue(year);
                    todoModels.clear();
                    list();
                    et_desc.setText("");
                    et_title.getEditText().setText("");
                    btn_selectDate.setText("");
                    Toast.makeText(UserToDoList.this, "Item Updated..", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addNewList() {

        //EditText Validations
        if (validateTitle() | validateDescription() | validateDate()) {

            return;
        }

        String date = btn_selectDate.getText().toString();

        String nTitle = et_title.getEditText().getText().toString();
        String nDesc = et_desc.getText().toString();

        String[] separated = date.split("/");

        String day = separated[0];
        String month = separated[1];
        String year = separated[2];

        String id = todoDb.push().getKey();
        TodoModel model = new TodoModel(id,nTitle,nDesc,day,month,year);

        if (id != null){
            todoDb.child(id).setValue(model);
        }

        todoModels.clear();
        et_desc.setText("");
        Objects.requireNonNull(et_title.getEditText()).setText("");
        btn_selectDate.setText("");
        list();
    }

    public void removeItem(int position){
        TodoModel model=  todoModels.get(position);
        idUpdate = model.getId();
        db = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Todo").child(idUpdate);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                adapter.notifyDataSetChanged();

                et_desc.setText("");
                Objects.requireNonNull(et_title.getEditText()).setText("");
                btn_selectDate.setText("");

                Toast.makeText(UserToDoList.this, "Item Deleted..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserToDoList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editItem(int position){
        TodoModel model=  todoModels.get(position);
        idUpdate = model.getId();
        db = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Todo").child(idUpdate);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String title = snapshot.child("title").getValue(String.class);
                Objects.requireNonNull(et_title.getEditText()).setText(title);

                String desc = snapshot.child("description").getValue(String.class);
                et_desc.setText(desc);

                String day = snapshot.child("day").getValue(String.class);
                String month = snapshot.child("month").getValue(String.class);
                String year = snapshot.child("year").getValue(String.class);

                String date = day + "/" + month + "/" + year ;
                btn_selectDate.setText(date);

                isUpdate = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserToDoList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                adapter.setOnItemClickListener(new TodoAdapter.OnItemClickListener() {
                    @Override
                    public void onEditClick(int position) {
                        editItem(position);
                    }

                    @Override
                    public void onDeleteClick(int position) {
                        removeItem(position);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserToDoList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateTitle() {
        String val = et_title.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            et_title.setError("Field can not be empty");
            return true;
        }else if(val.length()>25){
            et_title.setError("Title is too large");
            return true;
        }else {
            et_title.setError(null);
            return false;
        }
    }
    private boolean validateDescription() {
        String val = et_desc.getText().toString().trim();

        if (val.isEmpty()){
            et_desc.setError("Field can not be empty");
            return true;
        }else {
            et_desc.setError(null);
            return false;
        }
    }
    private boolean validateDate() {
        String val = btn_selectDate.getText().toString().trim();

        if (val.isEmpty()){
            btn_selectDate.setError("Field can not be empty");
            return true;
        }else {
            btn_selectDate.setError(null);
            return false;
        }
    }

}