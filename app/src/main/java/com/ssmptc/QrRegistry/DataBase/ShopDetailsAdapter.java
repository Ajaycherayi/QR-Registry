package com.ssmptc.QrRegistry.DataBase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssmptc.QrRegistry.CustomerLoginSignUp.ShopDetails;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.ToDoList.TodoAdapter;
import com.ssmptc.QrRegistry.ToDoList.TodoModel;

import java.util.List;

public class ShopDetailsAdapter  extends RecyclerView.Adapter<ShopDetailsAdapter.ShopDetailsHolder> {

    private Context mContext;
    private List<ShopsDataForCustomers> shopsDataForCustomersList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onCallClick(int position);
        void onMessageClick(int position);
        void onMoreClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

   public ShopDetailsAdapter(Context context , List<ShopsDataForCustomers> detailsModels){
       mContext = context;
       shopsDataForCustomersList = detailsModels;
   }

    @NonNull
    @Override
    public ShopDetailsAdapter.ShopDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View v = LayoutInflater.from(mContext).inflate(R.layout.list_shop_details,parent,false);
       return new ShopDetailsHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopDetailsAdapter.ShopDetailsHolder holder, int position) {

       ShopsDataForCustomers data = shopsDataForCustomersList.get(position);
       holder.sName.setText(data.getName());
       holder.sCategory.setText(data.getCategory());
       holder.sLocation.setText(data.getLocation());
    }

    @Override
    public int getItemCount() {
        return shopsDataForCustomersList.size();
    }

    static public class ShopDetailsHolder extends RecyclerView.ViewHolder{

          TextView sName,sCategory,sLocation;
          Button btn_call,btn_msg,btn_more;


        public ShopDetailsHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            sName = (TextView) itemView.findViewById(R.id.tv_shopName);
            sCategory = (TextView) itemView.findViewById(R.id.tv_category);
            sLocation = (TextView) itemView.findViewById(R.id.tv_location);

            btn_call = itemView.findViewById(R.id.btn_call);
            btn_msg=itemView.findViewById(R.id.btn_msg);
            btn_more =  itemView.findViewById(R.id.btn_more);

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

            btn_msg.setOnClickListener(new View.OnClickListener() {
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

            btn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onMoreClick(position);
                        }
                    }
                }
            });


        }
    }
}
