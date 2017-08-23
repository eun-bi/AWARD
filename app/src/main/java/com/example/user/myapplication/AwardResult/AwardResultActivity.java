package com.example.user.myapplication.AwardResult;

import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.MakeAward.MakeAwardActivity1;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.support.v4.app.FragmentTransaction;

public class AwardResultActivity extends AppCompatActivity {

    private final static String IMAGE_URL = Award.IMAGE_URL + "Image/";
    private static final String URL_ = Award.AWARD_URL + "Award_server/Award/delete_Award.jsp";

    protected Bundle awardResultBundle = new Bundle();

    protected String resultImagePath_FromBundle;
    protected String resultField_FromBundle;
    protected String resultTitle_FromBundle;
    protected static String resultCaption_FromBundle;
    protected String resultAward_FromBundle;

    private TabLayout tabLayout;
    ImageView award_result_img;
    TextView txtAward_result_field;
    TextView txtAward_result_title;
    Button btnCancel;

    private String delete_chk;

    private String user_id;
    private String award_id;

    AwardTextFragment textFragment = new AwardTextFragment();
    AwardImageFragment2 imageFragment = new AwardImageFragment2();
    AwardLinkFragment2 linkFragment = new AwardLinkFragment2();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award_result);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        Intent intent = getIntent();
        awardResultBundle = intent.getBundleExtra("awardResultBundle");

        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");

//        tabLayout.addTab(tabLayout.newTab().setText("시상평"));
//        tabLayout.addTab(tabLayout.newTab().setText("앨범"));
//        tabLayout.addTab(tabLayout.newTab().setText("링크"));
//        tabLayout.setTabTextColors(ContextCompat.getColorStateList(AwardResultActivity.this, R.color.white));


        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, textFragment);
            transaction.commit();
        }


        initView();
        setView();
        setEvent();

    }

    private void setEvent() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AwardResultActivity.this.finish();
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            int pre = 0;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeFragment(tab.getPosition(), pre);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                pre = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void changeFragment(int position, int pre) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch(position) {
            /* Change fragment  */
            case 0:

                transaction.replace(R.id.fragment, textFragment, "Caption");
                transaction.commit();
                break;

            case 1:
                transaction.replace(R.id.fragment, imageFragment, "Album");
                transaction.commit();
                break;

            case 2:
                transaction.replace(R.id.fragment, linkFragment, "Link");
                transaction.commit();
                break;
        }
    }


    private void setView() {

        /**
         * Result의 contentScrim 부분에 대해 setting
         * Feed에서 Bundle로 받아온 img_path, title 부분에 대한 setting
         * */

        resultImagePath_FromBundle = awardResultBundle.getString("award_img_path");
        resultTitle_FromBundle = awardResultBundle.getString("award_title");
        resultCaption_FromBundle = awardResultBundle.getString("award_caption");
        resultField_FromBundle = awardResultBundle.getString("award_field");
        award_id = awardResultBundle.getString("award_id");

        txtAward_result_field.setText(resultField_FromBundle);
        txtAward_result_title.setText(resultTitle_FromBundle);
        txtAward_result_title.setSelected(true);


        if(TextUtils.isEmpty(resultImagePath_FromBundle)){
            award_result_img.setImageResource(R.drawable.default_img_result);
        }
        else{
            Glide
                    .with(this)
                    .load(IMAGE_URL + resultImagePath_FromBundle)
                    .fitCenter()
                    .crossFade()
                    .centerCrop()
                    .override(400,400)
                    .thumbnail(0.1f)
                    .into(award_result_img);
        }

    }

    private void initView() {

        tabLayout = (TabLayout) findViewById(R.id.award_result_content_tabLayout);
        award_result_img = (ImageView)findViewById(R.id.award_result_img);
        txtAward_result_field = (TextView)findViewById(R.id.txtAward_result_field);
        txtAward_result_title = (TextView)findViewById(R.id.txtAward_result_title);
        btnCancel = (Button)findViewById(R.id.btnCancel);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_award_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            new AwardDelAsync().execute(user_id, award_id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // 서버에 삭제 요청
    class AwardDelAsync extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
//            pDialog = new ProgressDialog(MakeAwardActivity2.this);
//            pDialog.setMessage("Getting your data... Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            JSONObject result = null;

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", args[0]);
                Log.d("delete", args[0]);
                params.put("award_id", args[1]);
                Log.d("delete", args[1]);


                result = jsonParser.makeHttpRequest(
                        URL_, "POST", params);

                delete_chk = result.getString("award_delete");

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

            if(delete_chk.equalsIgnoreCase("1"))
                Toast.makeText(getApplicationContext(), "어워드 삭제 완료", Toast.LENGTH_SHORT).show();
            else if (delete_chk.equalsIgnoreCase("0")){
                Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
            }
//            if (pDialog != null && pDialog.isShowing()) {
//                pDialog.dismiss();
//            }

        } // AsyncTask 끝
    }
}
