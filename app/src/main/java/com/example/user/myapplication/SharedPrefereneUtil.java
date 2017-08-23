package com.example.user.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by User on 2017-05-09.
 */
public class SharedPrefereneUtil {

    private static final String Pref_name = "login_pref";

    private SharedPreferences sharedPreferences;
    private Editor editor;


    public SharedPrefereneUtil(Context context){

        this.sharedPreferences = context.getSharedPreferences(Pref_name,Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

    }

    /* 자동로그인을 위한 로그인여부 체크 */
    public void putLoginchk(boolean b){

        editor.putBoolean("login_chk",b);
        editor.commit();

    }

    public boolean getLoginchk(){

        // login_chk에 대한 값이 없을 경우 false 리턴
        return sharedPreferences.getBoolean("login_chk",false);
    }

    /* 로그인 상태 (카톡) */
    public void putLoginkt(boolean b){

        editor.putBoolean("login_kt",b);
        editor.commit();

    }

    public boolean getLoginkt(){
        return sharedPreferences.getBoolean("login_kt",false);
    }

    /* 로그인 상태 (페북) */
    public void putLoginfb(boolean b){

        editor.putBoolean("login_fb",b);
        editor.commit();

    }

    public boolean getLoginfb(){
        return sharedPreferences.getBoolean("login_fb",false);
    }

    /* 사용자 정보 저장 */
    public void putUser_id(String user_id){
        editor.putString("user_id",user_id);
        editor.commit();
    }

    public void putSharedPreferences(String user_name, String user_img_path){
        editor.putString("user_name",user_name);
        editor.putString("user_img_path",user_img_path);
        editor.commit();
    }

    /* 사용자 정보 get */
    public String getUser_id(){
        return sharedPreferences.getString("user_id","");
    }

    public String getUser_name(){
        return sharedPreferences.getString("user_name","");
    }

    public String getUser_img_path(){
        return sharedPreferences.getString("user_img_path","");
    }

    /* logout */
    public void isUserLogout(){
        editor.putString("user_id","");
//        boolean isUserid = sharedPreferences.getString("user_id","").isEmpty();
        editor.commit();
    }

    public void removeSharedPreferences(String key){

        editor.remove(key);
        editor.commit();

    }

}
