package com.example.user.myapplication.MyAward;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

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

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MyAwardsActivity extends AppCompatActivity {

    private Button btnBack;
    private ListView list_awards,list_n_awards;
    private ScrollView scrollView;

    private String field;
    private String award_num;

    MyAwardsAdapter myAwardsAdapter;
    NotMyAwardsAdapter notMyAwardsAdapter;

    private JSONObject myawards_json;
    private String interested_list, uninterested_list;

    private String user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_awards);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();
        makeList();
        setEvent();
    }

    private void makeList() {

        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);

        myAwardsAdapter = new MyAwardsAdapter();
        notMyAwardsAdapter = new NotMyAwardsAdapter();

        list_awards.setAdapter(myAwardsAdapter);
        list_n_awards.setAdapter(notMyAwardsAdapter);

//        setListViewHeightBaedOnChild(list_awards);
//        setListViewHeightBaedOnChild(list_n_awards);



        try {

            // user_id로 내 어워드 리스트 가져오기
            myawards_json = new ShowMyAwards().execute(user_id).get();

            // 관심분야 리스트 가져오기
            interested_list = myawards_json.getString("interested");
            Log.d("관심 분야 ? : ", interested_list);
            // 비관심분야 리스트 가져오기
            uninterested_list = myawards_json.getString("notinterested");
            Log.d("비관심 분야 ? : ", uninterested_list);

            new loadList().execute();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setListViewHeightBaedOnChild(ListView listview) {

        ListAdapter listAdapter = listview.getAdapter();

        if(listAdapter == null)
            return;

        int numberOfItems = listAdapter.getCount();
        int totalItemsHeight = 0;
        for(int itemPos = 0; itemPos < numberOfItems; itemPos++){
            View item = listAdapter.getView(itemPos, null, listview);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        int  totalDividersHeight = listview.getDividerHeight() * (numberOfItems-1);

        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listview.setLayoutParams(params);
        listview.requestLayout();
    }

    class loadList extends AsyncTask<String,String,JSONObject>{

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected JSONObject doInBackground(String... result) {

            try {

                Log.d("loadList_interested", "func : " + interested_list);
                JSONArray interested_array = new JSONArray(interested_list);

                for(int i = 0; i < interested_array.length(); i++) {
                    JSONObject jo = interested_array.getJSONObject(i);
                    field = jo.getString("field");
                    award_num = jo.getString("award_num");
                    Log.i("field  ", field);
                    Log.i("num  ", award_num);

                    if(field.equalsIgnoreCase("movie")){
                        field = "영화";
                    }
                    else if(field.equalsIgnoreCase("animation")){
                        field = "애니메이션";
                    }
                    else if(field.equalsIgnoreCase("drama")){
                        field = "드라마";
                    }
                    else if(field.equalsIgnoreCase("novel")){
                        field = "소설";
                    }
                    else if(field.equalsIgnoreCase("comic")){
                        field = "만화";
                    }
                    else if(field.equalsIgnoreCase("novel")){
                        field = "소설";
                    }
                    else if(field.equalsIgnoreCase("webtoon")){
                        field = "웹툰";
                    }
                    else if(field.equalsIgnoreCase("act")){
                        field = "연극";
                    }
                    else if(field.equalsIgnoreCase("musical")){
                        field = "뮤지컬";
                    }
                    myAwardsAdapter.addItem(field, award_num);
                }

                Log.d("loadList_uninterested", "func : " + uninterested_list);
                JSONArray uninterested_array = new JSONArray(uninterested_list);

                for(int i = 0; i < uninterested_array.length(); i++) {
                    JSONObject jo1 = uninterested_array.getJSONObject(i);
                    field = jo1.getString("field");
                    award_num = jo1.getString("award_num");
                    Log.i("field  ", field);
                    Log.i("num  ", award_num);

                    if(field.equalsIgnoreCase("movie")){
                        field = "영화";
                    }
                    else if(field.equalsIgnoreCase("animation")){
                        field = "애니메이션";
                    }
                    else if(field.equalsIgnoreCase("drama")){
                        field = "드라마";
                    }
                    else if(field.equalsIgnoreCase("novel")){
                        field = "소설";
                    }
                    else if(field.equalsIgnoreCase("comic")){
                        field = "만화";
                    }
                    else if(field.equalsIgnoreCase("novel")){
                        field = "소설";
                    }
                    else if(field.equalsIgnoreCase("webtoon")){
                        field = "웹툰";
                    }
                    else if(field.equalsIgnoreCase("act")){
                        field = "연극";
                    }
                    else if(field.equalsIgnoreCase("musical")){
                        field = "뮤지컬";
                    }
                    else if(field.equalsIgnoreCase("music")){
                        field = "음악";
                    }


                    notMyAwardsAdapter.addItem(field , award_num);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject jObj) {
            myAwardsAdapter.notifyDataSetChanged();
            notMyAwardsAdapter.notifyDataSetChanged();
        }


    }

    private void initView() {

        list_awards = (ListView)findViewById(R.id.list_awards);
        list_n_awards = (ListView)findViewById(R.id.list_n_awards);
        btnBack = (Button)findViewById(R.id.btnBack);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

    }

    private void setEvent() {

//        // listview 스크롤 문제 해결
//        list_awards.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                scrollView.requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
//
//        // listview 스크롤 문제 해결
//        list_n_awards.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                scrollView.requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAwardsActivity.this.finish();
            }
        });

    }

    class ShowMyAwards extends AsyncTask<String, String, JSONObject> {

        private static final String urlString = Award.AWARD_URL + "Award_server/Award/mypage_myAward.jsp";
        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MyAwardsActivity.this);
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
                Log.d("myawards_test", args[0]);


                result = jsonParser.makeHttpRequest(
                        urlString, "POST", params);


                if (result != null) {
                    Log.d("myawards_test", "result : " + result);
                    return result;
                } else {
                    Log.d("myawards_test", "result : null, doInBackground");
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
