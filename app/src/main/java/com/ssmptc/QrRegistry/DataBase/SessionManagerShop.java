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

        public void setDetails(String shopId, String shopName,String password) {
            editor.putString("KEY_ID", shopId);
            editor.putString("KEY_SHOP", shopName);
            editor.putString("KEY_PASSWORD", password);

            editor.commit();

        }

        public String getShopId() {
        return sharedPreferences.getString("KEY_ID", "");
    }

        public String getShopName() {
            return sharedPreferences.getString("KEY_SHOP", "");
        }

        public String getShopPassword() {
            return sharedPreferences.getString("KEY_PASSWORD", "");
        }


        public void logoutUserFromSession() {
            editor.clear();
            editor.commit();
        }
}

