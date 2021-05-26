package com.ssmptc.QrRegistry.ToDoList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.CustomerLoginSignUp.UserDashBoard;
import com.ssmptc.QrRegistry.DataBase.SessionManagerCustomer;
import com.ssmptc.QrRegistry.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerToDoList extends AppCompatActivity {

    SessionManagerCustomer managerCustomer;

    EditText et_title,et_desc;
    FloatingActionButton add;
    ImageView btn_back;
    String phone,idUpdate="";

    public boolean isUpdate = false; //flag to check is update or is add new

    RecyclerView recyclerView;
    TodoAdapter adapter;

    private DatabaseReference db,mDatabaseRef;
    private List<TodoModel> todoModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_to_do_list);

        btn_back = findViewById(R.id.btn_backToCd);
        et_title = findViewById(R.id.et_title);
        et_desc = findViewById(R.id.et_description);
        add  = findViewById(R.id.btn_add);

        recyclerView = findViewById(R.id.toDoListView);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));

        todoModels = new ArrayList<>();

        managerCustomer = new SessionManagerCustomer(getApplicationContext());
        phone = managerCustomer.getPhone();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Todo");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerToDoList.this, UserDashBoard.class));
                finish();
            }
        });

        list();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isUpdate){
                    addNewList(et_title.getText().toString(),et_desc.getText().toString());
                }
                else{
                    updateList(et_title.getText().toString(),et_desc.getText().toString());

                    isUpdate = !isUpdate; //Reset Flag
                }

            }
        });




    }

    private void updateList(String title, String desc) {

        db = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Todo").child(String.valueOf(idUpdate));
        db.child("title").setValue(title);
        db.child("description").setValue(desc);
        todoModels.clear();
        list();
        et_desc.setText("");
        et_title.setText("");

    }

    private void addNewList(String title, String desc) {

        String id = mDatabaseRef.push().getKey();


        TodoModel model = new TodoModel(id,title,desc);

        if (id != null){
            mDatabaseRef.child(id).setValue(model);
        }

        todoModels.clear();
        et_desc.setText("");
        et_title.setText("");
        list();

    }

    public void removeItem(int position){
        TodoModel model=  todoModels.get(position);
        idUpdate = model.getId();
        db = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Todo").child(idUpdate);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerToDoList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(CustomerToDoList.this, "Item Deleted..", Toast.LENGTH_SHORT).show();
    }

    public void editItem(int position){
        TodoModel model=  todoModels.get(position);
        idUpdate = model.getId();
        db = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Todo").child(idUpdate);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                et_title.setText(title);
                String desc = snapshot.child("description").getValue(String.class);
                et_desc.setText(desc);
                isUpdate = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerToDoList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

   private void list() {


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                todoModels.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()){

                    TodoModel model = postSnapshot.getValue(TodoModel.class);
                    todoModels.add(model);
                }

                adapter = new TodoAdapter(CustomerToDoList.this,todoModels);
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
                Toast.makeText(CustomerToDoList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}