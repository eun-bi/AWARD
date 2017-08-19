package com.example.user.myapplication.AwardResult;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.network.JSONParser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by User on 2017-04-07.
 */
public class AwardLinkFragment2 extends Fragment {

    private static final String Link_URL = Award.AWARD_URL + "Award_server/Award/myAward_Link.jsp";

    private static RecyclerView myLink_list_recycler;
    private ImageButton btnLink;
    private LinkListAdapter2 linkListAdapter;

    private ArrayList<AwardLink> awardLinks = null;
    private LinearLayoutManager mLinearLayoutManager;

    private JSONObject link_json;
    private String link_list;

    private String vodid;
    private String user_id;
    private String award_id;

    protected Bundle awardResultBundle = new Bundle();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        awardLinks = new ArrayList<>();

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(getContext());
        user_id = prefereneUtil.getSharedPreferences("user_id",user_id);

        Intent intent = getActivity().getIntent();
        awardResultBundle = intent.getBundleExtra("awardResultBundle");
        award_id = awardResultBundle.getString("award_id");

        try {
             /* Loading first list items when Fragment created */

            link_json = new ShowMyLink().execute(user_id, award_id).get(); // user_id. award_id
            link_list = link_json.getString("Links");
            Log.d("link_test : ", link_list);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState == null){

            View rootView = inflater.inflate(R.layout.fragment_award_link_2, container, false);
            initView(rootView);
            Log.i("LinkFragment", "New");
            return rootView;

        }
            return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        linkListAdapter = new LinkListAdapter2();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        myLink_list_recycler.setLayoutManager(mLinearLayoutManager);
        myLink_list_recycler.setAdapter(linkListAdapter);

        loadList(link_list);
        setEvent();
    }

    private void setEvent() {

        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AddLinkActivity.class);
                intent.putExtra("award_id", award_id);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //refreshItems();
    }

    private void loadList(String result) {
        try {

            linkListAdapter = new LinkListAdapter2();
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            myLink_list_recycler.setLayoutManager(mLinearLayoutManager);
            myLink_list_recycler.setAdapter(linkListAdapter);

            Gson gson = new Gson();
            Log.d("loadList", "func : " + link_list);
            JSONArray linkListObj = new JSONArray(result);

            if(linkListObj.length() == 0){
                Log.d("loadList", "func : 비었다");
            }
            else{
                for(int i = 0; i < linkListObj.length(); i++) {

                    JSONObject linkInfo = linkListObj.getJSONObject(i);
                    vodid = linkInfo.getString("videoId");
                    String description = linkInfo.getString("description");
                    String title = linkInfo.getString("title");
                    String url = linkInfo.getString("url");
                    linkListAdapter.addItem(vodid, url, title, description);

//                    AwardLink link_List = gson.fromJson(linkInfo, AwardLink.class);
//                    awardLinks.add(link_List);
                    // myLink_list_recycler.add(link_List);


                }
                //linkListAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // badgelist를 불러옴
    class ShowMyLink extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
//            pDialog = new ProgressDialog(getContext().getApplicationContext());
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
                Log.d("user_id", args[0]);
                params.put("award_id", args[1]);
                Log.d("award_id", args[1]);


                result = jsonParser.makeHttpRequest(
                        Link_URL, "POST", params);


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

    private void initView(View rootView) {

        myLink_list_recycler = (RecyclerView)rootView.findViewById(R.id.myLink_list_recycler);
        btnLink = (ImageButton)rootView.findViewById(R.id.btnLink);

    }

    public class LinkListAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private ArrayList<AwardLink> myLink = new ArrayList<AwardLink>();

        public LinkListAdapter2() {

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            mContext = parent.getContext();

            final View view = LayoutInflater.from(mContext).inflate(R.layout.list_link_item, parent, false);
            return new LinkViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            final AwardLink link = myLink.get(position);

            LinkViewHolder vi = (LinkViewHolder) holder;
            vi.link_title.setText(link.getTitle());
            vi.link_sub.setText(link.getDescription());

            Glide
                    .with(mContext)
                    .load(link.getUrl())
                    .override(100, 100)
                    .fitCenter()
                    .centerCrop()
                    .thumbnail(0.1f)
                    .into(vi.link_img);

            vi.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new VideoDeleteTask().execute(link.getVideoId(),award_id,user_id);
                }
            });

            vi.item_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext.getApplicationContext(), VideoPlayActivity.class);
                    intent.putExtra("id", link.getVideoId());
                    mContext.startActivity(intent);
                }
            });
        }

        public class LinkViewHolder extends RecyclerView.ViewHolder{
            ImageView link_img;
            TextView link_title;
            TextView link_sub;
            CardView item_link;
            Button btnDelete;

            public LinkViewHolder(View view) {
                super(view);
                link_img = (ImageView)view.findViewById(R.id.mylink_img);
                link_title = (TextView)view.findViewById(R.id.mylink_title);
                link_sub = (TextView)view.findViewById(R.id.mylink_sub);
                item_link = (CardView) itemView.findViewById(R.id.item_link);
                btnDelete = (Button)view.findViewById(R.id.mylink_delete);
            }
        }


        /* item count */
        public int getBasicItemCount() {
            return myLink == null ? 0 : myLink.size();
        }


        @Override
        public int getItemCount() {
            return getBasicItemCount();
        }

        public void addItem(String vidioId,String url,String title,String descrption){

            AwardLink item = new AwardLink();

            item.setVideoId(vidioId);
            item.setUrl(url);
            item.setTitle(title);
            item.setDescription(descrption);

            myLink.add(item);

        }

    }

    public class VideoDeleteTask extends AsyncTask<String, String, JSONObject> {

        Dialog loadingDialog;
        String youtube_url = Award.AWARD_URL + "Award_server/Award/myAward_Link_delete.jsp";
        JSONParser jsonParser = new JSONParser();

        protected void onPreExecute() {
            super.onPreExecute();
//            loadingDialog = ProgressDialog.show(get, "Please wait", "Loading...");
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

            Toast.makeText(getActivity().getApplicationContext(), "링크 삭제 완료", Toast.LENGTH_SHORT).show();

            myLink_list_recycler.setAdapter(linkListAdapter);
//            loadingDialog.dismiss();
            // Log.d("Test", "image byte is " + result);

        }
    }
}

