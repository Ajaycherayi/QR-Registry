package com.ssmptc.QrRegistry.DataBase;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    // Initialize Variables
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SessionManager(Context context){
        sharedPreferences = context.getSharedPreferences("AppKey",0);
        editor = sharedPreferences.edit();
        editor.apply();
    }
    // Set Login
    public void setLogin(boolean login){
            editor.putBoolean("KEY_LOGIN",login);
            editor.commit();
    }

    public boolean  getLogin(){

        return sharedPreferences.getBoolean("KEY_LOGIN",false);
    }
    public void setDetails(String name,String email,String phoneNo,String password){
        editor.putString("KEY_NAME",name);
        editor.putString("KEY_EMAIL",email);
        editor.putString("KEY_PHONE",phoneNo);
        editor.putString("KEY_PASSWORD",password);

        editor.commit();

    }

    public String getName(){
        return sharedPreferences.getString("KEY_NAME","");
    }
    public String getEmail(){
                return sharedPreferences.getString("KEY_EMAIL","");
    }
    public String getPhone(){
        return sharedPreferences.getString("KEY_PHONE","");
    }



    public void logoutUserFromSession(){
        editor.clear();
        editor.commit();
    }
}
