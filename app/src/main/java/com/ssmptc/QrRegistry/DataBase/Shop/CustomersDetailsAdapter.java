package com.ssmptc.QrRegistry.DataBase.Shop;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssmptc.QrRegistry.R;

import java.util.ArrayList;
import java.util.List;

public class CustomersDetailsAdapter extends RecyclerView.Adapter<CustomersDetailsAdapter.Textviewholder> {

    private Context mContext;
    private List<CustomerDataForShopList> customerDataForShop;
    private List<CustomerDataForShopList> copyList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onCallClick(int position);
        void onMessageClick(int position);
        void onEmailClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public CustomersDetailsAdapter(Context context, List<CustomerDataForShopList> customerDataForShopLists){
        mContext = context;
        customerDataForShop = customerDataForShopLists;
        this.copyList = new ArrayList<>();
        copyList.addAll(customerDataForShopLists);
    }

    @NonNull
    @Override
    public Textviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.customer_details_single_view,parent,false);
        return new Textviewholder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Textviewholder holder, int position) {

        CustomerDataForShopList model = customerDataForShop.get(position);
        holder.tv_name.setText(model.getName());
        holder.tv_phoneNumber.setText(model.getPhoneNumber());
        holder.tv_date.setText(model.getCurrentDate());
        holder.tv_time.setText(model.getCurrentTime());
        holder.tv_age.setText(model.getAge());
        holder.tv_gender.setText(model.getGender());
        holder.tv_location.setText(model.getLocation());

        String address = model.getAddress();
        String email = model.getEmail();

        if (address.equals("")){
            holder.tv_address.setVisibility(View.GONE);
            holder.hint_address.setVisibility(View.GONE);
        }else {
            holder.tv_address.setText(model.getAddress());
        }

        if (email.equals("")){
            holder.tv_email.setVisibility(View.GONE);
            holder.hint_email.setVisibility(View.GONE);
        }else {
            holder.tv_email.setText(model.getEmail());
        }

        boolean isExpandable = customerDataForShop.get(position).isExpandable();
        holder.expandable.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return customerDataForShop.size();
    }

    class Textviewholder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_phoneNumber, tv_time, tv_date, tv_age, tv_gender, tv_address, tv_email,tv_location;
        TextView hint_address, hint_email;
        Button btn_call, btn_message, btn_email;
        LinearLayout linearLayout;
        RelativeLayout expandable;

        public Textviewholder(View itemView, OnItemClickListener listener) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phoneNumber = itemView.findViewById(R.id.tv_phoneNumber);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_age = itemView.findViewById(R.id.tv_age);
            tv_gender = itemView.findViewById(R.id.tv_gender);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_location = itemView.findViewById(R.id.tv_location);

            hint_address = itemView.findViewById(R.id.hint_address);
            hint_email = itemView.findViewById(R.id.hint_email);

            btn_call = itemView.findViewById(R.id.btn_call);
            btn_message = itemView.findViewById(R.id.btn_msg);
            btn_email = itemView.findViewById(R.id.btn_email);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandable = itemView.findViewById(R.id.expandable_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CustomerDataForShopList customersList = customerDataForShop.get(getAdapterPosition());
                    customersList.setExpandable(!customersList.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onCallClick(position);
                        }
                    }
                }
            });

            btn_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onMessageClick(position);
                        }
                    }
                }
            });

            btn_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onEmailClick(position);
                        }
                    }
                }
            });
        }

    }

    public void Search(CharSequence txt) {

        List<CustomerDataForShopList> searchList = new ArrayList<>();

        if (!TextUtils.isEmpty(txt)){
            for (CustomerDataForShopList data : customerDataForShop){
                if (data.getName().toLowerCase().contains(txt) || data.getName().toLowerCase().contains(txt) || data.getCurrentTime().toLowerCase().contains(txt)){
                    searchList.add(data);
                }
            }
        }else {
            searchList.addAll(copyList);
        }
        customerDataForShop.clear();
        customerDataForShop.addAll(searchList);
        notifyDataSetChanged();
        searchList.clear();
    }

}

