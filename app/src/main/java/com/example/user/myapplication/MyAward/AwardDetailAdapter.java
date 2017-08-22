package com.example.user.myapplication.MyAward;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.AwardResult.AwardResultActivity;
import com.example.user.myapplication.MyAward.AwardDetail;
import com.example.user.myapplication.R;
import com.example.user.myapplication.Util;

import java.util.ArrayList;

/**
 * Created by User on 2016-09-13.
 */
public class AwardDetailAdapter extends BaseAdapter {


    private final static String IMAGE_URL = Award.IMAGE_URL + "Image/";

    private ArrayList<AwardDetail> awardDetaillist = new ArrayList<AwardDetail>();

    public AwardDetailAdapter() {}

    @Override
    public int getCount() {
        return awardDetaillist.size();
    }

    @Override
    public Object getItem(int position) {
        return awardDetaillist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_awards_detail, parent, false);
            Util.setGlobalFont(context, convertView); // font 적용
        }

        ImageView img_award = (ImageView)convertView.findViewById(R.id.img_award);
        TextView txtAward_title = (TextView)convertView.findViewById(R.id.txtAward_title);
        TextView txtAward_date = (TextView)convertView.findViewById(R.id.txtAward_date);

        final AwardDetail awardDetail = awardDetaillist.get(position);

        Glide
                .with(context)
                .load(IMAGE_URL + awardDetail.getImg_award())
                .fitCenter()
                .centerCrop()
                .crossFade()
                .thumbnail(0.1f)
                .into(img_award);

        txtAward_title.setText(awardDetail.getTxtAward_title());
        txtAward_date.setText(awardDetail.getTxtAward_date());

        convertView.setTag(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, AwardResultActivity.class);
//                intent.putExtra("award_id", awardDetail.getAward_id());
//                intent.putExtra("award_next","0");
//                Log.d("award_id", awardDetaillist.get(position).getAward_id());
//                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void addItem(String award_id, String img,String title,String date){

        AwardDetail item = new AwardDetail();

        item.setAward_id(award_id);
        item.setImg_award(img);
        item.setTxtAward_title(title);
        item.setTxtAward_date(date);

        awardDetaillist.add(item);

    }

}
