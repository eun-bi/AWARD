package com.example.user.myapplication.MyBadge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.Util;

import java.util.ArrayList;

/**
 * Created by User on 2017-05-01.
 */
public class unlikeExpandableListViewAdapter extends BaseExpandableListAdapter{

    private Context mContext;
    private ArrayList<String> mParentList;
    private ArrayList<ArrayList<String>> mChildList;

    public unlikeExpandableListViewAdapter (Context context, ArrayList<String> parentList, ArrayList<ArrayList<String>> childList){
        this.mContext = context;
        this.mParentList = parentList;
        this.mChildList = childList;
    }


    @Override
    public int getGroupCount() {
        return this.mParentList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mParentList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String ParentText = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater groupInfla = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = groupInfla.inflate(R.layout.list_item_like_parent, parent, false);
            Util.setGlobalFont(mContext, convertView); // font 적용
        }

        TextView parent_like = (TextView)convertView.findViewById(R.id.like_parent);
        parent_like.setText(ParentText);

        ImageView img_chk_list = (ImageView)convertView.findViewById(R.id.img_chk_list);
        if(isExpanded){
            img_chk_list.setImageResource(R.drawable.more_up);
        }else{
            img_chk_list.setImageResource(R.drawable.more_gold);
        }
        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mChildList.size();
    }



    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mChildList.get(groupPosition).get(childPosition);
    }



    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if(convertView == null) {
            LayoutInflater childInfla = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = childInfla.inflate(R.layout.list_item_like_child, null);
            Util.setGlobalFont(mContext, convertView); // font 적용
        }


        TextView like_child = (TextView)convertView.findViewById(R.id.like_child);

        if(childText == null){
            like_child.setText(" ");
        }

        like_child.setText(childText);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
