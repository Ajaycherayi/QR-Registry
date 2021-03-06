package com.ssmptc.QrRegistry.DataBase.User;

import android.content.Context;
import android.content.SharedPreferences;
public class SessionManagerUser {

    // Initialize Variables
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SessionManagerUser(Context context){
        sharedPreferences = context.getSharedPreferences("CustomerAppKey",0);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    // Set Button
    public void setShopButton(boolean disable){
        editor.putBoolean("KEY_BUTTON",disable);
        editor.commit();
    }

    public boolean getShopButton(){

        return sharedPreferences.getBoolean("KEY_BUTTON",false);
    }


    // Set Login
    public void setCustomerLogin(boolean login){
            editor.putBoolean("KEY_LOGIN",login);
            editor.commit();
    }

    public boolean getCustomerLogin(){

        return sharedPreferences.getBoolean("KEY_LOGIN",false);
    }
    public void setDetails(String name,String phoneNo,String password){
        editor.putString("KEY_NAME",name);
        editor.putString("KEY_PHONE",phoneNo);
        editor.putString("KEY_PASSWORD",password);

        editor.commit();

    }

    public String getName(){
        return sharedPreferences.getString("KEY_NAME","");
    }
    public String getPhone(){
        return sharedPreferences.getString("KEY_PHONE","");
    }
    public String getPassword(){
        return sharedPreferences.getString("KEY_PASSWORD","");
    }



    public void logoutUserFromSession(){
        editor.clear();
        editor.commit();
    }
}
