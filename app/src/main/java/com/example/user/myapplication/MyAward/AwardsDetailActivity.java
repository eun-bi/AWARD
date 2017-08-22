package com.example.user.myapplication.MyAward;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.myapplication.Award;
import com.example.user.myapplication.MyBadge.AwardBadge;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AwardsDetailActivity extends AppCompatActivity {

    private ListView list_awards;
    private Button btnCancel;
    private TextView txtAward_name;

    private AwardDetailAdapter adapter;

    private String field;
    private String user_id;

    private JSONObject awards_detail_json;
    private String awards_item_list;

    private String award_id;
    private String award_img;
    private String award_name;
    private String award_created;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awards_detail);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();
        makeList();
        setEvent();

    }

    private void setEvent() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AwardsDetailActivity.this.finish();
            }
        });

    }

    private void makeList() {

        Intent intent = getIntent();
        field = intent.getStringExtra("field");
        txtAward_name.setText(field + " 어워드");

        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);

        if(field.equalsIgnoreCase("영화")){
            field = "movie";
        }
        else if(field.equalsIgnoreCase("애니메이션")){
            field = "animation";
        }
        else if(field.equalsIgnoreCase("드라마")){
            field = "drama";
        }
        else if(field.equalsIgnoreCase("소설")){
            field = "novel";
        }
        else if(field.equalsIgnoreCase("만화")){
            field = "comic";
        }
        else if(field.equalsIgnoreCase("소설")){
            field = "novel";
        }
        else if(field.equalsIgnoreCase("웹툰")){
            field = "webtoon";
        }
        else if(field.equalsIgnoreCase("연극")){
            field = "act";
        }
        else if(field.equalsIgnoreCase("뮤지컬")){
            field = "musical";
        }

        adapter = new AwardDetailAdapter();
        list_awards.setAdapter(adapter);

        try {

            awards_detail_json = new AwardAsync().execute(user_id, field).get();
            awards_item_list = awards_detail_json.getString("award_list");
            Log.d("awards_item_list", awards_item_list);
            new loadList().execute(awards_item_list);

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

                Log.d("loadList", "func : " + awards_item_list);
                JSONArray awardsList = new JSONArray(awards_item_list);

                for(int i = 0; i < awardsList.length(); i++) {
                    JSONObject jo = awardsList.getJSONObject(i);
                    award_id = jo.getString("award_id");
                    award_img = jo.getString("award_img");
                    award_name = jo.getString("award_name");
                    award_created = jo.getString("award_created");

                    adapter.addItem(award_id,award_img,award_name,award_created);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject jObj) {
            adapter.notifyDataSetChanged();
        }


    }


    private void initView() {

        list_awards = (ListView)findViewById(R.id.list_awards);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        txtAward_name = (TextView)findViewById(R.id.txtAward_name);

    }

    class AwardAsync extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        private static final String URL = Award.AWARD_URL + "Award_server/Award/mypage_myAwardlist.jsp";

        Dialog loadingDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(AwardsDetailActivity.this, "Please wait", "Loading...");
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", args[0]);
                Log.d("user_id", args[0]);
                params.put("field", args[1]);
                Log.d("field", args[1]);

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
        }
    }


}
