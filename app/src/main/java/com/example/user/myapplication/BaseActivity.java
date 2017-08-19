package com.example.user.myapplication;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by User on 2017-01-24.
 */

// 폰트 일괄 적용 - 다른 activity 에서 상속받기.

public class BaseActivity extends Activity {

    private static Typeface mTypeface = null;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        if(mTypeface == null){
            mTypeface = Typeface.createFromAsset(this.getAssets(),"fonts/NanumBarunGothic.otf");
        }

        setGlobalFont(getWindow().getDecorView());

    }

    private void setGlobalFont(View view){

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
                    setGlobalFont(v);
                }
            }
        }
    }
}
