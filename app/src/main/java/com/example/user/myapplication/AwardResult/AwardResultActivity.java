package com.example.user.myapplication.AwardResult;

import android.app.FragmentTransaction;
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

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AwardResultActivity extends AppCompatActivity {

    private final static String IMAGE_URL = Award.IMAGE_URL + "Image/";
    private static final String URL_ = Award.AWARD_URL + "Award_server/Award/delete_Award.jsp";
    private static final String get_url = Award.AWARD_URL + "Award_server/Award/mypage_award_detail.jsp";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    protected String resultImagePath_FromBundle;
    protected String resultField_FromBundle;
    protected String resultTitle_FromBundle;
    protected static String resultCaption_FromBundle;
    protected String resultAward_FromBundle;

    private String award_id;

    ImageView award_result_img;
    TextView txtAward_result_field;
    TextView txtAward_result_title;

    protected Bundle awardResultBundle = new Bundle();

    private String delete_chk;

    private JSONObject award_json;
    private String award_img_path;
    private String award_title;
    private String award_field;

    private String user_id;

    //private ImageView img_result;
//    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award_result);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        Intent intent = getIntent();
        awardResultBundle = intent.getBundleExtra("awardResultBundle");

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(getApplicationContext());
        user_id = prefereneUtil.getSharedPreferences("user_id", user_id);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        setView();



        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.award_result_content_tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);

    }

    //todo test
    // 갱신 문제
    @Override
    protected void onResume() {
        super.onResume();
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    private void setView() {

        // AwardsDetail 에서 넘어온 경우

        Intent intent = getIntent();
        String award_next = intent.getStringExtra("award_next");
        award_id = intent.getStringExtra("award_id");

        if(TextUtils.isEmpty(award_next)){
            Log.i("intent"," x");

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

            return;

        }

        /*  award detail 에서 넘어온 경우 */

        else {
            // award_id로 요청했을 시 title, field, img_path 받아오기

            try {
                award_json = new GetAward().execute(award_id).get();
                award_title = award_json.getString("award_title");
                award_field = award_json.getString("award_field");
                award_img_path = award_json.getString("award_img_path");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            txtAward_result_title.setText(award_title);
            txtAward_result_field.setText(award_field);

            if (TextUtils.isEmpty(award_img_path)) {
                award_result_img.setImageResource(R.drawable.default_img_result);
            }
            Glide
                    .with(this)
                    .load(IMAGE_URL + award_img_path)
                    .fitCenter()
                    .crossFade()
                    .centerCrop()
                    .override(400, 400)
                    .thumbnail(0.1f)
                    .into(award_result_img);
        }

    }

    private void initView() {

        award_result_img = (ImageView)findViewById(R.id.award_result_img);
        txtAward_result_field = (TextView)findViewById(R.id.txtAward_result_field);
        txtAward_result_title = (TextView)findViewById(R.id.txtAward_result_title);

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

        if(id == R.id.home){
            AwardResultActivity.this.finish();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            new AwardDelAsync().execute(user_id, award_id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_award_result, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            Fragment fragment = null;

            switch (position){
                case 0:
                    fragment = new AwardTextFragment();
                    break;
                case 1:
                    fragment = new AwardImageFragment2();
                    break;
                case 2:
                    fragment = new AwardLinkFragment2();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "시상평";
                case 1:
                    return "앨범";
                case 2:
                    return "링크";
            }
            return null;
        }

        // refresh
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    // 시상평 정보를 불러옴
    class GetAward extends AsyncTask<String, String, JSONObject> {

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
                params.put("award_id", args[0]);
                Log.d("async_test", args[0]);



                result = jsonParser.makeHttpRequest(
                        get_url, "POST", params);


                if (result != null) {
                    Log.d("test", "result : " + result);
                    return result;
                } else {
                    Log.d("test", "result : null, doInBackground");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(JSONObject jObj) {

//            if (pDialog != null && pDialog.isShowing()) {
//                pDialog.dismiss();
//            }

        } // AsyncTask 끝
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
