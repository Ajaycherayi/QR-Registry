package com.ssmptc.QrRegistry.DataBase;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagerShop {


        // Initialize Variables
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        public SessionManagerShop(Context context) {
            sharedPreferences = context.getSharedPreferences("ShopAppKey", 0);
            editor = sharedPreferences.edit();
            editor.apply();
        }

        // Set Login
        public void setShopLogin(boolean login) {
            editor.putBoolean("KEY_LOGIN_SHOP", login);
            editor.commit();
        }

        public boolean getShopLogin() {

            return sharedPreferences.getBoolean("KEY_LOGIN_SHOP", false);
        }

        public void setDetails(String phoneNo, String shopName, String location, String category, String ownerName, String password) {
            editor.putString("KEY_PHONE", phoneNo);
            editor.putString("KEY_SHOP", shopName);
            editor.putString("KEY_LOCATION", location);
            editor.putString("KEY_CATEGORY",category);
            editor.putString("KEY_NAME", ownerName);
            editor.putString("KEY_PASSWORD", password);

            editor.commit();

        }


        public String getPhone() {
        return sharedPreferences.getString("KEY_PHONE", "");
    }

        public String getShopName() {
            return sharedPreferences.getString("KEY_SHOP", "");
        }

        public String getLocation() {
            return sharedPreferences.getString("KEY_LOCATION", "");
        }

        public String getCategory() {
            return sharedPreferences.getString("KEY_CATEGORY", "");
        }

        public String getOwnerName() {
        return sharedPreferences.getString("KEY_NAME", "");
    }

        public String getShopPassword() {
            return sharedPreferences.getString("KEY_PASSWORD", "");
        }


        public void logoutUserFromSession() {
            editor.clear();
            editor.commit();
        }
}

