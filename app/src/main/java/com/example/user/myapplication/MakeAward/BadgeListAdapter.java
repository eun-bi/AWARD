package com.example.user.myapplication.MakeAward;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.myapplication.Award;
import com.example.user.myapplication.MyBadge.AwardBadge;
import com.example.user.myapplication.R;
import com.example.user.myapplication.Util;

import java.util.ArrayList;

/**
 * Created by User on 2016-08-07.
 */
public class BadgeListAdapter extends ArrayAdapter<AwardBadge> {

    private ArrayList<AwardBadge> badge = null;
    Context mContext;
    LayoutInflater mlayoutinflater;

    public BadgeListAdapter(Context context, int textViewResourceId, ArrayList<AwardBadge> badges) {
        super(context,textViewResourceId, badges);
        mContext = context;
        mlayoutinflater = LayoutInflater.from(mContext);
        this.badge = new ArrayList<>();
        this.badge.addAll(badges);
    }

    private class ViewHolder{
        ImageView imgBadge;
        TextView txtBadge;
        TextView txtBadge_explain;

    }

    public void add(AwardBadge mybadge) {
        this.badge.add(mybadge);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null){
            convertView = mlayoutinflater.inflate(R.layout.activity_list_badge,null);
            Util.setGlobalFont(mContext, convertView); // font 적용
            viewHolder = new ViewHolder();

            viewHolder.imgBadge = (ImageView)convertView.findViewById(R.id.imgBadge);
            viewHolder.txtBadge = (TextView)convertView.findViewById(R.id.txtBadge);
            viewHolder.txtBadge_explain = (TextView)convertView.findViewById(R.id.txtBadge_explain);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CheckBox chkBadge = (CheckBox)convertView.findViewById(R.id.chkBadge);
        AwardBadge mybadge = this.badge.get(position);

        viewHolder.txtBadge.setText(mybadge.getBadge_name());
        viewHolder.txtBadge_explain.setText(mybadge.getBadge_tag());

        chkBadge.setChecked(((ListView) parent).isItemChecked(position));
        chkBadge.setFocusable(false);
        chkBadge.setClickable(false);

        return convertView;
    }

}
