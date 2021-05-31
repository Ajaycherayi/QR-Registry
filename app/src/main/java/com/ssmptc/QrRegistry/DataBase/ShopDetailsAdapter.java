package com.ssmptc.QrRegistry.DataBase;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssmptc.QrRegistry.R;

import java.util.ArrayList;
import java.util.List;

public class ShopDetailsAdapter  extends RecyclerView.Adapter<ShopDetailsAdapter.ShopDetailsHolder> {

    private Context mContext;
    private List<ShopsDataForUser> shopsDataForUserList;
    private List<ShopsDataForUser> copyList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onCallClick(int position);
        void onMessageClick(int position);
        void onMoreClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

   public ShopDetailsAdapter(Context context , List<ShopsDataForUser> detailsModels){
       mContext = context;
       shopsDataForUserList = detailsModels;
       this.copyList = new ArrayList<>();
       copyList.addAll(detailsModels);
   }

    @NonNull
    @Override
    public ShopDetailsAdapter.ShopDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View v = LayoutInflater.from(mContext).inflate(R.layout.shop_details_list,parent,false);
       return new ShopDetailsHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopDetailsAdapter.ShopDetailsHolder holder, int position) {

       ShopsDataForUser data = shopsDataForUserList.get(position);
       holder.sName.setText(data.getName());
       holder.sCategory.setText(data.getCategory());
       holder.sLocation.setText(data.getLocation());
    }

    @Override
    public int getItemCount() {
        return shopsDataForUserList.size();
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


    public void Search(CharSequence txt) {

        List<ShopsDataForUser> searchList = new ArrayList<>();

        if (!TextUtils.isEmpty(txt)){
            for (ShopsDataForUser data : shopsDataForUserList){
                if (data.getName().toLowerCase().contains(txt) || data.getLocation().toLowerCase().contains(txt) || data.getCategory().toLowerCase().contains(txt)){
                    searchList.add(data);
                }
            }
        }else {
            searchList.addAll(copyList);
        }
        shopsDataForUserList.clear();
        shopsDataForUserList.addAll(searchList);
        notifyDataSetChanged();
        searchList.clear();
    }

}
