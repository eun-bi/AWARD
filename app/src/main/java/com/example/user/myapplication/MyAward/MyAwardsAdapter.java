package com.example.user.myapplication.MyAward;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.Util;

import java.util.ArrayList;

/**
 * Created by User on 2016-09-13.
 */
public class MyAwardsAdapter extends BaseAdapter {

    private ArrayList<MyAwards> alist = new ArrayList<MyAwards>();

    public MyAwardsAdapter(){}


    @Override
    public int getCount() {
        return alist.size();
    }

    @Override
    public Object getItem(int position) {
        return alist.get(position);
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
            convertView = inflater.inflate(R.layout.list_myawards, parent, false);
            Util.setGlobalFont(context, convertView); // font 적용
        }

        TextView txtAward_name = (TextView)convertView.findViewById(R.id.txtAward_name);
        TextView txtAward_num = (TextView)convertView.findViewById(R.id.txtAward_num);

        MyAwards awardsMenu = alist.get(position);

        txtAward_name.setText(awardsMenu.getAward_name() + " 어워드");
        txtAward_num.setText(awardsMenu.getAward_num());

        convertView.setTag(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AwardsDetailActivity.class);
                intent.putExtra("field", alist.get(position).getAward_name());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void addItem(String name, String num){

        MyAwards item = new MyAwards();

        item.setAward_name(name);
        item.setAward_num(num);

        alist.add(item);
    }
}
