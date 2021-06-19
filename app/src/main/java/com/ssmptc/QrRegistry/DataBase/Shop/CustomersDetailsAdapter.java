package com.ssmptc.QrRegistry.DataBase.Shop;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.R;
import java.util.ArrayList;
import java.util.List;

public class CustomersDetailsAdapter extends RecyclerView.Adapter<CustomersDetailsAdapter.TextViewHolder> {

    private Context mContext;
    private List<CustomerDataForShopList> customerDataForShop;
    private List<CustomerDataForShopList> copyList;



    public CustomersDetailsAdapter(Context context, List<CustomerDataForShopList> customerDataForShopLists){
        mContext = context;
        customerDataForShop = customerDataForShopLists;
        this.copyList = new ArrayList<>();
        copyList.addAll(customerDataForShopLists);
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.customer_details_single_view,parent,false);
        return new TextViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder,final int position) {

        CustomerDataForShopList model = customerDataForShop.get(position);
        holder.tv_name.setText(model.getName());
        holder.tv_phoneNumber.setText(model.getPhoneNumber());
        holder.tv_date.setText(model.getCurrentDate());
        holder.tv_time.setText(model.getCurrentTime());
        holder.tv_age.setText(model.getAge());
        holder.tv_gender.setText(model.getGender());
        holder.tv_location.setText(model.getLocation());

        holder.btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SessionManagerShop managerShop;
                managerShop = new SessionManagerShop(mContext);
                final String shopId = managerShop.getShopId();


                final Dialog dialog= new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.contacts);


                Button btn_email = dialog.findViewById(R.id.btn_sendMail);
                Button btn_locate = dialog.findViewById(R.id.btn_locate);
                Button btn_call = dialog.findViewById(R.id.btn_call);
                Button btn_msg= dialog.findViewById(R.id.btn_msg);

                btn_locate.setVisibility(View.GONE);

                String email = model.getEmail();

                if (email.equals("")){

                    btn_email.setVisibility(View.GONE);

                }else{

                    btn_email.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String phoneNumber = model.getPhoneNumber();

                            FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Profile")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String email = snapshot.child("email").getValue(String.class);
                                            assert email != null;

                                            Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                                            Uri data = Uri.parse("mailto:?subject=" + "QR Registry"+ "&body=" + "Hai," + "&to=" + email);
                                            emailIntent.setData(data);
                                            mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    });

                }



                    btn_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String id = model.getId();


                        FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").child(id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String number = snapshot.child("phoneNumber").getValue(String.class);
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + number));
                                mContext.startActivity(callIntent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                btn_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String id = model.getId();

                        FirebaseDatabase.getInstance().getReference("Shops").child(shopId).child("Customers").child(id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String number = snapshot.child("phoneNumber").getValue(String.class);
                                Intent MessageIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
                                mContext.startActivity(MessageIntent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setWindowAnimations(R.style.BottomDialog);
                dialog.getWindow().setGravity(Gravity.BOTTOM);

            }
        });


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

    public class TextViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_phoneNumber, tv_time, tv_date, tv_age, tv_gender, tv_address, tv_email,tv_location;
        TextView hint_address, hint_email;
        Button btn_contact;
        LinearLayout linearLayout;
        RelativeLayout expandable;

        public TextViewHolder(View itemView) {
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


            btn_contact = itemView.findViewById(R.id.btn_contact);

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

        }



    }

   public void Search(CharSequence txt) {

        List<CustomerDataForShopList> searchList = new ArrayList<>();

        if (!TextUtils.isEmpty(txt)){
            for (CustomerDataForShopList data : customerDataForShop){
                if (data.getName().toLowerCase().contains(txt) || data.getAge().toLowerCase().contains(txt) || data.getCurrentTime().toLowerCase().contains(txt)
                        || data.getAge().toLowerCase().contains(txt) || data.getLocation().toLowerCase().contains(txt)){
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

