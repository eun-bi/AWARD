package com.example.user.myapplication.MyPage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.Util;

import java.util.ArrayList;

/**
 * Created by User on 2016-08-22.
 */
public class MenuAdapter extends BaseAdapter{

    private ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();

    public MenuAdapter() {}

        @Override
    public int getCount() {
        return menuItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_mypage, parent, false);
            Util.setGlobalFont(context, convertView); // font 적용
        }

            ImageView menu_img = (ImageView)convertView.findViewById(R.id.menu_img);
            TextView menu_name = (TextView)convertView.findViewById(R.id.menu_name);

            MenuItem menuItem = menuItemList.get(position);

            menu_img.setImageDrawable(menuItem.getMenu_img());
            menu_name.setText(menuItem.getMenu_name());

            return convertView;
    }

    public void addItem(Drawable img,String name){
        MenuItem item = new MenuItem();

        item.setMenu_img(img);
        item.setMenu_name(name);

        menuItemList.add(item);
    }

}
