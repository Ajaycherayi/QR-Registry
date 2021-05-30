package com.ssmptc.QrRegistry.ToDoList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ssmptc.QrRegistry.R;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder>{

    private final Context mContext;
    private final List<TodoModel> todoList;
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
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_todo,parent,false);
        return new TodoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {

        TodoModel model = todoList.get(position);

        holder.tv_title.setText(model.getTitle());
        holder.tv_description.setText(model.getDescription());
        holder.tv_day.setText(model.getDay());
        holder.tv_month.setText(model.getMonth());
        holder.tv_year.setText(model.getYear());

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    if (position != RecyclerView.NO_POSITION){
                        mListener.onDeleteClick(position);
                    }
                }

            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    if (position != RecyclerView.NO_POSITION){
                        mListener.onEditClick(position);
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title,tv_description,tv_day,tv_month,tv_year;
        ImageView btn_delete;
        LinearLayout btn_edit;

        public TodoViewHolder(View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_day = itemView.findViewById(R.id.tv_day);
            tv_month = itemView.findViewById(R.id.tv_month);
            tv_year = itemView.findViewById(R.id.tv_year);


            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
   }
}
