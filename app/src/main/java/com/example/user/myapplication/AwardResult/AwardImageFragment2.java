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

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.MakeAward.MakeAwardActivity3;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.network.JSONParser;
import com.google.gson.Gson;

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

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(getContext());
        user_id = prefereneUtil.getSharedPreferences("user_id",user_id);

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

                subImageAdapter = new AwardSubImageAdapter2();
                gridLayoutManager = new GridLayoutManager(getContext(),3);

//        myImg_list_recycler.setHasFixedSize(true);
                myImg_list_recycler.setLayoutManager(gridLayoutManager);
                myImg_list_recycler.setAdapter(subImageAdapter);

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


    private boolean checkPermission() {

        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle("Permission necessary");
                    builder.setMessage("Write storage permission is necessary to write picture!!!");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }else{
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
                }
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case MY_PERMISSION_STORAGE :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readAlbum();
                }else{

                }
                break;
        }
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


        new android.app.AlertDialog.Builder(getActivity())
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

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);

    }

    /* 앨범에서 이미지 */
    private void doTakeAlbumAction() {

        /* 사진 다중 선택 */
//        Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
//        intent.putExtra("award_id",award_id);
//        startActivity(intent);

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);

        //intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 이미지 여러장
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_ALBUM);
//        startActivityForResult(intent, PICK_FROM_ALBUM);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

//        else if(resultCode == Activity.RESULT_OK  && requestCode == PICK_FROM_ALBUM){
//                //  imagesPathList = new ArrayList<String>();
//
//                Log.w("제대로","받았다");
//                Log.w("data",data.getStringExtra("data"));
//
//                String[] imagesPath =  data.getStringExtra("data").split("\\|");
//
//                for (int i = 0; i < imagesPath.length; i++) {
//
//                    imagesPathList.add(imagesPath[i]);
//                    Log.w("img_:album", "file://" + imagesPathList.get(i));
//
//                    absolutePath = "file://" + imagesPathList.get(i);
//                    Log.w("absolutePath", absolutePath);
//                }
//        }


        switch (requestCode) {

            case CROP_FROM_CAMERA :{
                Log.w("state", "카메라");

                final Bundle extras = data.getExtras();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if(extras != null){

                    mImageUri = data.getData();
                    Log.w("camera_img_path", mImageUri.toString());

                    //new UploadImg().execute(award_id);
//
//                    try {
//                        Bitmap mlmageImg =  MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
////                    subImageAdapter.addItem(1, "test", mlmageImg);
//
////                    Cursor c = getActivity().getContentResolver().query(Uri.parse(mImageUri.toString()), null, null, null, null);
////                    c.moveToFirst();
////                    absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
////                    Log.w("absolutePath", absolutePath);
//
//                        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(mImageUri.toString()),null, null, null, null);
//                        cursor.moveToNext();
//
////                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        absolutePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
//                        Log.w("camera", absolutePath);
//
//                    }
//
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }



                    //subImageAdapter.addItem(1, "test", absolutePath);

                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM:{
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA:{
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
                intent.putExtra("return-data", true);
                intent.putExtra("output", mImageCaptureUri);
                startActivityForResult(intent, CROP_FROM_CAMERA);

            }
        }

        subImageAdapter.notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];


                if ("primary".equalsIgnoreCase(type)) {

                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                Log.d("content_uri", contentUri.toString());
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                //이부분 매우중요
                Log.d("content_provider", "" + contentUri + "/" + selectionArgs);
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
            Log.d("media_parser1", uri.toString());
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.d("media_parser2", uri.toString());
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {

            Log.d("media_parser3", uri.toString());
            return uri.getPath();

        }
        Log.d("media_parser4", uri.toString());
        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };


        try {

            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                Log.d("content_provider5", "" + uri + "/" + index);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
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
                            .override(100,100)
                            .thumbnail(0.1f)
                            .into(vi.sub_img);
                }

            vi.sub_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (position == 0){
                        readAlbum();

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