package com.ssmptc.QrRegistry.DataBase.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.ssmptc.QrRegistry.DataBase.Shop.ShopImageUrl;
import com.ssmptc.QrRegistry.R;
import java.util.List;

public class ShowImageInUserAdapter extends RecyclerView.Adapter<ShowImageInUserAdapter.ImageViewHolder> {

    private final Context mContext;
    private final List<ShopImageUrl> shopImageUrlList;

    public ShowImageInUserAdapter(Context context, List<ShopImageUrl> shopImageUrls) {
        mContext = context;
        shopImageUrlList = shopImageUrls;
    }

    @NonNull
    @Override
    public ShowImageInUserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.shop_image_list,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowImageInUserAdapter.ImageViewHolder holder, int position) {

        ShopImageUrl uploadCurrent = shopImageUrlList.get(position);
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.drawable.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return shopImageUrlList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.show_images);

        }
    }
}
