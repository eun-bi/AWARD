package com.example.user.myapplication.Setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.example.user.myapplication.Award;
import com.example.user.myapplication.FieldSetActivity;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;
import com.example.user.myapplication.sqliteDBHelper;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.example.user.myapplication.sqliteDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class setProfileActivity extends AppCompatActivity {

    private static final String IMAGE_URL = Award.IMAGE_URL + "profile/";

    private Button btnCancel, btn_pic, btnChk, btn_register;
    private ImageView img_Profile;
    private TextView txtChk;
    private EditText editName;

    private String user_id;
    private String user_img_path;
    private String user_name;

    private String getUser_name; // 닉네임 중복체크

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;
    private Uri selPhotoUri;

    private String absolutePath;


    private JSONObject myprofile_json;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();
        setView();
        setEvent();
    }

    private void setView() {

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(getApplicationContext());
        user_id = prefereneUtil.getUser_id();
        user_name = prefereneUtil.getUser_name();
        user_img_path = prefereneUtil.getUser_img_path();

        Glide
                .with(setProfileActivity.this)
                .load(IMAGE_URL + user_img_path)
                .fitCenter()
                .centerCrop()
                .crossFade() // 이미지 로딩 시 페이드 효과
                .bitmapTransform(new CropCircleTransformation(setProfileActivity.this))  // image 원형
                .override(200, 200)
                .into(img_Profile);

          editName.setText(user_name);
    }


    private void setEvent() {


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfileActivity.this.finish();
            }
        });

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
                }

                else {
                    btnChk.setEnabled(true);
                    btn_register.setEnabled(true);
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
                    btn_register.setEnabled(false);
                    return;
                }


                // 중복체크를 안 했을 경우
                if (TextUtils.isEmpty(getUser_name)) {
                    txtChk.setText("중복체크를 해주세요");
                    return;
                }

                // nameset test - 지우기
                user_name = String.valueOf(editName.getText());

                Log.i("일련번호", user_id);
                Log.i("프로필 이미지", user_img_path);
                Log.i("닉네임", user_name);

                AlertDialog.Builder builder = new AlertDialog.Builder(setProfileActivity.this);
                builder.setMessage("프로필을 변경하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // 서버에 변경된 값 저장
                                new ProfileSetAsync().execute(user_id, user_name);

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.setTitle("AWARD");
                alert.setIcon(R.drawable.logo);
                alert.show();
            }
        });

        btnChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_name = String.valueOf(editName.getText());
                new chkAsync().execute(user_id, user_name);

            }

        });

        btn_pic.setOnClickListener(new View.OnClickListener() {
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

                new AlertDialog.Builder(setProfileActivity.this)
                        .setTitle("프로필 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
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
                                .with(setProfileActivity.this)
                                .load(user_img_path)
                                .fitCenter()
                                .centerCrop()
                                .crossFade() // 이미지 로딩 시 페이드 효과
                                .bitmapTransform(new CropCircleTransformation(setProfileActivity.this))  // image 원형
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

    public class chkAsync extends AsyncTask<String, String, JSONObject> {

        Dialog loadingDialog;
        private static final String urlString = Award.AWARD_URL + "Award_server/Award/login_check_nickname.jsp";

        JSONParser jsonParser = new JSONParser();
        // JSONObject jsonObject = new JSONObject();

        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(setProfileActivity.this, "Please wait", "Loading...");
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
                btnCancel.setEnabled(false);
            }
            else if (getUser_name.equalsIgnoreCase("0")){
                txtChk.setText(user_name + "님, 환영합니다");
                btnCancel.setEnabled(true);
            }


            // Log.d("Test", "image byte is " + result);

        }
    }

    class ShowMyProfile extends AsyncTask<String, String, JSONObject> {

        private static final String urlString = Award.AWARD_URL + "Award_server/Award/mypage_myprofile.jsp";
        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(setProfileActivity.this);
            pDialog.setMessage("프로필을 가져오는 중입니다.");
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
                Log.d("test_log", args[0]);


                result = jsonParser.makeHttpRequest(
                        urlString, "POST", params);


                if (result != null) {
                    Log.d("test_log", "result : " + result);
                    return result;
                } else {
                    Log.d("test_log", "result : null, doInBackground");
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

    public class ProfileSetAsync extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        Dialog loadingDialog;
        private static final String urlString1 = Award.AWARD_URL + "Award_server/Award/mypage_myprofileset.jsp";




        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(setProfileActivity.this, "Please wait", "프로필 저장 중...");
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

            Toast.makeText(setProfileActivity.this, "프로필 변경 성공", Toast.LENGTH_SHORT).show();
            setProfileActivity.this.finish();

        }

    }


    private void initView() {

        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnChk = (Button)findViewById(R.id.btnChk);
        btn_pic = (Button)findViewById(R.id.btn_pic);
        btn_register = (Button)findViewById(R.id.btn_register);
        img_Profile = (ImageView)findViewById(R.id.img_Profile);
        txtChk = (TextView)findViewById(R.id.txtChk);
        editName = (EditText)findViewById(R.id.editName);

    }
}
