package com.example.user.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by User on 2017-03-24.
 */

public class sqliteDBHelper extends SQLiteOpenHelper{

   // public static final String tableName = "user_info";

    public sqliteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE user_info" +
                " (user_id TEXT primary key, user_name TEXT, user_img_path TEXT)";
        try{
            db.execSQL(sql);
        } catch (SQLException e){
            Log.e("sql:", e.toString());
        }
   }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert (String user_id, String user_name, String user_img_path){

        SQLiteDatabase db = getWritableDatabase();


        db.execSQL("INSERT INTO user_info" +
                " VALUES('" + user_id + "', '" + user_name + "', '" + user_img_path + "');");

        db.close();

        Log.d("user_db",  "insert" + user_id + "/" + user_name + "/" + user_img_path);
    }

    public void updata (String user_id, String user_name, String user_img_path, String email){

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE user_info" + "SET " +
                    "user_name = '" + user_name + "', user_img_path = '" + user_img_path + "', email = '" + email
                    + "' WHERE user_id = '" + user_id + "';");
        db.close();

        Log.d("user_db" ,"update");

    }

    public void delete(String user_id){

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM user_info" + "WHERE user_id = '" + user_id + "';");
        db.close();

        Log.d("user_db", "delete");

    }




}
