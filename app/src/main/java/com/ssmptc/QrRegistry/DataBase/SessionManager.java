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
    public void setName(String name){
        editor.putString("KEY_NAME",name);
        editor.commit();

    }

    public String getName(){
        return sharedPreferences.getString("KEY_NAME","");

    }

    /*public HashMap<String, String> getUserDetailsFromSession(){

        HashMap<String,String> userData = new HashMap<String,String>();

        userData.put(KEY_NAME,userSession.getString(KEY_NAME,null));
        userData.put(KEY_EMAIL,userSession.getString(KEY_EMAIL,null));
        userData.put(KEY_PHONENUMBER,userSession.getString(KEY_PHONENUMBER,null));
        userData.put(KEY_PASSWORD,userSession.getString(KEY_PASSWORD,null));

        return userData;
    }


    public void logoutUserFromSession(){
        editor.clear();
        editor.commit();
    }*/
}
