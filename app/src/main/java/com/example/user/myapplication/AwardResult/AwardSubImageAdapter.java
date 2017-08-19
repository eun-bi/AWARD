package com.example.user.myapplication.AwardResult;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.R;

import java.util.ArrayList;

/**
 * Created by User on 2016-10-11.
 */
public class AwardSubImageAdapter extends BaseAdapter {

    private final static String IMAGE_URL = Award.IMAGE_URL + "subImages/";

    private ArrayList<SubImageData> subImageDataArrayList = new ArrayList<SubImageData>();

    public AwardSubImageAdapter() {}

    @Override
    public int getCount() {
        return subImageDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return subImageDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        AwardSubImageViewHolder subImageViewHolder = null;

        if (convertView == null) {

            subImageViewHolder = new AwardSubImageViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_sub_img, parent, false);

            subImageViewHolder.sub_img = (ImageView) convertView.findViewById(R.id.sub_img);

            if(position == 0){
                subImageViewHolder.sub_img.setImageResource(R.drawable.btn_plus_rectangle);
            }

            convertView.setTag(subImageViewHolder);

        }
        else {
            subImageViewHolder = (AwardSubImageViewHolder) convertView.getTag();
        }

     //   subImageViewHolder = (AwardSubImageViewHolder) convertView.getTag();
        SubImageData subImageData = subImageDataArrayList.get(position);

        Glide
                .with(context)
                .load(IMAGE_URL + subImageData.getSub_img())
                .override(100, 100)
                .fitCenter()
                .centerCrop()
                .thumbnail(0.1f)
                .into(subImageViewHolder.sub_img);

        convertView.setTag(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ImageDetailActivity.class);
                intent.putExtra("path",subImageDataArrayList.get(position).getSub_img());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void addItem(String award_id,String user_id,String sub_img){

        SubImageData item = new SubImageData();

        item.setAward_id(award_id);
        item.setUser_id(user_id);
        item.setSub_img(sub_img);

        subImageDataArrayList.add(item);

    }

    public void deletItem(String sub_img){
        subImageDataArrayList.remove(sub_img);
    }

    public class AwardSubImageViewHolder {
        ImageView sub_img;
    }


}