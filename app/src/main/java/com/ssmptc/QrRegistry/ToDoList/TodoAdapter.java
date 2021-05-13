package com.ssmptc.QrRegistry.ToDoList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssmptc.QrRegistry.DataBase.CustomersModel;
import com.ssmptc.QrRegistry.R;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.Todoviewholder>{

    private Context mContext;
    private List<TodoModel> todoList;

    public TodoAdapter(Context context, List<TodoModel> todoModels){
        mContext = context;
        todoList = todoModels;

    }
    @NonNull
    @Override
    public Todoviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_todo,parent,false);
        return new Todoviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Todoviewholder holder, int position) {

        TodoModel model = todoList.get(position);
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());

    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    class Todoviewholder extends RecyclerView.ViewHolder {

        TextView title,description;

        public Todoviewholder( View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_title);
            description = itemView.findViewById(R.id.tv_description);

        }
    }
}
