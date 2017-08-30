package com.example.user.myapplication.MakeAward;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.MainActivity;
import com.example.user.myapplication.NameSetActivity;
import com.example.user.myapplication.R;
import com.example.user.myapplication.Util;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sun.jna.platform.win32.WinNT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MakeAwardActivity1 extends AppCompatActivity {

    Button btnNext, btnCancel;
    ImageButton btnImg;
    EditText edit_title;
    ListView list_field;

    String field;

    String nominate;
    String absolutePath;

    private Uri mImageCaptureUri;
    private Uri selPhotoUri;

    private AwardFieldAdapter awardFieldAdapter;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_award1);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();
        makeList();
        Img_(); // 이미지
        setEvent();
    }


    private void makeList() {
        awardFieldAdapter = new AwardFieldAdapter();
        list_field.setAdapter(awardFieldAdapter);
        list_field.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setFieldList();

        // nominate 에서 시상하기로 넘어왔을 경우
        Intent intent = getIntent();
        field = intent.getStringExtra("Award_field");
        nominate = intent.getStringExtra("nominate");

        if(TextUtils.isEmpty(field)){
            Log.i("intent test","x");
        }
        else{
            Log.d("intent field", field);
            awardFieldAdapter.notifyDataSetChanged();
        }

        if(TextUtils.isEmpty(nominate)){
            Log.i("intent test","x");
        }
        else{
            edit_title.setText(nominate);
        }
    }

    private void setFieldList() {
        awardFieldAdapter.addItem("영화");
        awardFieldAdapter.addItem("드라마");
        awardFieldAdapter.addItem("애니메이션");
        awardFieldAdapter.addItem("웹툰");
        awardFieldAdapter.addItem("만화");
        awardFieldAdapter.addItem("소설");
        awardFieldAdapter.addItem("음악");
        awardFieldAdapter.addItem("연극");
        awardFieldAdapter.addItem("뮤지컬");
    }

    private void setEvent() {

        edit_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트뷰, 리스트뷰의 변화가 아닌 뷰 전체의 변화가 있을 시에 체크하도록
                if (TextUtils.isEmpty(edit_title.getText())) {
                    btnNext.setEnabled(false);
                    btnNext.setTextColor(ContextCompat.getColorStateList(MakeAwardActivity1.this,R.color.white_40));
                }
                else{
                    btnNext.setEnabled(true);
                    btnNext.setTextColor(ContextCompat.getColorStateList(MakeAwardActivity1.this,R.color.white));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        list_field.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    field = "1";
                }
                if(position==1){
                    field = "2";
                }
                if(position==2){
                    field = "3";
                }
                if(position==3){
                    field = "4";
                }
                if(position==4){
                    field = "5";
                }
                if(position==5){
                    field = "6";
                }
                if(position==6){
                    field = "7";
                }
                if(position==7){
                    field = "8";
                }
                if(position==8){
                    field = "9";
                }

                Log.d("field", field);
                awardFieldAdapter.notifyDataSetChanged();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setBack();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(field)){
                    Toast.makeText(getApplicationContext(),"분야를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), MakeAwardActivity2.class);
                intent.putExtra("Award_Name", edit_title.getText().toString());
                intent.putExtra("Award_img", absolutePath);
                intent.putExtra("Award_field", field);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

            }
        });

    }


    private void Img_() {
        btnImg.setOnClickListener(new View.OnClickListener() {
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
                        .setRationaleMessage("AWARD는 저장공간과 카메라 접근이 필요합니다")
                        .setDeniedMessage("[설정] > [권한]에서 권한을 허용할 수 있습니다")
                        .setGotoSettingButton(true)
                        .setGotoSettingButtonText("설정")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();

            }
        });
    }

    public void readAlbum() {


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


        new android.app.AlertDialog.Builder(this)
                .setTitle(" + ")
                .setPositiveButton("촬영", cameraListener)
                .setNeutralButton("카메라롤에서 선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();


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
                      Bitmap selPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(),selPhotoUri);
                      btnImg.setImageBitmap(selPhoto);

                      Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null, null, null, null);
                      c.moveToFirst();
                      absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                      Log.d("absoltePath 실제 경로", absolutePath);

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

                intent.putExtra("scale",true);
                intent.putExtra("output",mImageCaptureUri);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setBack();
    }

    private void setBack() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("게시물을 삭제하시겠어요?");

        // OK 버튼 이벤트
        dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        // Cancel 버튼 이벤트
        dialog.setNegativeButton("유지",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }



    private void initView() {
        edit_title = (EditText)findViewById(R.id.edit_title);
        btnImg = (ImageButton) findViewById(R.id.btnImg);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        list_field = (ListView)findViewById(R.id.list_field);
    }
}

