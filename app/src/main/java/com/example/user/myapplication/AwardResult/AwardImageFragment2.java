package com.example.user.myapplication.AwardResult;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.LoginActivity;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.network.JSONParser;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by User on 2016-10-09.
 */
public class AwardImageFragment2 extends Fragment {


    public static final int MY_PERMISSION_STORAGE = 123;
    private static RecyclerView myImg_list_recycler;
    private AwardSubImageAdapter2 subImageAdapter;

    private GridLayoutManager gridLayoutManager;

    private Uri mImageUri;

    private ArrayList<String> imagesPathList = null;

    private String absolutePath;

    private Bitmap yourbitmap;
    private Bitmap resized;


    private Uri mImageCaptureUri;

    private JSONObject img_json;
    private String img_list;

    private String user_id;
    private String award_id;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    protected Bundle awardResultBundle = new Bundle();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagesPathList = new ArrayList<String>();

        user_id = new SharedPrefereneUtil(getContext()).getUser_id();
        Log.d("user_id",user_id);

        Intent intent = getActivity().getIntent();
        awardResultBundle = intent.getBundleExtra("awardResultBundle");
        award_id = awardResultBundle.getString("award_id");


        try {

            img_json = new ShowImg().execute(user_id, award_id).get(); // user_id. award_id
            img_list = img_json.getString("img");
            Log.d("이미지 총 리스트 ", img_list);

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
            View rootView = inflater.inflate(R.layout.fragment_award_image_2, container, false);
            initView(rootView);

            return rootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subImageAdapter = new AwardSubImageAdapter2();
        gridLayoutManager = new GridLayoutManager(getContext(),3);

//        myImg_list_recycler.setHasFixedSize(true);
        myImg_list_recycler.setLayoutManager(gridLayoutManager);
        myImg_list_recycler.setAdapter(subImageAdapter);

        loadList(img_list);
    }

    private void initView(View rootView) {
        myImg_list_recycler = (RecyclerView)rootView.findViewById(R.id.myImg_list_recycler);
    }

    @Override
    public void onResume() {
        super.onResume();
        //refreshItems();
        //loadList(img_list);
    }


    private void loadList(String result) {

        try {


            Gson gson = new Gson();

            Log.d("loadList", "func : " + img_list);
            JSONArray ImgListObj = new JSONArray(img_list);

            if(ImgListObj.length() == 0){
                Log.d("loadList", "func : 비었다");
            }else{
                for(int i = 0; i < ImgListObj.length(); i++) {
                    String Img_url = ImgListObj.getString(i);
                    Log.d("이미지 url :", Img_url);

                    //  SubImageData img_list = gson.fromJson(Img_url,SubImageData.class);

                    subImageAdapter.addItem(award_id, user_id, Img_url);
                }
            }

            subImageAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
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


        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("이미지 선택")
                .setNeutralButton("카메라롤에서 선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();


    }


    /* 앨범에서 이미지 */
    private void doTakeAlbumAction() {

        Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
        intent.putExtra("award_id",award_id);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        else if(resultCode == Activity.RESULT_OK  && requestCode == PICK_FROM_ALBUM){
            //  imagesPathList = new ArrayList<String>();

            Log.e("제대로","받았다");
            Log.e("data",data.getStringExtra("data"));

            String[] imagesPath =  data.getStringExtra("data").split("\\|");

            for (int i = 0; i < imagesPath.length; i++) {

                imagesPathList.add(imagesPath[i]);
                Log.w("img_:album", "file://" + imagesPathList.get(i));

                absolutePath = "file://" + imagesPathList.get(i);
                Log.w("absolutePath", absolutePath);
            }
        }

        subImageAdapter.notifyDataSetChanged();
    }

    // badgelist를 불러옴
    class ShowImg extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        String URL_ = Award.AWARD_URL + "Award_server/Award/myAward_Album_detail.jsp";

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
                        URL_, "POST", params);


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

    public class AwardSubImageAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final static String IMAGE_URL = Award.IMAGE_URL + "subImages/";

        private ArrayList<SubImageData> subImageDataArrayList = new ArrayList<SubImageData>();
        public Context mContext;
        public int position_;

        public AwardSubImageAdapter2() {

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            mContext = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.list_item_sub_img, parent, false);
            return new ImgViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final SubImageData subImageData = subImageDataArrayList.get(position);

            ImgViewHolder vi = (ImgViewHolder) holder;

            if(position==0){
                vi.sub_img.setImageResource(R.drawable.btn_plus_rectangle);
            }else if(position>0){
                Glide
                        .with(mContext)
                        .load(IMAGE_URL + subImageData.getSub_img())
                        .fitCenter()
                        .centerCrop()
                        .into(vi.sub_img);
            }

            vi.sub_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (position == 0){

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

                        TedPermission.with(getContext())
                                .setPermissionListener(permissionlistenr)
                                .setRationaleMessage("AWARD는 저장공간 접근이 필요합니다")
                                .setDeniedMessage("[설정] > [권한]에서 권한을 허용할 수 있습니다")
                                .setGotoSettingButton(true)
                                .setGotoSettingButtonText("설정")
                                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check();

                    }else if(position > 0){

                        Intent intent = new Intent(mContext.getApplicationContext(), ImageDetailActivity.class);
                        intent.putExtra("path", subImageData.getSub_img());
                        intent.putExtra("award_id",subImageData.getAward_id());
                        mContext.startActivity(intent);

                    }
                }
            });
        }

        public class ImgViewHolder extends RecyclerView.ViewHolder{
            ImageView sub_img;

            public ImgViewHolder(View view) {
                super(view);
                sub_img = (ImageView)view.findViewById(R.id.sub_img);
            }
        }

        /* item count */
        public int getBasicItemCount() {
            return subImageDataArrayList == null ? 0 : subImageDataArrayList.size();
        }

        @Override
        public int getItemCount() {
            return getBasicItemCount();
        }


        public void addItem(String award_id,String user_id,String sub_img){

            SubImageData item = new SubImageData();

            item.setAward_id(award_id);
            item.setUser_id(user_id);
            item.setSub_img(sub_img);

            subImageDataArrayList.add(item);

        }


    }
}