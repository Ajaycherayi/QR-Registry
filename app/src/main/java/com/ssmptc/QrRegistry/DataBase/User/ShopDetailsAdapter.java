package com.ssmptc.QrRegistry.DataBase.User;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.R;
import com.ssmptc.QrRegistry.User.ShopDetailsSingleView;
import java.util.ArrayList;
import java.util.List;

public class ShopDetailsAdapter  extends RecyclerView.Adapter<ShopDetailsAdapter.ShopDetailsHolder> {

    private Context mContext;
    private List<ShopsDataForUser> shopsDataForUserList;
    private List<ShopsDataForUser> copyList;


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
       return new ShopDetailsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopDetailsAdapter.ShopDetailsHolder holder, int position) {

       ShopsDataForUser data = shopsDataForUserList.get(position);

       holder.sName.setText(data.getName());
       holder.sCategory.setText(data.getCategory());
       holder.sLocation.setText(data.getLocation());

       holder.btn_more.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String id = data.getShopId(); // Get Shop Id from ShopsDataForCustomers ShopImageUrl
               String keyId = data.getId(); // Get User DataBase Key

               Intent intent = new Intent(mContext, ShopDetailsSingleView.class);
               intent.putExtra("shopId",id); // Pass Shop Id value To ShopDetailsSingleView
               intent.putExtra("key",keyId); // Pass key value To ShopDetailsSingleView
               mContext.startActivity(intent);
               notifyDataSetChanged();
           }
       });

       holder.btn_delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               SessionManagerUser managerUser;
               managerUser = new SessionManagerUser(mContext);
               String phoneNumber = managerUser.getPhone();

               String pushKey = data.getId();

               AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

               builder.setTitle("Confirm");
               builder.setMessage("Are you sure?");

               builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                   public void onClick(DialogInterface dialog, int which) {

                       FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Shops").child(pushKey)
                               .addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       snapshot.getRef().removeValue();

                                       Toast.makeText(mContext, "Shop Details Deleted", Toast.LENGTH_SHORT).show();
                                       notifyDataSetChanged();
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {
                                       Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                               });

                       dialog.dismiss();
                   }
               });

               builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       // Do nothing
                       dialog.dismiss();
                   }
               });

               AlertDialog alert = builder.create();
               alert.show();

           }
       });




    }

    @Override
    public int getItemCount() {
        return shopsDataForUserList.size();
    }

    static public class ShopDetailsHolder extends RecyclerView.ViewHolder{

          TextView sName,sCategory,sLocation;
          Button btn_delete,btn_more;


        public ShopDetailsHolder(View itemView) {
            super(itemView);
            sName = (TextView) itemView.findViewById(R.id.tv_shopName);
            sCategory = (TextView) itemView.findViewById(R.id.tv_category);
            sLocation = (TextView) itemView.findViewById(R.id.tv_location);

            btn_delete = itemView.findViewById(R.id.btn_delete);
            btn_more =  itemView.findViewById(R.id.btn_more);


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
