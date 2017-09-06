package com.example.user.myapplication.MakeAward;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.user.myapplication.Award;
import com.example.user.myapplication.MainActivity;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MakeAwardActivity3 extends AppCompatActivity {
    
    EditText edit_caption;
    Button btnMakeAward, btnCancel;
    ToggleButton toggle_keyboard;

    InputMethodManager inputMethodManager;
    CallbackManager callbackManager;

    String title;
    Uri selPhotoUri;
    String caption;
    String field;
    String badge_id;
    String user_id;

    String absolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_award3);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();

        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);

        Intent intent = getIntent();
        title = intent.getStringExtra("Award_Name");
        absolutePath = getIntent().getStringExtra("Award_img");
        field = intent.getStringExtra("Award_field");
        badge_id = intent.getStringExtra("Award_badge");

        setEvent();
        
    }

    private void setEvent() {

        edit_caption.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_BACK){
                    hideKeyboard();
                }
                return false;
            }
        });

        edit_caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeAwardActivity3.this.finish();
            }
        });

        btnMakeAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                caption = edit_caption.getText().toString();

                if (TextUtils.isEmpty(caption)) {
                    caption = " ";


                    Log.e("captiontest", caption + "//" + edit_caption.getText().toString());
                    Log.e("AWARD", "업로드 시작" + title + "/" + caption + "/" + field + "/" + badge_id);
                    new MakeAward().execute(title, caption, field, badge_id);

                }else{
                    Log.e("captiontest", caption + "//" + edit_caption.getText().toString());
                    Log.e("AWARD", "업로드 시작" + title + "/" + caption + "/" + field + "/" + badge_id);
                    new MakeAward().execute(title, caption, field, badge_id);
                }



            }
        });

        // 키보드 show/hide 기능
        toggle_keyboard.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (toggle_keyboard.isChecked()) {
                    hideKeyboard();
                } else {
                    showKeyboard();
                }
            }
        });
    }

    // 키보드 보이기
    private void showKeyboard(){

        toggle_keyboard.setBackground(getDrawable(R.drawable.typeshowdown));
        inputMethodManager.showSoftInput(edit_caption,InputMethodManager.SHOW_IMPLICIT);

    }

    // 키보드 숨기기
    private void hideKeyboard(){

        toggle_keyboard.setBackground(getDrawable(R.drawable.typeshowup));
        inputMethodManager.hideSoftInputFromWindow(toggle_keyboard.getWindowToken(),0);
    }

    private void initView() {
        edit_caption = (EditText) findViewById(R.id.edit_caption);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnMakeAward = (Button) findViewById(R.id.btnMakeAward);
        toggle_keyboard = (ToggleButton)findViewById(R.id.toggle_keyboard);
        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

    }

    public class MakeAward extends AsyncTask<String, String, JSONObject> {

        Dialog loadingDialog;


        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(MakeAwardActivity3.this, "AWARD", "업로드 중");
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            String boundary = "*****";
            String delimiter = "\r\n--" + boundary + "\r\n";

            try {


                Log.d("absoltePath 실제 경로", absolutePath);

                FileInputStream mFileInputStream = new FileInputStream(absolutePath);
                URL connectUrl = new URL(Award.AWARD_URL + "Award_server/Award/makeaward_t.jsp" );
                Log.d("Test", "mFileInputStream  is " + mFileInputStream);


                StringBuffer postDataBuilder = new StringBuffer();

                postDataBuilder.append(delimiter);
                postDataBuilder.append(setValue("user_id", user_id));
                postDataBuilder.append(delimiter);
                postDataBuilder.append(setValue("caption", caption));
                postDataBuilder.append(delimiter);
                postDataBuilder.append(setValue("title", title));
                postDataBuilder.append(delimiter);
                postDataBuilder.append(setValue("field", field));
                postDataBuilder.append(delimiter);
                postDataBuilder.append(setValue("badge_id", badge_id));
                postDataBuilder.append(delimiter);
                postDataBuilder.append(setFile("uploadFile", absolutePath));
                postDataBuilder.append("\r\n");

                // open connection
                HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                        conn.getOutputStream()));

                // 위에서 작성한 메타데이터를 먼저 전송한다. (한글이 포함되어 있으므로 UTF-8 메소드 사용)
                out.writeUTF(postDataBuilder.toString());


                int bytesAvailable = mFileInputStream.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                byte[] buffer = new byte[bufferSize];
                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                Log.d("Test", "image byte is " + bytesRead);

                // read image
                while (bytesRead > 0) {
                    // dos.write(buffer, 0, bufferSize);
                    out.write(buffer);
                    bytesAvailable = mFileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                }

                out.writeBytes(delimiter);
                out.flush();
                out.close();
                //in.close();

            /*    dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                Log.e("Test" , "File is written");
                mFileInputStream.close();
                dos.flush(); // finish upload...*/

                // get response
                int ch;
                InputStream is = conn.getInputStream();
                StringBuffer b = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    b.append((char) ch);
                }
                String s = b.toString();
                Log.e("Test", "result = " + s);


                conn.getInputStream();
                conn.disconnect();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String setValue(String key, String value) {
            return "Content-Disposition: form-data; name=\"" + key + "\"r\n\r\n"
                    + value;
        }

        /**
         * 업로드할 파일에 대한 메타 데이터를 설정한다.
         *
         * @param key      : 서버에서 사용할 파일 변수명
         * @param fileName : 서버에서 저장될 파일명
         * @return
         */
        public String setFile(String key, String fileName) {
            return "Content-Disposition: form-data; name=\"" + key
                    + "\";filename=\"" + fileName + "\"\r\n";
        }

        protected void onPostExecute(JSONObject jsonObject) {

            loadingDialog.dismiss();

            Toast.makeText(MakeAwardActivity3.this, R.string.award_upload,Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MakeAwardActivity3.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            MakeAwardActivity3.this.finish();
           // Log.d("Test", "image byte is " + result);

        }
    }
}
