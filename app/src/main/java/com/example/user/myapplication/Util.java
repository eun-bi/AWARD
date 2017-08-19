package com.example.user.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by User on 2017-01-25.
 */
public class Util {

    public static Typeface mTypeface = null;


    public static void setGlobalFont(Context context,View view){

        mTypeface = Typeface.createFromAsset(context.getAssets(),"fonts/NanumBarunGothic.otf");

        if(view != null){
            if(view instanceof ViewGroup){
                ViewGroup vg = (ViewGroup)view;
                int vgCnt = vg.getChildCount();
                for(int i=0 ; i<vgCnt ; i++){
                    View v = vg.getChildAt(i);
                    if(v instanceof TextView){
                        ((TextView)v).setTypeface(mTypeface);
                        // ((EditText)v).setTypeface(mTypeface);
                    }
                    setGlobalFont(context, v);
                }
            }
        }

    }
}
