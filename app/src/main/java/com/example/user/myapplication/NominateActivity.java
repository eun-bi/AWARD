package com.example.user.myapplication;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.MakeAward.MakeAwardActivity1;
import com.example.user.myapplication.network.JSONParser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class NominateActivity extends AppCompatActivity {

    private ScrollView scrollView_nominate;
    private LinearLayout linear_nominate;

    private Button btn_sisang;
    private Button btnBack;
    private Spinner spinner_nominate;
    private ListView list_nominate;
    private TextView txtNominate_tag;

    private String field;
    private String nominate;

    NominateListAdapter nominateListAdapter;

    private JSONObject nominate_json;
    private String nominate_list;

    private String nominate_name;

    private ArrayList<String> nominate_title_list;
    String nomi_description;

    private static final String URL_ = Award.AWARD_URL + "Award_server/Award/nominate.jsp";
    private static final int NOMINATE = 100;

    private final String[] fields = {"영화","드라마","애니메이션","웹툰",
                                    "만화","소설","음악","연극","뮤지컬"};

    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nominate);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();

        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);

        makeList();
        setEvent();
    }

    private void makeList() {


        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.item_spinner, fields);
//        {
//
//            @Override
//            public View getView(int position, View convertView,  ViewGroup parent) {
//                View v = super.getView(position,convertView,parent);
//                Context context = parent.getContext();
//                Util.setGlobalFont(context, convertView); // font 적용
//                return v;
//            }
//
//            @Override
//            public View getDropDownView(int position, View convertView,ViewGroup parent) {
//                View v = super.getDropDownView(position,convertView,parent);
//                Context context = parent.getContext();
//                Util.setGlobalFont(context, convertView); // font 적용
//                return v;
//            }
//        };
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner_nominate.setAdapter(adapter);

        nominateListAdapter = new NominateListAdapter();
        list_nominate.setAdapter(nominateListAdapter);
        list_nominate.setTextFilterEnabled(true);
        list_nominate.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    private void setEvent() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NominateActivity.this.finish();
            }
        });

        btn_sisang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NominateActivity.this, MakeAwardActivity1.class);
                intent.putExtra("Award_field", field);
                intent.putExtra("nominate", nominate);
                Log.d("nominate", nominate);
                Log.d("nominate",field);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });


        spinner_nominate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    field = "1";
                }
                if (position == 1) {
                    field = "2";
                }
                if (position == 2) {
                    field = "3";
                }
                if (position == 3) {
                    field = "4";
                }
                if (position == 4) {
                    field = "5";
                }
                if (position == 5) {
                    field = "6";
                }
                if (position == 6) {
                    field = "7";
                }
                if (position == 7) {
                    field = "8";
                }
                if (position == 8) {
                    field = "9";
                }

                Log.d("field", field);


                try {

                    nominate_json = new GetNominate().execute(user_id, field).get();
                    nominate_list = nominate_json.getString("nominate");

                    Log.d("nominate_test : ", nominate_list);
                    loadList(nominate_list);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        list_nominate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                btn_sisang.setEnabled(true);
                btn_sisang.setBackgroundResource(R.drawable.btn_sisang_act);
                nominateListAdapter.notifyDataSetChanged();

                nominate = nominateListAdapter.getTitle(position);
            }
        });


    }

    private void loadList(String result) {

        try {

            nominateListAdapter = new NominateListAdapter();
            list_nominate.setAdapter(nominateListAdapter);
            list_nominate.setTextFilterEnabled(true);
            list_nominate.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            Log.d("loadList", "func : " + nominate_list);
            JSONObject json_nomi = new JSONObject(nominate_list);

            nomi_description = json_nomi.getString("nomi_description");
            String nomi_title_array = json_nomi.getString("nomi_title");

            Log.d("nominate_test_plz  ", nomi_description + "// " + nomi_title_array);

            JSONArray ja2 = new JSONArray(nomi_title_array);

            for(int j=0; j<ja2.length(); j++){
//                nominate_title_list = new ArrayList<>();
                String nomi_title = ja2.getString(j);
                Log.i("노미네이션 이름:  ", nomi_title);

                nominateListAdapter.addItem(nomi_title);
//                nominate_title_list.add(nomi_title);
                nominate_name = nomi_title;

            }
//            nominateListAdapter.notifyDataSetChanged();
            txtNominate_tag.setText(nomi_description);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {

        btn_sisang = (Button)findViewById(R.id.btn_sisang);
        btnBack = (Button)findViewById(R.id.btnBack);
        spinner_nominate = (Spinner)findViewById(R.id.spinner_nominate);
        list_nominate = (ListView)findViewById(R.id.list_nominate);
        txtNominate_tag = (TextView)findViewById(R.id.txtNominate_tag);
//        scrollView_nominate = (ScrollView)findViewById(R.id.scrollView_nominate);

    }

    // badgelist를 불러옴
    class GetNominate extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(NominateActivity.this);
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
                Log.d("nominate_test", args[0]);
                params.put("field", args[1]);
                Log.d("nominate_test", args[1]);


                result = jsonParser.makeHttpRequest(
                        URL_, "POST", params);


                if (result != null) {
                    Log.d("nominate_test", "result : " + result);
                    return result;
                } else {
                    Log.d("nominate_test", "result : null, doInBackground");
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
