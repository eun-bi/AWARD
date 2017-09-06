package com.example.user.myapplication.AwardResult;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.example.user.myapplication.network.JSONParser;

import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.List;

public class ImageSelectActivity extends AppCompatActivity {

    private GridView img_select_gridview;
    private Button btnSelect,btnCancel;

    private ImageSelectAdapter imageSelectAdapter;

    private int count;
    private String[] arrPath;
    private String[] arrPath_;

  //  private String[] abso
    private int ids[];
    private boolean[] thumbnailsselection;

    private String absolutePath;
    private int len;
    private int cnt;

    private String user_id;
    private String award_id;

    ArrayList<GalleryImg> galleryImgArrayList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();

        user_id = new SharedPrefereneUtil(getApplicationContext()).getUser_id();
        Log.d("user_id",user_id);

        Intent intent = getIntent();
        award_id = intent.getStringExtra("award_id");

        img();
        setEvent();


    }

    private void initView() {

        img_select_gridview = (GridView)findViewById(R.id.img_select_gridview);
        btnSelect = (Button)findViewById(R.id.btnSelect);
        btnCancel = (Button)findViewById(R.id.btnCancel);

    }

    private void img() {


        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
        int image_column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = cursor.getCount();
        this.arrPath = new String[this.count];
        ids = new int[count];
        this.thumbnailsselection = new boolean[this.count];

        len = thumbnailsselection.length;
        this.arrPath_ = new String[this.len];

        for (int i = 0; i < this.count; i++) {
            cursor.moveToPosition(i);
            ids[i] = cursor.getInt(image_column_index);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = cursor.getString(dataColumnIndex);

            // gridview 스크롤뷰 버벅거리는 문제 해결 -> glide
            GalleryImg galleryimg = new GalleryImg();
            galleryimg.setGallery_img_url(arrPath[i]);
            galleryImgArrayList.add(galleryimg);



         //   absolutePath =  arrPath[i];
        }

        cursor.close();

        imageSelectAdapter = new ImageSelectAdapter(this,R.layout.list_item_img_select,galleryImgArrayList);
        img_select_gridview.setAdapter(imageSelectAdapter);
        img_select_gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        img_select_gridview.setTextFilterEnabled(true);

    }

    private void setEvent() {

        img_select_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imageSelectAdapter.notifyDataSetChanged();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageSelectActivity.this.finish();

            }
        });


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                len = thumbnailsselection.length;

                cnt = 0;


                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
//                        selectImages = selectImages + arrPath[i] + " |";
                        Log.d("개별 url ", arrPath[i]);
                    //    absolutePath = arrPath[i];

                    }
                }
                new imageUploadAsync().execute(user_id, award_id);
                Log.d("선택한 사진 갯수", "" + cnt);

                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                } else {
//                    new imageUploadAsync().execute(Award.user_id, Award.award_id);
                }




//                    Log.d("SelectedImages", selectImages);
//                    Intent i = new Intent();
//                    i.putExtra("data", selectImages);
//                    setResult(RESULT_OK, i);


//                    new imageUploadAsync().execute(arrPath[i]);

            }
        });
    }

    public class imageUploadAsync extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        Dialog loadingDialog;
        private static final String urlString1 = Award.AWARD_URL+"Award_server/Award/myAward_Album_add.jsp";


        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(ImageSelectActivity.this, "AWARD", "이미지 추가중");
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            String boundary = "*****";
            String delimiter = "\r\n--" + boundary + "\r\n";

            try {
                len = thumbnailsselection.length;

                for(int i=0; i<len;i++) {
                    if (thumbnailsselection[i]) {
                        FileInputStream mFileInputStream = new FileInputStream(arrPath[i]);
                        Log.d("이미지 테스트ㅡ", arrPath[i]);
                        URL connectUrl = new URL(urlString1);
                        Log.d("Test", "mFileInputStream  is " + mFileInputStream);

                        StringBuffer postDataBuilder = new StringBuffer();

                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("user_id", user_id));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("award_id", award_id));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setFile("uploadFile", arrPath[i]));
                        postDataBuilder.append("\r\n");


                        // open connection
                        HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setConnectTimeout(500000);
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
                    }

                }
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

            Toast.makeText(ImageSelectActivity.this, "이미지를 추가하였습니다", Toast.LENGTH_SHORT).show();
            ImageSelectActivity.this.finish();
        }

    }

    public class ImageSelectAdapter extends ArrayAdapter<GalleryImg> {

        private LayoutInflater layoutInflater = null;
        private ArrayList<GalleryImg> imgs = null;

        public ImageSelectAdapter(Context context, int textViewResourceId, ArrayList<GalleryImg> galleryImgs) {
            super(context, textViewResourceId, galleryImgs);

            layoutInflater = LayoutInflater.from(context);
            this.imgs = new ArrayList<>();
            this.imgs.addAll(galleryImgs);
        }


        @Override
        public int getCount() {
            return count;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ImageSelectViewHolder imageSelectViewHolder;

            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.list_item_img_select,null);
                imageSelectViewHolder = new ImageSelectViewHolder();
                imageSelectViewHolder.img_select = (ImageView)convertView.findViewById(R.id.img_select);
                imageSelectViewHolder.img_checkbox = (CheckBox)convertView.findViewById(R.id.img_checkbox);
                convertView.setTag(imageSelectViewHolder);
            }
            else{
                imageSelectViewHolder = (ImageSelectViewHolder)convertView.getTag();
            }

            imageSelectViewHolder.img_checkbox.setId(position);
            imageSelectViewHolder.img_select.setId(position);

            imageSelectViewHolder.img_checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });

            imageSelectViewHolder.img_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = imageSelectViewHolder.img_checkbox.getId();
                    if (thumbnailsselection[id]) {
                        imageSelectViewHolder.img_checkbox.setChecked(false);
                        Log.w("chk?", imageSelectViewHolder.img_checkbox.toString());
                        thumbnailsselection[id] = false;
                    } else {
                        imageSelectViewHolder.img_checkbox.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });

            GalleryImg img = this.imgs.get(position);

                Glide
                        .with(getApplicationContext())
                        .load(img.getGallery_img_url())
                        .fitCenter()
                        .centerCrop()
                        .into(imageSelectViewHolder.img_select);


            imageSelectViewHolder.img_checkbox.setChecked(thumbnailsselection[position]);
            imageSelectViewHolder.id = position;

//            imageSelectViewHolder.img_checkbox.setFocusable(false);
//            imageSelectViewHolder.img_checkbox.setClickable(false);

            return convertView;
        }
    }

    class ImageSelectViewHolder {

        int id;
        ImageView img_select;
        CheckBox img_checkbox;
    }
}
