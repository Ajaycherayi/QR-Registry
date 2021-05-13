package com.ssmptc.QrRegistry.ToDoList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.CustomerAdapter;
import com.ssmptc.QrRegistry.DataBase.CustomersModel;
import com.ssmptc.QrRegistry.DataBase.SessionManagerCustomer;
import com.ssmptc.QrRegistry.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerToDoList extends AppCompatActivity {

    SessionManagerCustomer managerCustomer;

    EditText et_title,et_desc;
    ImageButton add;

    ///RecyclerView recyclerView;
    ///TodoAdapter adapter;

    private DatabaseReference mDatabaseRef;
    private List<TodoModel> todoModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_to_do_list);

        et_title = findViewById(R.id.et_title);
        et_desc = findViewById(R.id.et_description);
        add  = findViewById(R.id.btn_add);

        //recyclerView = findViewById(R.id.toDoListView);
       // recyclerView.setHasFixedSize(true);
       // recyclerView.setLayoutManager(new LinearLayoutManager(this));

        todoModels = new ArrayList<>();

        managerCustomer = new SessionManagerCustomer(getApplicationContext());
        String phone = managerCustomer.getPhone();




        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sTitle = et_title.getText().toString();
                String sDesc = et_desc.getText().toString();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Todo");
                TodoModel model = new TodoModel(sTitle,sDesc);
                db.push().setValue(model);

                mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(phone).child("Todo");


            }
        });




    }

   /* private void list() {

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    TodoModel model = postSnapshot.getValue(TodoModel.class);
                    todoModels.add(model);
                }

                adapter = new TodoAdapter(CustomerToDoList.this,todoModels);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerToDoList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}