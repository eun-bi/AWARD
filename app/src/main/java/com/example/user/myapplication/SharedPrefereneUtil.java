package com.example.user.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by User on 2017-05-09.
 */
public class SharedPrefereneUtil {

    private final String Pref_name = "login_pref";
    static Context mContext;

    public SharedPrefereneUtil(Context context){

        mContext = context;

    }

    /* 자동로그인을 위한 로그인여부 체크 */
    public void putLoginchk(String key, boolean b){

        SharedPreferences pref = mContext.getSharedPreferences(Pref_name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key,b);
        editor.commit();

    }

    public boolean getLoginchk(String key, boolean b){

        SharedPreferences pref = mContext.getSharedPreferences(Pref_name,Context.MODE_PRIVATE);
        return pref.getBoolean(key, b);

    }

    public void putSharedPreferences(String key, String value){

        SharedPreferences pref = mContext.getSharedPreferences(Pref_name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,value);
        editor.commit();

    }

    public String getSharedPreferences(String key, String value) {

        SharedPreferences pref = mContext.getSharedPreferences(Pref_name,Context.MODE_PRIVATE);

        try{
            return pref.getString(key, value);
        }catch (Exception e){
            return value;
        }

    }

    public void removeSharedPreferences(String key){

        SharedPreferences pref = mContext.getSharedPreferences(Pref_name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();

    }

}
