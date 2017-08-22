package com.example.user.myapplication.MakeAward;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MakeAwardActivity2 extends AppCompatActivity {

    private Button btnNext, btnCancel;
    private ListView badge_listView;

    private String Award_Name;
    private Uri selPhotoUri;
    private String field;

    private BadgeListAdapter adapter;
    private ArrayList<AwardBadge> badgeList = null;

    LayoutInflater inflater;

    private JSONObject badge_json;
    String badge_list;

    private String user_id;
    private String badge_id;

    private static final String Badge_URL = Award.AWARD_URL + "Award_server/Award/makeaward_selectBadge.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_award2);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);

        Intent intent = getIntent();
        Award_Name = intent.getStringExtra("Award_Name");
        selPhotoUri = intent.getParcelableExtra("Award_img");
        field = intent.getStringExtra("Award_field");


        initView();
        makeList();
        setEvent();

        Toast.makeText(MakeAwardActivity2.this,"작품명: " + Award_Name ,Toast.LENGTH_SHORT).show();
        Toast.makeText(MakeAwardActivity2.this,"이미지: " + selPhotoUri ,Toast.LENGTH_SHORT).show();

    }

    private void makeList() {

        badgeList = new ArrayList<>();

        adapter = new BadgeListAdapter(this,R.layout.activity_list_badge, badgeList);
        badge_listView.setAdapter(adapter);
        badge_listView.setTextFilterEnabled(true);
        badge_listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        try {

            badge_json = new ShowMyBadge().execute(user_id, field).get(); // field,
            badge_list = badge_json.getString("Badges");
            Log.d("badge_test : ", badge_list);
            new loadList().execute(badge_list);

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
                Gson gson = new Gson();
                Log.d("loadList", "func : " + badge_list);
                JSONArray badgeListObj = new JSONArray(badge_list);

                for(int i = 0; i < badgeListObj.length(); i++) {
                    String badgeInfo = badgeListObj.getJSONObject(i).toString();
                    Log.d("badge_test", badgeInfo);
                    AwardBadge badge_List = gson.fromJson(badgeInfo, AwardBadge.class);
                    badgeList.add(badge_List);
                    adapter.add(badge_List);

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

    private void setEvent() {

        badge_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                btnNext.setTextColor(Color.parseColor("#da9a7aC"));
                badge_id = badgeList.get(position).getBadge_id();
                adapter.notifyDataSetChanged();

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(badge_id)){
                    btnNext.setEnabled(false);
//                    btnNext.setTextColor(Color.parseColor("#3dffffff"));
                }

                else{
                    Intent intent = new Intent(MakeAwardActivity2.this, MakeAwardActivity3.class);
                    intent.putExtra("Award_Name",Award_Name);
                    intent.putExtra("Award_img",selPhotoUri);
                    intent.putExtra("Award_field", field);
                    intent.putExtra("Award_badge", badge_id);
                    Log.d("badge_id", badge_id);

                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakeAwardActivity2.this, MakeAwardActivity1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                MakeAwardActivity2.this.finish();
            }
        });
    }

    private void initView() {
        badge_listView = (ListView)findViewById(R.id.badge_listView);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }


    // badgelist를 불러옴
    class ShowMyBadge extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MakeAwardActivity2.this);
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
                Log.d("badge_test", args[0]);
                params.put("field", args[1]);
                Log.d("badge_test", args[1]);


                result = jsonParser.makeHttpRequest(
                        Badge_URL, "POST", params);


                if (result != null) {
                    Log.d("badge_test", "result : " + result);
                    return result;
                } else {
                    Log.d("badge_test", "result : null, doInBackground");
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
}
