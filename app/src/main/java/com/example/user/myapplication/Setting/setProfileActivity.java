package com.example.user.myapplication.Setting;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

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
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class setProfileActivity extends AppCompatActivity {

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
                .load(user_img_path)
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
                // 텍스트 입력 전                // 입력이 끝났을 때
                txtChk.setText(" ");

                if(TextUtils.isEmpty(editName.getText())){
//                    btnChk.setEnabled(false);
                    btn_register.setEnabled(false);
                }

                else {
//                    btnChk.setEnabled(true);
                    btn_register.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 중복체크를 안 했을 경우
                if (TextUtils.isEmpty(getUser_name)) {
                    txtChk.setText("중복체크를 해주세요");
                    return;
                }

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

                                new SharedPrefereneUtil(getApplicationContext()).putSharedPreferences(user_name, user_img_path);
                                Toast.makeText(getApplicationContext(),"프로필을 변경하였습니다.",Toast.LENGTH_SHORT).show();
                                setProfileActivity.this.finish();

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


                PermissionListener permissionlistenr = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Log.d("permission","granted");
                        readAlbum();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Log.d("permission","denied");
                    }
                };

                TedPermission.with(getApplicationContext())
                        .setPermissionListener(permissionlistenr)
                        .setRationaleMessage("AWARD는 저장공간 접근이 필요합니다")
                        .setDeniedMessage("[설정] > [권한]에서 권한을 허용할 수 있습니다")
                        .setGotoSettingButton(true)
                        .setGotoSettingButtonText("설정")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();

            }
        });
    }
    public void readAlbum() {

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


        new android.app.AlertDialog.Builder(this)
                .setTitle("이미지 선택")
                .setNeutralButton("카메라롤에서 선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();


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
                if(new SharedPrefereneUtil(getApplicationContext()).getUser_name().equals(user_name)){
                    txtChk.setText("닉네임을 그대로 사용합니다");
                }else{
                    txtChk.setText("이미 존재하는 닉네임입니다");
                }

            }
            else if (getUser_name.equalsIgnoreCase("0")){
                txtChk.setText("닉네임이" + user_name + " 로 변경되었습니다");

            }

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
