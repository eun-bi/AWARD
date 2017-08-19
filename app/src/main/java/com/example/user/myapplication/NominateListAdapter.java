package com.example.user.myapplication;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.myapplication.MakeAward.MakeAwardActivity1;
import com.example.user.myapplication.MyAward.AwardsDetailActivity;
import com.example.user.myapplication.MyBadge.AwardBadge;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by User on 2017-02-07.
 */
public class NominateListAdapter extends BaseAdapter {

    private ArrayList<Nominate> nominate = new ArrayList<Nominate>();


    public NominateListAdapter() {

    }

    public void addItem(String title) {
        Nominate item = new Nominate();
        item.setNomi_title(title);
        nominate.add(item);

        notifyDataSetChanged();

    }

    public void update(){
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return nominate.size();
    }

    @Override
    public Object getItem(int position) {
        return nominate.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_nominate_item,parent,false);
            Util.setGlobalFont(context, convertView); // font 적용
        }

        TextView nominate_name = (TextView)convertView.findViewById(R.id.txtNominate_item);
        CheckBox chkNominate = (CheckBox)convertView.findViewById(R.id.chkNominate);

        final Nominate nominate_ = nominate.get(position);
        nominate_name.setText(nominate_.getNomi_title());


        chkNominate.setChecked(((ListView)parent).isItemChecked(pos));
        chkNominate.setFocusable(false);
        chkNominate.setClickable(false);
        
        return convertView;

    }
}
