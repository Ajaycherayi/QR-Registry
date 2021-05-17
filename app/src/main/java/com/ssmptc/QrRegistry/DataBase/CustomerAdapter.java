package com.ssmptc.QrRegistry.DataBase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.ShopLoginSignup.ShopCustomersList;

import java.util.List;

public class CustomerAdapter  extends RecyclerView.Adapter<CustomerAdapter.Textviewholder> {

    private Context mContext;
    private List<CustomersModel> modelList;


    public CustomerAdapter(Context context,List<CustomersModel> customersModels){
        mContext = context;
        modelList = customersModels;
    }

    @NonNull
    @Override
    public Textviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.customer_single_row,parent,false);
        return new Textviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Textviewholder holder, int position) {

        CustomersModel model = modelList.get(position);
        holder.name.setText(model.getName());
        holder.phoneNo.setText(model.getPhoneNo());

        boolean isExpandable = modelList.get(position).isExpandable();
        holder.expandable.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class Textviewholder extends RecyclerView.ViewHolder {

        TextView name, phoneNo, currentDate,currentTime;
        LinearLayout linearLayout;
        RelativeLayout expandable;

        public Textviewholder( View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nametext);
            phoneNo = (TextView) itemView.findViewById(R.id.coursetext);
           //currentDate = (TextView) itemView.findViewById(R.id.emailtext);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandable = itemView.findViewById(R.id.expandable_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CustomersModel customersList = modelList.get(getAdapterPosition());
                    customersList.setExpandable(!customersList.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }

    }
}

