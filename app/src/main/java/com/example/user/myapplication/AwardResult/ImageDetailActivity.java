package com.example.user.myapplication.AwardResult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.R;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class ImageDetailActivity extends AppCompatActivity {


    private final static String IMAGE_URL = Award.IMAGE_URL + "subImages/";

    private static final String URL_ = Award.AWARD_URL + "Award_server/Award/delete_SubImg.jsp";

    private Button btn_back;
    private ImageView img_sub_full;

    private String img_path;
    private JSONObject json;
    private String delete_chk;

    private String award_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용
        
        initView();
        setView();
        setEvent();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            new imgDelAsync().execute(award_id, img_path);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setView() {

        Intent intent = getIntent();
        img_path = intent.getStringExtra("path");
        award_id = intent.getStringExtra("award_id");
        Log.d("img_path", img_path);
        Log.d("award_id", award_id);

        Glide
                .with(this)
                .load(IMAGE_URL + img_path)
                .fitCenter()
                .crossFade()
                .centerCrop()
                .thumbnail(0.1f)
                .into(img_sub_full);

    }

    private void setEvent() {

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDetailActivity.this.finish();
            }
        });

    }

    private void initView() {
        btn_back = (Button)findViewById(R.id.btn_back);
        img_sub_full = (ImageView)findViewById(R.id.img_sub_full);
    }

    // 서버에 삭제 요청
    class imgDelAsync extends AsyncTask<String, String, JSONObject> {

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
                Log.d("img_delete", args[0]);
                params.put("img_path", args[1]);
                Log.d("img_delete", args[1]);


                result = jsonParser.makeHttpRequest(
                        URL_, "POST", params);

                delete_chk = result.getString("img_delete");

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

            if(delete_chk.equalsIgnoreCase("1")) {
                Toast.makeText(getApplicationContext(), "이미지 삭제 완료", Toast.LENGTH_SHORT).show();
                ImageDetailActivity.this.finish();
            }
            else if (delete_chk.equalsIgnoreCase("0")){
                Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
            }

        } // AsyncTask 끝
    }
}
