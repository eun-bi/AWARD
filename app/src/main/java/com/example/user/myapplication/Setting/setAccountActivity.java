package com.example.user.myapplication.Setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;

public class setAccountActivity extends AppCompatActivity {

    private Button btnCancel;
    private Switch switchFb, switchKakao;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_account);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();
        setView();
        setEvent();
    }

    private void setView() {

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(setAccountActivity.this);
        user_id = prefereneUtil.getSharedPreferences("user_id","");


    }

    private void setEvent() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAccountActivity.this.finish();
            }
        });


    }

    private void initView() {
        btnCancel = (Button)findViewById(R.id.btnCancel);
        switchFb = (Switch)findViewById(R.id.switchFb);
        switchKakao = (Switch)findViewById(R.id.switchKakao);
    }
}
