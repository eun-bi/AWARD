package com.example.user.myapplication.MyBadge;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
    private ArrayList<String> childContent1,childContent2;

    private String field;
    private String badges;
    private String badge_name;
    private String user_id;

    private JSONObject myawards_json;
    private String interested_list, uninterested_list;

    private ArrayList<Group> groups = null;
    private ArrayList<Group> ungroups = null;



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

        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);


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

                groups = new ArrayList<Group>();
                ArrayList<Child> children = null;

                Log.d("loadList", "func : " + interested_list);
                JSONArray interested_array = new JSONArray(interested_list);

                for (int i = 0; i < interested_array.length(); i++) {

//                    parentList = new ArrayList<String>(); // 어워드 목록
//                    childContent1 = new ArrayList<String>();
//                    childList = new ArrayList<ArrayList<String>>(); // 어워드에 맞는 뱃지 목록

                    Group group = new Group();
                    JSONObject jo = interested_array.getJSONObject(i);
                    field = jo.getString("field");

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
                    Log.d("field",field);
                    group.setField(field);

                    children = new ArrayList<Child>();
                    badges = jo.getString("badges");
                    JSONArray ja2 = new JSONArray(badges); // 뱃지 배열

                    for (int j = 0; j < ja2.length(); j++) {

                        Child child = new Child();
                        JSONObject jo1 = ja2.getJSONObject(j);
                        badge_name = jo1.getString("badge_name");
                        Log.i("뱃지 이름:  ", badge_name);

                        child.setBadge_name(badge_name);
                        children.add(child);
//                        if(TextUtils.isEmpty(badge_name)){
//                            badge_name = " ";
//                        }
//                        childContent1.add(j, badge_name);

//                        childList.get(i).add(badge_name);
                       // childContent.add(j,badge_name);
                    }
                    group.setItems(children);
                    groups.add(group);
//                    childList.add(i, childContent1);
//                    Log.i("content", childList.toString());
                }

//                Log.i("parent", parentList.toString());
//                Log.i("child" , childList.toString());


                ///////////////////////* uninterested *//////////////////////////

                Log.d("loadList_uninterested", "func : " + uninterested_list);
                JSONArray uninterested_array = new JSONArray(uninterested_list);
//                parentList2 = new ArrayList<String>();
                ungroups = new ArrayList<Group>();
                ArrayList<Child> unchildren = null;

                for(int i=0; i<uninterested_array.length(); i++){

                    // notinterest
//
//                    childList2 = new ArrayList<ArrayList<String>>();
//                    childContent2 = new ArrayList<String>();

                    Group ungroup = new Group();
                    JSONObject jo1 = uninterested_array.getJSONObject(i);

                    field = jo1.getString("field");

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

                    Log.d("field", field);
                    ungroup.setField(field);

                    unchildren = new ArrayList<Child>();
                    badges = jo1.getString("badges");
                    JSONArray ja2 = new JSONArray(badges); // 뱃지 배열

                    for (int j = 0; j < ja2.length(); j++) {

                        Child unchild = new Child();
                        JSONObject jsonObject = ja2.getJSONObject(j);
                        badge_name = jsonObject.getString("badge_name");

//                        if(TextUtils.isEmpty(badge_name)){
//                            badge_name = "뱃지가 없습니다.";
//                        }

                        Log.i("뱃지 이름:  ", badge_name);
                        unchild.setBadge_name(badge_name);
                        unchildren.add(unchild);
//                        childContent2.add(j,badge_name);
//                        Log.i("content", childContent2.toString());

                    }

                    ungroup.setItems(unchildren);
                    ungroups.add(ungroup);

//                    childList2.add(i, childContent2);
//                    Log.i("content", childList2.toString());
//
//                    Log.i("parent", parentList2.toString());
//                    Log.i("child" , childList2.toString());

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject jObj) {

            mlikeExpListViewAdapter = new LikeExpandableListViewAdapter(getApplicationContext(),groups);
            badge_interested_expandableList.setAdapter(mlikeExpListViewAdapter);

            unlikeExpandableListViewAdapter = new unlikeExpandableListViewAdapter(getApplicationContext(),ungroups);
            badge_uninterested_expandableList.setAdapter(unlikeExpandableListViewAdapter);

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


        // Listview on child click listener
        badge_interested_expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                String badge_name = (String)parent.getExpandableListAdapter().getChild(groupPosition,childPosition);

                Child child_badge = (Child)parent.getExpandableListAdapter().getChild(groupPosition,childPosition);

                Intent intent = new Intent(MyBadgeActivity.this, BadgeDetailActivity.class);
                intent.putExtra("badge_name", child_badge.getBadge_name());
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

                Child child_badge = (Child)parent.getExpandableListAdapter().getChild(groupPosition,childPosition);
                Intent intent = new Intent(MyBadgeActivity.this, BadgeDetailActivity.class);
                intent.putExtra("badge_name", child_badge.getBadge_name());
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
