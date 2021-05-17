package com.ssmptc.QrRegistry.ToDoList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssmptc.QrRegistry.DataBase.CustomersModel;
import com.ssmptc.QrRegistry.DataBase.ShopAdapter;
import com.ssmptc.QrRegistry.R;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.Todoviewholder>{

    private Context mContext;
    private List<TodoModel> todoList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public TodoAdapter(Context context, List<TodoModel> todoModels){
        mContext = context;
        todoList = todoModels;

    }
    @NonNull
    @Override
    public Todoviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_todo,parent,false);
        return new Todoviewholder(v,mListener,todoList);
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

   public class Todoviewholder extends RecyclerView.ViewHolder {

        TextView title,description;
        ImageView btn_edit,btn_delete;

        public Todoviewholder(View itemView, OnItemClickListener listener, List<TodoModel> todoList) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_title);
            description = itemView.findViewById(R.id.tv_description);


            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);


          btn_edit.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (listener != null){
                      int position = getAdapterPosition();
                      if (position != RecyclerView.NO_POSITION){
                          listener.onEditClick(position);
                      }
                  }

              }
          });

          btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }

                }
            });

        }


   }

}
