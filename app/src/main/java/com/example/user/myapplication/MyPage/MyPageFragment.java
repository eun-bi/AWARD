package com.example.user.myapplication.MyPage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.FieldSetActivity;
import com.example.user.myapplication.MyAward.MyAwardsActivity;
import com.example.user.myapplication.MyBadge.MyBadgeActivity;
import com.example.user.myapplication.MyField.MyFieldActivity;
import com.example.user.myapplication.R;
import com.example.user.myapplication.Setting.SettingActivity;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by User on 2017-03-08.
 */
public class MyPageFragment extends Fragment{

    private static final String PROFILE_URL = Award.AWARD_URL + "Award_server/Award/mypage.jsp";
    private static final String IMAGE_URL = Award.IMAGE_URL + "profile/";
    private ImageView img_Profile;
    private TextView txtAward_myname;
    private ListView listView_menu;
    private MenuAdapter adapter;

    private JSONObject profile_json;

    private String user_name;
    private String user_img_path;
    private String user_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(getContext());
        user_id = prefereneUtil.getSharedPreferences("user_id",user_id);

        try {
            profile_json = new GetProfile().execute(user_id).get();
            user_name = profile_json.getString("user_name");
            user_img_path = profile_json.getString("user_img_path");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            View view = inflater.inflate(R.layout.fragment_mypage, container, false);
            initView(view);
            Log.i("Fragment123", "New Profile");
            return view;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Util.setGlobalFont(getContext(), getActivity().getWindow().getDecorView()); // font 적용

        adapter = new MenuAdapter();
        listView_menu.setAdapter(adapter);
        setProfile_Menu();

        Glide
                .with(this)
                .load(IMAGE_URL + user_img_path)
                .fitCenter()
                .centerCrop()
                .crossFade() // 이미지 로딩 시 페이드 효과
                .bitmapTransform(new CropCircleTransformation(getActivity()))  // image 원형
                .override(200, 200)
                .into(img_Profile);

        txtAward_myname.setText(user_name);

        setEvent();
    }


    private void setProfile_Menu() {
        adapter.addItem(getActivity().getDrawable(R.drawable.menu_like), "내 관심분야");
        adapter.addItem(getActivity().getDrawable(R.drawable.menu_badge), "내 뱃지");
        adapter.addItem(getActivity().getDrawable(R.drawable.menu_trophy), "내 어워드");
        adapter.addItem(getActivity().getDrawable(R.drawable.menu_setting), "설정");
    }

    private void setEvent() {

        listView_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent;

                switch (position) {
                    case 0:
                        intent = new Intent(getActivity().getApplicationContext(), MyFieldActivity.class);
                        startActivity(intent);
                        break;
                    case 1 :
                        intent = new Intent(getActivity().getApplicationContext(), MyBadgeActivity.class);
                        startActivity(intent);
                        break;
                    case 2 :
                        intent = new Intent(getActivity().getApplicationContext(), MyAwardsActivity.class);
                        startActivity(intent);
                        break;
                    case 3 :
                        intent = new Intent(getActivity().getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void initView(View v) {

        img_Profile = (ImageView) v.findViewById(R.id.img_Profile);
        txtAward_myname = (TextView) v.findViewById(R.id.txtAward_myname);
        listView_menu = (ListView)v.findViewById(R.id.listView_menu);

    }

    private class GetProfile extends AsyncTask<String, String, JSONObject> {
        JSONParser jsonParser = new JSONParser();

        protected void onPreExecute() {

        }

        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", args[0]);

                JSONObject result = jsonParser.makeHttpRequest(
                        PROFILE_URL, "GET", params);

                if (result != null) {
                    Log.d("profile", "result : " + result);
                    return result;
                } else {
                    Log.d("profile", "result : null, doInBackground");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject jObj) {
            super.onPostExecute(jObj);
        }
    } /* Profile 통신을 위한 AsyncTask 끝 */

}
