package com.example.user.myapplication;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.network.JSONParser;
import com.example.user.myapplication.sqliteDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class NameSetActivity extends AppCompatActivity {

    TextView txtName, txtChk;
    EditText editName;
    Button btnChk, btn_register, btnImgSet, btnBack;
    ImageView img_Profile;

    private String user_name;

    private String user_id;
    private String user_img_path;
    private String user_name_a;

    private String getUser_name; // 닉네임 중복체크

    private Uri mImageCaptureUri;
    private Uri selPhotoUri;

    private String absolutePath;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_set);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();
        setVIew();
        setEvent();

    }


    private void setEvent() {

        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트 입력 전
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때


                txtChk.setText(" ");

                if(TextUtils.isEmpty(editName.getText())){
                    btnChk.setEnabled(false);
                    btn_register.setEnabled(false);
                    btn_register.setTextColor(ContextCompat.getColorStateList(NameSetActivity.this,R.color.white_40));
                }

                else {
                    btnChk.setEnabled(true);
                    btn_register.setEnabled(true);
                    btn_register.setTextColor(ContextCompat.getColorStateList(NameSetActivity.this,R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력이 끝났을 때
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(editName.getText())) {
                    txtChk.setText("닉네임을 입력은 필수입니다.");
                    return;
                }

                // 중복체크를 안 했을 경우
                if(TextUtils.isEmpty(getUser_name)){
                    txtChk.setText("중복체크를 해주세요");
                    return;
                }

                // nameset test - 지우기
                user_name = String.valueOf(editName.getText());

                Log.i("일련번호", user_id);
                Log.i("프로필 이미지", user_img_path);
                Log.i("닉네임", user_name);


                new NameSetAsync().execute(user_id, user_name);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameSetActivity.this.finish();
            }
        });

        btnImgSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePhotoAction();
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(NameSetActivity.this)
                        .setTitle("프로필 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });

        btnChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_name = String.valueOf(editName.getText());
                new chkAsync().execute(user_id, user_name);

            }

        });
    }

    /* 카메라에서 이미지 */
    private void doTakePhotoAction() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);

    }

    /* 앨범에서 이미지 */
    private void doTakeAlbumAction() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);

    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }


        switch (requestCode) {
            case CROP_FROM_CAMERA: {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if (extras != null) {

                    selPhotoUri = data.getData();

                    try {
                        Bitmap selPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), selPhotoUri);
//                        img_Profile.setImageBitmap(selPhoto);
//                        absolutePath = user_img_path;
                        Log.w("uri", selPhotoUri.toString()); // content://media/external/images/media/110

                       // 절대 경로로 변환
                        Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null, null, null, null);
                        c.moveToNext();
                        absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                        Log.w("absolutePath", absolutePath);

                        user_img_path = absolutePath;
                        Log.d("img_path",absolutePath);

                        Glide
                                .with(NameSetActivity.this)
                                .load(user_img_path)
                                .fitCenter()
                                .centerCrop()
                                .crossFade() // 이미지 로딩 시 페이드 효과
                                .bitmapTransform(new CropCircleTransformation(NameSetActivity.this))  // image 원형
                                .override(200,200)
                                .into(img_Profile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Bitmap photo = extras.getParcelable("data");

                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

/*                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);*/

                intent.putExtra("scale", true);
                intent.putExtra("output", mImageCaptureUri);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
        }
    }


    private void setVIew() {

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        user_img_path = intent.getStringExtra("user_img_path");
        user_name_a = intent.getStringExtra("user_name_a");

        txtName.setText(user_name_a);

        Glide
                .with(NameSetActivity.this)
                .load(user_img_path)
                .fitCenter()
                .centerCrop()
                .crossFade() // 이미지 로딩 시 페이드 효과
                .bitmapTransform(new CropCircleTransformation(NameSetActivity.this))  // image 원형
                .override(200,200)
                .into(img_Profile);

    }



    private void initView() {
        editName = (EditText) findViewById(R.id.editName);
        btnChk = (Button) findViewById(R.id.btnChk);
        btn_register = (Button) findViewById(R.id.btn_register);
        txtName = (TextView) findViewById(R.id.txtName);
        txtChk = (TextView) findViewById(R.id.txtChk);
        btnImgSet = (Button) findViewById(R.id.btnImgSet);
        btnBack = (Button) findViewById(R.id.btnBack);
        img_Profile = (ImageView) findViewById(R.id.img_Profile);
    }

    public class chkAsync extends AsyncTask<String, String, JSONObject> {

        Dialog loadingDialog;
        private static final String urlString = Award.AWARD_URL + "Award_server/Award/login_check_nickname.jsp";

        JSONParser jsonParser = new JSONParser();
       // JSONObject jsonObject = new JSONObject();

        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(NameSetActivity.this, "Please wait", "Loading...");
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", args[0]);
                params.put("user_name", args[1]);

                Log.i("user_id", user_id);
                Log.i("user_name", user_name);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        urlString, "POST", params);

                Log.d("server", json.getString("user_check"));
                getUser_name = json.getString("user_check");
                Log.d("server", getUser_name);

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

            if(getUser_name.equalsIgnoreCase("1")) {
                txtChk.setText("이미 존재하는 아이디입니다");
                btn_register.setEnabled(false);
            }
            else if (getUser_name.equalsIgnoreCase("0")){
                txtChk.setText(user_name + "님, 환영합니다");
                btn_register.setEnabled(true);
            }


            // Log.d("Test", "image byte is " + result);

        }
    }

        public class NameSetAsync extends AsyncTask<String, String, JSONObject> {

            JSONParser jsonParser = new JSONParser();
            Dialog loadingDialog;
            private static final String urlString1 = Award.AWARD_URL + "Award_server/Award/login_first.jsp";




            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(NameSetActivity.this, "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... args) {


                String boundary = "*****";
                String delimiter = "\r\n--" + boundary + "\r\n";

                try {

                    FileInputStream mFileInputStream = new FileInputStream(user_img_path);
                    URL connectUrl = new URL(urlString1);
                    Log.d("Test", "mFileInputStream  is " + mFileInputStream);


                    StringBuffer postDataBuilder = new StringBuffer();

                    postDataBuilder.append(delimiter);
                    postDataBuilder.append(setValue("user_id", user_id));
                    postDataBuilder.append(delimiter);
                    postDataBuilder.append(setValue("user_name", user_name));
                    postDataBuilder.append(delimiter);
                    postDataBuilder.append(setFile("uploadFile", user_img_path));
                    postDataBuilder.append("\r\n");

                    // open connection
                    HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    //   conn.setRequestProperty("uploadedfile",absolutePath);
                    //    conn.setRequestProperty("caption",caption);
                    //   conn.setRequestProperty("title",title);

                    //  FileInputStream in = new FileInputStream(filePath);
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

                    //mEdityEntry.setText(s);
                    // out.close();

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

                Toast.makeText(NameSetActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                /* sharedpreferences 로그인 정보 저장 */
                SharedPrefereneUtil sharedPrefereneUtil = new SharedPrefereneUtil(getApplicationContext());
                sharedPrefereneUtil.putSharedPreferences(user_id, user_name, user_img_path);
                sharedPrefereneUtil.putLoginchk(true);

                Intent intent = new Intent(NameSetActivity.this, FieldSetActivity.class);
                startActivity(intent);
                NameSetActivity.this.finish();
                // Log.d("Test", "image byte is " + result);

            }

        }
    }
