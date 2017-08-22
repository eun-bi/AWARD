package com.example.user.myapplication.AwardResult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

/**
 * Created by User on 2016-10-02.
 */
public class AwardTextFragment extends Fragment {

    private TextView txtBadge_name,txtAward_caption;
    private ImageView img_badge;

    private JSONObject badgejson;
    private String badge_name;
    private String badge_img_path;

    private String user_id;
    private String award_id;

    protected Bundle awardResultBundle = new Bundle();

    private static final String URL_ = Award.AWARD_URL + "Award_server/Award/myAward_award.jsp";
    private final static String IMAGE_URL = Award.IMAGE_URL + "badge_icon/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null)
            return super.onCreateView(inflater, container, savedInstanceState);


        View rootView = inflater.inflate(R.layout.fragment_award_text,container,false);
        Util.setGlobalFont(getContext(), rootView); // font 적용


        initView(rootView);
        setView(rootView);

        loadView();

        return rootView;
    }

    private void loadView() {

        user_id = new SharedPrefereneUtil(getContext()).getUser_id();
        Log.d("user_id",user_id);
        Intent intent = getActivity().getIntent();
        awardResultBundle = intent.getBundleExtra("awardResultBundle");
        award_id = awardResultBundle.getString("award_id");

        try {
            badgejson = new ShowAwardResult().execute(user_id, award_id).get();
            badge_name = badgejson.getString("badge_name");
            badge_img_path = badgejson.getString("badge_img_path");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtBadge_name.setText(badge_name);

        Glide
                .with(this)
                .load(IMAGE_URL + badge_img_path)
                .override(100, 100)
                .fitCenter()
                .centerCrop()
                .thumbnail(0.1f)
                .into(img_badge);

    }

    private void initView(View rootView) {
        
        txtBadge_name = (TextView)rootView.findViewById(R.id.txtBadge_name);
        txtAward_caption = (TextView)rootView.findViewById(R.id.txtAward_caption);
        img_badge = (ImageView)rootView.findViewById(R.id.img_badge);

    }

    public void setView(View view) {

        if(TextUtils.isEmpty(AwardResultActivity.resultCaption_FromBundle)){
            txtAward_caption.setText("등록된 시상평이 없습니다. ");
        }else{
            txtAward_caption.setText(AwardResultActivity.resultCaption_FromBundle);
        }
    }

    // 시상평 정보를 불러옴
    class ShowAwardResult extends AsyncTask<String, String, JSONObject> {

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
                Log.d("async_test", args[0]);
                params.put("award_id", args[1]);
                Log.d("async_test", args[1]);


                result = jsonParser.makeHttpRequest(
                        URL_, "POST", params);


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

//            if (pDialog != null && pDialog.isShowing()) {
//                pDialog.dismiss();
//            }

        } // AsyncTask 끝
    }
}
