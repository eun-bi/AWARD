package com.example.user.myapplication;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.user.myapplication.network.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FieldSetActivity extends AppCompatActivity {

    Button btnFieldSet, btnBack;
    ToggleButton btnMovie, btnAni, btnDrama, btnNovel, btnMusical,btnPlay, btnComic, btnWebtoon;

    String user_id;
    String user_img_path;
    String user_name;

   ArrayList<String> fieldList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_set);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initVIew();
        setView();
        setEvent();
    }

    private void setView() {

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(getApplicationContext());
        user_name = prefereneUtil.getUser_name();
        user_img_path = prefereneUtil.getUser_img_path();
        user_id = prefereneUtil.getUser_id();

    }

    private void setEvent() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FieldSetActivity.this.finish();
            }
        });

        btnMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnMovie.isChecked()) {
                    btnMovie.setBackground(getDrawable(R.drawable.movie_act));
                    fieldList.add("movie");
                }else{
                    btnMovie.setBackground(getDrawable(R.drawable.movie_deact));
                    fieldList.remove("movie");
                }

            }
        });

        btnAni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnAni.isChecked()) {
                    btnAni.setBackground(getDrawable(R.drawable.ani_act));
                    fieldList.add("animation");
                }else{
                    btnAni.setBackground(getDrawable(R.drawable.ani_deact));
                    fieldList.remove("animation");
                }

            }
        });

        btnDrama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnDrama.isChecked()) {
                    btnDrama.setBackground(getDrawable(R.drawable.drama_act));
                    fieldList.add("drama");
                }else{
                    btnDrama.setBackground(getDrawable(R.drawable.drama_deact));
                    fieldList.remove("drama");
                }

            }
        });

        btnNovel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnNovel.isChecked()) {
                    btnNovel.setBackground(getDrawable(R.drawable.fiction_act));
                    fieldList.add("novel");
                }else{
                    btnNovel.setBackground(getDrawable(R.drawable.fiction_deact));
                    fieldList.remove("novel");
                }

            }
        });

        btnMusical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnMusical.isChecked()) {
                    btnMusical.setBackground(getDrawable(R.drawable.musical_act));
                    fieldList.add("musical");
                }else{
                    btnMusical.setBackground(getDrawable(R.drawable.musical_deact));
                    fieldList.remove("musical");
                }

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnPlay.isChecked()) {
                    btnPlay.setBackground(getDrawable(R.drawable.play_act));
                    fieldList.add("act");
                }else{
                    btnPlay.setBackground(getDrawable(R.drawable.play_deact));
                    fieldList.remove("act");
                }

            }
        });

        btnComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnComic.isChecked()) {
                    btnComic.setBackground(getDrawable(R.drawable.toon_act));
                    fieldList.add("comic");
                }else{
                    btnComic.setBackground(getDrawable(R.drawable.toon_deact));
                    fieldList.remove("comic");
                }

            }
        });

        btnWebtoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnWebtoon.isChecked()) {
                    btnWebtoon.setBackground(getDrawable(R.drawable.webtoon_act));
                    fieldList.add("webtoon");
                }else{
                    btnWebtoon.setBackground(getDrawable(R.drawable.webtoon_deact));
                    fieldList.remove("webtoon");
                }

            }
        });


        btnFieldSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FieldSetAsync().execute();
            }
        });

    }

    private void initVIew() {
        btnFieldSet = (Button)findViewById(R.id.btnFieldSet);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnMovie = (ToggleButton)findViewById(R.id.btnMovie);
        btnAni = (ToggleButton)findViewById(R.id.btnAni);
        btnDrama = (ToggleButton)findViewById(R.id.btnDrama);
        btnNovel = (ToggleButton)findViewById(R.id.btnNovel);
        btnMusical = (ToggleButton)findViewById(R.id.btnMusical);
        btnPlay = (ToggleButton)findViewById(R.id.btnPlay);
        btnComic = (ToggleButton)findViewById(R.id.btnComic);
        btnWebtoon = (ToggleButton)findViewById(R.id.btnWebtoon);
    }

    class FieldSetAsync extends AsyncTask<String, String, JSONObject> {


        JSONParser jsonParser = new JSONParser();
        private static final String URL = Award.AWARD_URL + "Award_server/Award/login_interested.jsp";

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                JSONArray ja = new JSONArray();

                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", user_id);

                for(int i=0; i < fieldList.size() ;i++){

                    JSONObject jo = new JSONObject();

                    jo.put("field_name", fieldList.get(i));
                    Log.d("field_name", fieldList.get(i));
                    Log.d("Test",jo.toString());
                    ja.put(jo);
                }

                params.put("field_list",ja.toString());
                Log.d("list",ja.toString());

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        URL, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute (JSONObject jsonObject){

            Intent intent = new Intent(FieldSetActivity.this, MainActivity.class);
            startActivity(intent);
            FieldSetActivity.this.finish();
        }
    }

}
