package com.ssmptc.QrRegistry.DataBase;

import com.google.firebase.database.Exclude;

public class Model {

    private String ImageUrl;
    private String mKey;
    public Model(){

    }
    public Model(String imageUrl){
        this.ImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }

    @Exclude
    public String getKey(){
        return mKey;
    }
    @Exclude
    public void setKey(String key){
        mKey = key;
    }
}
