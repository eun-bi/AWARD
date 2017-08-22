package com.example.user.myapplication.MyFeedAward;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import com.example.user.myapplication.Award;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.network.JSONParser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 2017-03-08.
 */
public class MyFeedAwardFragment extends Fragment implements View.OnTouchListener{

    private static final String MYAWRAD_URL = Award.AWARD_URL + "Award_server/Award/feed_myaward.jsp";
    private static final String MYAWARD_HEADER_URL = Award.AWARD_URL + "Award_server/Award/nominate_list.jsp";

    private static RecyclerView mRecyclerView;
    private MyFeedAwardAdapter recycler_adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar myAward_toolbar;

    ArrayList<MyFeedAward> awardsList = null;
    private LinearLayoutManager mLinearLayoutManager;

    private static boolean loadingMore = true;

    private static int scroll_num = 1;
    private static final int result_length = 4;
    private JSONObject award_json, nominate_json;

    private static String firstAward, nominates;

    private String user_id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        awardsList = new ArrayList<>();

        user_id = new SharedPrefereneUtil(getContext()).getUser_id();
        Log.d("user_id",user_id);

        try {
            /* Loading first list items when Fragment created */
            award_json = new ShowMyAward().execute(user_id, Integer.toString(scroll_num)).get();
            nominate_json = new GetNominate().execute().get();
            Log.d("nominate_test",nominate_json.toString());
            Log.d("award_test",award_json.toString());
            firstAward = award_json.getString("MyAwardList");
            nominates = nominate_json.getString("NominateList");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState == null) {
            View view = inflater.inflate(R.layout.fragment_myfeed_myaward, container, false);
            initView(view);
            Log.i("Fragment123", "New Feed");
            return view;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recycler_adapter = new MyFeedAwardAdapter(getContext(), awardsList, nominates);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(recycler_adapter);

        loadList(firstAward);
        refreshItems();
        setEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        //refreshItems();
    }

