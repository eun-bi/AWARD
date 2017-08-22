package com.example.user.myapplication.MyField;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.example.user.myapplication.Award;
import com.example.user.myapplication.MainActivity;
import com.example.user.myapplication.MyAward.MyAwardsActivity;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MyFieldActivity extends AppCompatActivity {

    private Button btnBack, btnRegister;
    private ToggleButton btnMovie, btnAni, btnDrama, btnNovel, btnMusical,btnPlay, btnComic, btnWebtoon;

    private ArrayList<String> fieldList = new ArrayList<String>();

    private JSONObject field_json;
    private String field_list;

    private String user_id;
    private String field;
    private ArrayList<String> myField = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_field);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initVIew();
        setView();
        setEvent();
    }

    private void setView() {

        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);

        try {

            // user_id로 리스트 가져오기
            field_json = new ShowMyAwards().execute(user_id).get();

            field_list = field_json.getString("field");
            Log.d("관심 분야 ? : ", field_list);
            new loadList().execute(field_list);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class loadList extends AsyncTask<String,String,JSONObject>{

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected JSONObject doInBackground(String... result) {

            try {

                Log.d("loadList", "func : " + field_list);
                JSONArray interested_array = new JSONArray(field_list);

                for(int i = 0; i < interested_array.length(); i++) {
                    field = interested_array.getString(i);
                    myField.add(field);
                    Log.i("field  ", field);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject jObj) {

            // 내 관심 분야 불러와서 표시
            for(int i=0; i< myField.size(); i++){
                final String interField = myField.get(i);
                Log.i("myfield",interField);
                if(interField.equals("movie")){
                    btnMovie.setBackground(getDrawable(R.drawable.movie_act));
                    fieldList.add("movie");
                    btnMovie.setChecked(true);
                }
                if(interField.equals("animation")){
                    btnAni.setBackground(getDrawable(R.drawable.ani_act));
                    fieldList.add("animation");
                    btnAni.setChecked(true);
                }
                if(interField.equals("drama")){
                    btnDrama.setBackground(getDrawable(R.drawable.drama_act));
                    fieldList.add("drama");
                    btnDrama.setChecked(true);

                }
                if(interField.equals("novel")){
                    btnNovel.setBackground(getDrawable(R.drawable.fiction_act));
                    fieldList.add("novel");
                    btnNovel.setChecked(true);
                }
                if(interField.equals("musical")){
                    btnMusical.setBackground(getDrawable(R.drawable.musical_act));
                    fieldList.add("musical");
                    btnMusical.setChecked(true);
                }
                if(interField.equals("act")){
                    btnPlay.setBackground(getDrawable(R.drawable.play_act));
                    fieldList.add("act");
                    btnPlay.setChecked(true);
                }
                if(interField.equals("comic")){
                    btnComic.setBackground(getDrawable(R.drawable.toon_act));
                    fieldList.add("comic");
                    btnComic.setChecked(true);
                }
                if(interField.equals("webtoon")){
                    btnWebtoon.setBackground(getDrawable(R.drawable.webtoon_act));
                    fieldList.add("webtoon");
                    btnWebtoon.setChecked(true);
                }
            }
        }


    }

    class ShowMyAwards extends AsyncTask<String, String, JSONObject> {

        private static final String urlString = Award.AWARD_URL + "Award_server/Award/mypage_myinterested.jsp";
        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MyFieldActivity.this);
            pDialog.setMessage("Getting your data... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected JSONObject doInBackground(String... args) {


            JSONObject result = null;

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", args[0]);
                Log.d("myfield_test", args[0]);


                result = jsonParser.makeHttpRequest(
                        urlString, "POST", params);


                if (result != null) {
                    Log.d("myfield_test", "result : " + result);
                    return result;
                } else {
                    Log.d("myfield_test", "result : null, doInBackground");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(JSONObject jObj) {

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

        } // AsyncTask 끝
    }


    private void setEvent() {

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MyFieldActivity.this);
                builder.setMessage("관심 분야를 변경하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new FieldSetAsync().execute();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.setTitle("AWARD");
                alert.setIcon(R.drawable.logo);
                alert.show();
            }

        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFieldActivity.this.finish();
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

    }

    class FieldSetAsync extends AsyncTask<String, String, JSONObject> {


        JSONParser jsonParser = new JSONParser();
        private static final String URL = Award.AWARD_URL + "Award_server/Award/login_interested.jsp";

        Dialog loadingDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(MyFieldActivity.this, "Please wait", "변경 사항을 저장 중입니다.");
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
            loadingDialog.dismiss();

            MyFieldActivity.this.finish();
        }
    }


    private void initVIew() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnMovie = (ToggleButton)findViewById(R.id.btnMovie);
        btnAni = (ToggleButton)findViewById(R.id.btnAni);
        btnDrama = (ToggleButton)findViewById(R.id.btnDrama);
        btnNovel = (ToggleButton)findViewById(R.id.btnNovel);
        btnMusical = (ToggleButton)findViewById(R.id.btnMusical);
        btnPlay = (ToggleButton)findViewById(R.id.btnPlay);
        btnComic = (ToggleButton)findViewById(R.id.btnComic);
        btnWebtoon = (ToggleButton)findViewById(R.id.btnWebtoon);
    }

}
