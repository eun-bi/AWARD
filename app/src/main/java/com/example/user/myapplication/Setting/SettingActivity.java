package com.example.user.myapplication.Setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.myapplication.Award;
import com.example.user.myapplication.LoginActivity;
import com.example.user.myapplication.R;
import com.example.user.myapplication.SharedPrefereneUtil;
import com.example.user.myapplication.Util;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class SettingActivity extends AppCompatActivity {

    Button btnCancel;
    TextView setAccount,setProfile,setNotice, ask, info, logout;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Util.setGlobalFont(this, getWindow().getDecorView()); // font 적용

        initView();

        SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(getApplicationContext());
        user_id = prefereneUtil.getSharedPreferences("user_id", user_id);

        setEvent();

    }

    private void setEvent() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        setAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, setAccountActivity.class);
                startActivity(intent);
            }
        });

        setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, setProfileActivity.class);
                startActivity(intent);
            }
        });

        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                startActivity(intent);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setMessage("로그아웃 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {

                                    // 로그아웃

                                    // kakao Logout
                                    UserManagement.requestLogout(new LogoutResponseCallback() {
                                        @Override
                                        public void onCompleteLogout() {
                                            Log.d("kakao", " logout");

                                            // sharedpreferences
                                            SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(SettingActivity.this);
                                            prefereneUtil.removeSharedPreferences(user_id);

                                            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                                    // facebook Logout
                                    LoginManager.getInstance().logOut();

//                                    // sharedpreferences
//                                    SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(SettingActivity.this);
//                                    prefereneUtil.removeSharedPreferences(Award.user_id);
//                                    Log.d("로그아웃", "성공");


                                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } catch (Exception e) {
                                    Log.d("로그아웃", "실패");
                                }

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.setTitle("AWARD");
                alert.setIcon(R.drawable.logo);
                alert.show();
            }
        });
    }

    private void initView() {

        btnCancel = (Button)findViewById(R.id.btnCancel);
        setAccount = (TextView)findViewById(R.id.setAccount);
        setProfile = (TextView)findViewById(R.id.setProfile);
        ask = (TextView)findViewById(R.id.ask);
        info = (TextView)findViewById(R.id.info);
        logout = (TextView)findViewById(R.id.logout);
    }
}