    private void initView(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.myAward_swipe_refresh);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.myAward_list_recycler);
        myAward_toolbar = (Toolbar) v.findViewById(R.id.myAward_toolbar);
        myAward_toolbar.getBackground().setAlpha(0);
    }

    void refreshItems() {
        // Load items
        awardsList.clear();
        loadingMore = true;

        scroll_num = 1;
        try {
            award_json = new ShowMyAward().execute(user_id, Integer.toString(scroll_num)).get();
            firstAward = award_json.getString("MyAwardList");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        loadList(firstAward);
        recycler_adapter.notifyDataSetChanged();

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setEvent(){

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }

        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private static final int HIDE_THRESHOLD = 20;
            private int scrolledDistance = 0;
            private boolean controlsVisible = true;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                /* 마지막 아이템의 위치 계산해서 계속 paging */
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();

                Log.i("scorll number", "dy : " + dy + ", firstVisible : " + firstVisibleItem + ", visibleItem : " + visibleItemCount +
                        "totalCount : " + totalItemCount);

                int offSetTop = recyclerView.getChildAt(0).getTop();
                if (firstVisibleItem == offSetTop) {
                    /* toolbar 의 색상을 투명으로 바꿔준다 */
                    //Toast.makeText(getContext(), "처음", Toast.LENGTH_SHORT).show();
                    myAward_toolbar.setBackgroundResource(R.drawable.background_toolbar_translucent);
                }

                //show views if first item is first visible position and views are hidden
                if (firstVisibleItem == 0) {
                    if (!controlsVisible) {
                        showViews();
                        controlsVisible = true;
                    }
                } else {
                    if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                        hideViews();
                        controlsVisible = false;
                        scrolledDistance = 0;
                    } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                        showViews();
                        controlsVisible = true;
                        scrolledDistance = 0;
                    }

                    if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                        scrolledDistance += dy;
                    }
                }

                if (dy > 0) {
                    if (loadingMore) {
                        if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                            /* Paging */
                            try {
                                //Toast.makeText(getContext(), "last", Toast.LENGTH_SHORT).show();
                                scroll_num++;
                                award_json = new ShowMyAward().execute(user_id, Integer.toString(scroll_num)).get();

                                String response = award_json.getString("MyAwardList");
                                Log.d("loadList", Integer.toString(scroll_num) + ": initial : " + response);
                                loadList(response);
                            } catch (Exception e) {
                                Log.e("myAward", "error : myAward, onScroll, 결과 값 받아오는 에러");
                                e.printStackTrace();
                            }

                        }
                    }
                }

            }
        });

    }

    private void hideViews() {
//        MainActivity.myAward_toolbar.animate().translationY(-MainActivity.myAward_toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        myAward_toolbar.animate().translationY(-myAward_toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    private void showViews() {
//        MainActivity.myAward_toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        myAward_toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        myAward_toolbar.setBackgroundColor(Color.BLACK);
    }

    private void loadList(String result) {
        /* 통신 후 받은 결과값을 객체로 변환 후 list에 뿌려주는 함수 */
        try {
            Gson gson = new Gson();
            Log.d("loadList", "func : " + result);
            JSONArray awardListObj = new JSONArray(result);

            if (awardListObj.length() == 0) {
                scroll_num --;
                Log.d("loadList", "func : 비었다");

                // todo 작품등록 안 했을 시 default image 보여주는 fragment 연결
                mSwipeRefreshLayout.setBackgroundResource(R.drawable.default_feed_img);

//                ListView listView = (ListView) findViewById(R.id.list_myAward);
                //listView.removeFooterView(listFooter);
                loadingMore = false;
            }
//            else if (awardListObj.length() < result_length) {
//                scroll_num--;
//                loadingMore = false;
//            }
            else {

                mSwipeRefreshLayout.setBackgroundResource(R.drawable.default_img_result);

                for(int i = 0; i < awardListObj.length(); i++) {
                    String awardInfo = awardListObj.getJSONObject(i).toString();
                    MyFeedAward myFeedAward = gson.fromJson(awardInfo, MyFeedAward.class);
                    awardsList.add(myFeedAward);

////                    todo 리사이클러 뷰에 아이템이 늘어나지 않으면 풀어보자
//                    recycler_adapter.add(myAward);
                }
                recycler_adapter.notifyDataSetChanged();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        /* Header 와 listview 사이에 터치 중복 방지 */

        if (event.getAction() == MotionEvent.ACTION_UP){
            mRecyclerView.requestDisallowInterceptTouchEvent(false);
        }
        else {
            mRecyclerView.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }

    public static void moveScroll() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    private class ShowMyAward extends AsyncTask<String, String, JSONObject> {
        /* myAward 부분을 서버에서 JSONObject 형식으로 가지고 옴 */

        JSONParser jsonParser = new JSONParser();

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("AWARD를 가져오는 중 입니다.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", args[0]);
                Log.d("myAward", args[0]);
                params.put("scroll_num", args[1]);
                Log.d("myAward", args[1]);

                JSONObject result = jsonParser.makeHttpRequest(
                        MYAWRAD_URL, "POST", params);


                if (result != null) {
                    Log.d("myAward", "result : " + result);
                    return result;
                } else {
                    Log.d("myAward", "result : null, doInBackground");
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
            super.onPostExecute(jObj);
        }
    } /* list 통신을 위한 AsyncTask 끝 */

    private class GetNominate extends AsyncTask<String, String, JSONObject> {
        /* Header의 nominate를 jsonOjbect 형식으로 받아옴 */
        JSONParser jsonParser = new JSONParser();

        protected void onPreExecute() {
        }

        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();

                JSONObject result = jsonParser.makeHttpRequest(
                        MYAWARD_HEADER_URL, "GET", params);

                if (result != null) {
                    Log.d("Nominate", "result : " + result);
                    return result;
                } else {
                    Log.d("Nominate", "result : null, doInBackground");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject jObj) {
            super.onPostExecute(jObj);
        }
    } /* Header nominate 통신을 위한 AsyncTask 끝 */

}
