package com.example.user.myapplication.MyBadge;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.MyAward.AwardDetailAdapter;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class BadgeDetailActivity extends AppCompatActivity {

    Button btnCancel;
    private ImageView img_badge;
    TextView txtBadge_name,txtBadge_explain,txtAward_num;
    ListView list_mybadge;

    AwardDetailAdapter adapter;

    private String badge_name;
    private String award_num;
    private String badge_id;
    private String badge_icon;
    private String badge_tag;

    private String award_id;
    private String award_img;
    private String award_name;
    private String award_created;

    private String user_id;

    private JSONObject badges_detail_json;
    private String badges_item_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_detail);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();
        makeList();
        setEvent();
    }

    private void makeList() {

        Intent intent = getIntent();
        badge_name = intent.getStringExtra("badge_name");
        txtBadge_name.setText(badge_name);

        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);

        adapter = new AwardDetailAdapter();
        list_mybadge.setAdapter(adapter);

        try {

            badges_detail_json = new BadgeAsync().execute(user_id, badge_name).get();
            badges_item_list = badges_detail_json.getString("myBadgeDetail");
            Log.d("badges_item_list", badges_item_list);
            new loadList().execute(badges_item_list);


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

                Log.d("loadList", "func : " + badges_item_list);

                JSONObject jo = new JSONObject(badges_item_list);

                award_num = jo.getString("counts");
                badge_id = jo.getString("badgeid");
                badge_icon = jo.getString("badgeicon");
                badge_tag = jo.getString("badgetag");

                Log.i("시상?", award_num);
                Log.i("시상?", badge_id);
                Log.i("시상?", badge_icon);
                Log.i("시상?", badge_tag);

                String badge_award_list = jo.getString("badge_award_list");
                Log.i("시상?", badge_award_list);

                JSONArray ja2 = new JSONArray(badge_award_list); // 뱃지 배열
                for (int j = 0; j < ja2.length(); j++) {
                    JSONObject jo1 = ja2.getJSONObject(j);
                    award_id = jo1.getString("award_id");
                    award_img = jo1.getString("award_img");
                    award_name = jo1.getString("award_name");
                    award_created = jo1.getString("award_created");

                    Log.i("z", award_id);
                    Log.i("z", award_img);
                    Log.i("z", award_name);
                    Log.i("z", award_created);

                    adapter.addItem(award_id,award_img,award_name,award_created);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(JSONObject jObj) {

            txtBadge_explain.setText(badge_tag);
            txtAward_num.setText(award_num);

            adapter.notifyDataSetChanged();

        }


    }

    private void setEvent() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BadgeDetailActivity.this.finish();
            }
        });
    }

    class BadgeAsync extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        private static final String URL = Award.AWARD_URL + "Award_server/Award/mypage_badgeDetail.jsp";

        Dialog loadingDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(BadgeDetailActivity.this, "Please wait", "Loading...");
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", args[0]);
                Log.d("user_id", args[0]);
                params.put("badge_name", args[1]);
                Log.d("badge_name", args[1]);

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

    private void initView() {

        btnCancel = (Button)findViewById(R.id.btnCancel);
        img_badge = (ImageView)findViewById(R.id.img_badge);
        txtBadge_name = (TextView)findViewById(R.id.txtBadge_name);
        txtAward_num = (TextView)findViewById(R.id.txtAward_num);
        txtBadge_explain = (TextView)findViewById(R.id.txtBadge_explain);
        list_mybadge = (ListView)findViewById(R.id.list_mybadge);

    }
}
