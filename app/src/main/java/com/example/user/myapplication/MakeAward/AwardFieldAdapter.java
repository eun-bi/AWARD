package com.example.user.myapplication.MakeAward;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.Util;

import java.util.ArrayList;

/**
 * Created by User on 2016-10-18.
 */
public class AwardFieldAdapter extends BaseAdapter {

    private ArrayList<AwardField> awardFields = new ArrayList<AwardField>();

    public AwardFieldAdapter(){}

    @Override
    public int getCount() {
        return awardFields.size();
    }

    @Override
    public Object getItem(int position) {
        return awardFields.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if(convertView ==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_field_item,parent,false);
            Util.setGlobalFont(context, convertView); // font 적용
        }

        TextView field = (TextView)convertView.findViewById(R.id.txtAward_field);
        CheckBox chkField = (CheckBox)convertView.findViewById(R.id.chkField);

        AwardField awardField = awardFields.get(position);
        field.setText(awardField.getField());

        chkField.setChecked(((ListView)parent).isItemChecked(pos));
        chkField.setFocusable(false);
        chkField.setClickable(false);


        return convertView;
    }

    public void addItem(String field){
        AwardField item = new AwardField();

        item.setField(field);

        awardFields.add(item);
    }

}
