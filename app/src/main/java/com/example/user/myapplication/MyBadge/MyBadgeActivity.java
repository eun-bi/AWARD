package com.example.user.myapplication.MyBadge;

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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.user.myapplication.Award;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyBadgeActivity extends AppCompatActivity {

    public ExpandableListView badge_interested_expandableList, badge_uninterested_expandableList;
    public LikeExpandableListViewAdapter mlikeExpListViewAdapter;
    public unlikeExpandableListViewAdapter unlikeExpandableListViewAdapter;

    private Button btnBack;
    private ScrollView scrollView;

    public ArrayList<String> parentList, parentList2;
    public ArrayList<ArrayList<String>> childList, childList2;
    private ArrayList<String> childContent;


    private String field;
    private String badges;
    private String badge_name;
    private String user_id;

    private JSONObject myawards_json;
    private String interested_list, uninterested_list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_badge);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();
        makeList();
        setEvent();

    }

    private void makeList() {

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(getApplicationContext());
        user_id = prefereneUtil.getSharedPreferences("user_id",user_id);


        // interest
        parentList = new ArrayList<String>(); // 어워드 목록
        childList = new ArrayList<ArrayList<String>>(); // 어워드에 맞는 뱃지 목록

        mlikeExpListViewAdapter = new LikeExpandableListViewAdapter(getApplicationContext(),parentList,childList);
        badge_interested_expandableList.setAdapter(mlikeExpListViewAdapter);

        // notinterest
        parentList2 = new ArrayList<String>();
        childList = new ArrayList<ArrayList<String>>();

        unlikeExpandableListViewAdapter = new unlikeExpandableListViewAdapter(getApplicationContext(),parentList2,childList2);
        badge_uninterested_expandableList.setAdapter(unlikeExpandableListViewAdapter);

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

    class loadList extends AsyncTask<String,String,JSONObject>{

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected JSONObject doInBackground(String... result) {

            try {

                Log.d("loadList", "func : " + interested_list);
                JSONArray interested_array = new JSONArray(interested_list);

                for (int i = 0; i < interested_array.length(); i++) {

                    childContent = new ArrayList<String>();

                    JSONObject jo = interested_array.getJSONObject(i);
                    field = jo.getString("field");
                    parentList.add(i, field);
                    Log.d("field",field);

//                    int index = parentList.indexOf(field);
//                    Log.i("index", String.valueOf(index));

                    badges = jo.getString("badges");
                    JSONArray ja2 = new JSONArray(badges); // 뱃지 배열

                    for (int j = 0; j < ja2.length(); j++) {

                        JSONObject jo1 = ja2.getJSONObject(j);
                        badge_name = jo1.getString("badge_name");
                        Log.i("뱃지 이름:  ", badge_name);

                        childContent.add(j, badge_name);
                        childList.add(i, childContent);
//                        childList.get(i).add(badge_name);
                       // childContent.add(j,badge_name);
                    }

                    Log.i("content", childList.toString());
                }

                Log.i("parent", parentList.toString());
                Log.i("child" , childList.toString());

                Log.d("loadList_uninterested", "func : " + uninterested_list);
                JSONArray uninterested_array = new JSONArray(uninterested_list);

                for(int i=0; i<uninterested_array.length(); i++){

                    childContent = new ArrayList<String>();

                    JSONObject jo1 = uninterested_array.getJSONObject(i);

                    field = jo1.getString("field");
                    parentList2.add(i, field);
                    Log.d("field", field);

                    badges = jo1.getString("badges");
                    JSONArray ja2 = new JSONArray(badges); // 뱃지 배열

                    for (int j = 0; j < ja2.length(); j++) {

                        JSONObject jsonObject = ja2.getJSONObject(j);
                        badge_name = jsonObject.getString("badge_name");
                        Log.i("뱃지 이름:  ", badge_name);

                        childContent.add(j,badge_name);
                        childList2.add(i, childContent);

                    }

                    Log.i("content", childList2.toString());

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject jObj) {

            mlikeExpListViewAdapter.notifyDataSetChanged();
            unlikeExpandableListViewAdapter.notifyDataSetChanged();
        }


    }

    private void setEvent() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyBadgeActivity.this.finish();
            }
        });

        badge_interested_expandableList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        badge_interested_expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Toast.makeText(getApplicationContext(), "Group Clicked " + parentList.get(groupPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        badge_interested_expandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(), parentList.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        badge_interested_expandableList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(), parentList.get(groupPosition) + " Collapsed", Toast.LENGTH_SHORT).show();
            }
        });

        // Listview on child click listener
        badge_interested_expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String badge_name = (String)parent.getExpandableListAdapter().getChild(groupPosition,childPosition);

                Intent intent = new Intent(MyBadgeActivity.this, BadgeDetailActivity.class);
                intent.putExtra("badge_name", badge_name);
                startActivity(intent);
                MyBadgeActivity.this.finish();

                return false;
            }
        });

        badge_uninterested_expandableList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        badge_uninterested_expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String badge_name = (String)parent.getExpandableListAdapter().getChild(groupPosition,childPosition);

                Intent intent = new Intent(MyBadgeActivity.this, BadgeDetailActivity.class);
                intent.putExtra("badge_name", badge_name);
                startActivity(intent);
                MyBadgeActivity.this.finish();

                return false;
            }
        });

    }

    private void initView() {

        badge_interested_expandableList = (ExpandableListView)findViewById(R.id.badge_interested_expandableList);
        badge_uninterested_expandableList = (ExpandableListView)findViewById(R.id.badge_uninterested_expandableList);
        btnBack = (Button)findViewById(R.id.btnBack);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

    }


    class ShowMyAwards extends AsyncTask<String, String, JSONObject> {

        private static final String urlString = Award.AWARD_URL + "Award_server/Award/mypage_myBadge.jsp";
        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MyBadgeActivity.this);
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
