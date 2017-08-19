package com.example.user.myapplication.AwardResult;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.BaseActivity;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class AddLinkActivity extends AppCompatActivity {

    Button btnBack,btnSearch;
    private static EditText editSearch;
    ListView list_search;
    LinearLayout linearLayout;

    ArrayList<SearchData> sdata = new ArrayList<SearchData>();
    ArrayList<AwardLink> awardLinks = new ArrayList<AwardLink>();

    private static String serverKey = "AIzaSyBdxc9tEbnWZxNcBtH3iW4V4MS37nyxH7E"; // Youtube api key

    String vodid; // 동영상 아이디 - 재생에 필요
    String title;
    String description;
    String imgUrl;

    private String user_id;
    private String award_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_link);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(getApplicationContext());
        user_id = prefereneUtil.getSharedPreferences("user_id",user_id);

        Intent intent = getIntent();
        award_id = intent.getStringExtra("award_id");

        setEvent();

    }

    private void initView() {

        linearLayout = (LinearLayout)findViewById(R.id.snakbar_layout);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        editSearch = (EditText)findViewById(R.id.editSearch);
        list_search = (ListView)findViewById(R.id.list_search);

    }

    private void setEvent() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddLinkActivity.this.finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new searchTask().execute();
            }
        });

    }

    private class searchTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                parsingJsonData(getUtube());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            YoutubeListAdapter youtubeListAdapter = new YoutubeListAdapter(AddLinkActivity.this, R.layout.item_searchlist,sdata);
            list_search.setAdapter(youtubeListAdapter);
            youtubeListAdapter.notifyDataSetChanged();
        }
    }

    private void parsingJsonData(JSONObject jsonObject) throws JSONException {

        sdata.clear();

        JSONArray contacts = jsonObject.getJSONArray("items");

        for(int i=0; i<contacts.length();i++){

            JSONObject c = contacts.getJSONObject(i);

            String kind = c.getJSONObject("id").getString("kind"); // 동영상 종류 체크
            if(kind.equals("youtube#video")){
                vodid = c.getJSONObject("id").getString("videoId");
            }
            else {
                vodid = c.getJSONObject("id").getString("playlistId");

            }

            title = c.getJSONObject("snippet").getString("title");

            String n_title ="";
            try {
                n_title = new String(title.getBytes("8859_1"),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("title",n_title);

            description = c.getJSONObject("snippet").getString("description");
            String n_description ="";
            try {
                n_description = new String(description.getBytes("8859_1"),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");


            sdata.add(new SearchData(vodid,n_title,imgUrl,n_description));


        }
    }

    public JSONObject getUtube() throws MalformedURLException {

        String url_youtube = "https://www.googleapis.com/youtube/v3/search?"
                + "part=snippet&q=" + editSearch.getText().toString()
                + "&key=" + serverKey + "&maxResults=50";

       //  검색어 공백 문제 해결
        String encodeS = url_youtube.replaceAll(" ","%20");

        URL url = new URL(encodeS);

        StringBuilder stringBuilder = new StringBuilder();

        try {

            HttpURLConnection urlConnection;

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            InputStream stream = urlConnection.getInputStream();

            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }



    /**
     * Created by User on 2016-12-10.
     */
    public class YoutubeListAdapter extends ArrayAdapter<SearchData> {

        private  ArrayList<SearchData> items;
        SearchData data;

        public YoutubeListAdapter(Context context, int textViewResourceId, ArrayList<SearchData> items) {
            super(context,textViewResourceId,items);
            this.items = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Context context = parent.getContext();

            View v = convertView;
            data = items.get(position);

            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_searchlist,null);
          //  Util.setGlobalFont(context,v);


            ImageView youtube_img = (ImageView)v.findViewById(R.id.youtube_img);
            final ToggleButton btn_videoAdd = (ToggleButton)v.findViewById(R.id.btn_videoAdd);

            final String url = data.getUrl();
            String sUrl = "";
            String eUrl = "";
            sUrl = url.substring(0, url.lastIndexOf("/") + 1);
            eUrl = url.substring(url.lastIndexOf("/") + 1, url.length());
            try {
                eUrl = URLEncoder.encode(eUrl, "EUC-KR").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String new_url = sUrl + eUrl;
            Glide.with(context).load(new_url).into(youtube_img);

            v.setTag(position);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    Intent intent = new Intent(AddLinkActivity.this, VideoPlayActivity.class);
                    intent.putExtra("id", items.get(pos).getVideoId());
                    startActivity(intent);
                }
            });

            btn_videoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // new VideoAddTask().execute();
                    if (btn_videoAdd.isChecked()) {
                        btn_videoAdd.setTextColor(Color.parseColor("#da9a7a"));

                        Log.e("youtube", "링크저장" + "///" + items.get(position).getVideoId() + "/" + items.get(position).getTitle() + "/" + items.get(position).getUrl() + "/" + items.get(position).getDescription());
                        // 내 링크 리스트에 추가 기능 필요 x ?
//                        awardLinks.add(new AwardLink(items.get(position).getVideoId(), items.get(position).getTitle() , items.get(position).getUrl(), items.get(position).getDescription()));


//                         서버에 저장 기능
                        new VideoAddTask().execute(items.get(position).getVideoId(), items.get(position).getTitle() , items.get(position).getUrl(), items.get(position).getDescription(),award_id,user_id);
                    }
                    else{
                        btn_videoAdd.setTextColor(Color.parseColor("#73ffffff"));
                        // 선택 해제 시 리스트에서 삭제하기 기능 필요

                        new VideoDeleteTask().execute(items.get(position).getVideoId(),award_id, user_id);
                    }

                }
            });

            ((TextView)v.findViewById(R.id.youtube_title)).setText(data.getTitle());
            ((TextView)v.findViewById(R.id.youtube_sub)).setText(data.getDescription());

            return v;

        }
    }

    public class VideoAddTask extends AsyncTask<String, String, JSONObject> {

        Dialog loadingDialog;
        String youtube_url = Award.AWARD_URL + "Award_server/Award/myAward_Link_add.jsp";
        JSONParser jsonParser = new JSONParser();

        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(AddLinkActivity.this, "Please wait", "Loading...");
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("vodid", args[0]);
                params.put("title", args[1]);
                params.put("url", args[2]);
                params.put("description", args[3]);
                params.put("award_id", args[4]);
                params.put("user_id", args[5]);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        youtube_url, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject jsonObject) {

            loadingDialog.dismiss();
            Snackbar snackbar = Snackbar.make(linearLayout, "추가 완료", Snackbar.LENGTH_SHORT);
            snackbar.show();
            // Log.d("Test", "image byte is " + result);

        }
    }

    public class VideoDeleteTask extends AsyncTask<String, String, JSONObject> {

        Dialog loadingDialog;
        String youtube_url = Award.AWARD_URL + "Award_server/Award/myAward_Link_delete.jsp";
        JSONParser jsonParser = new JSONParser();

        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(AddLinkActivity.this, "Please wait", "Loading...");
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("vodid", args[0]);
                params.put("award_id", args[1]);
                params.put("user_id", args[2]);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        youtube_url, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject jsonObject) {

            loadingDialog.dismiss();
            // Log.d("Test", "image byte is " + result);

        }
    }


}
