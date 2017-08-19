package com.example.user.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                if(networkInfo != null){

                    if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                            && networkInfo.isConnectedOrConnecting()){
                        // network connect

                        try {

                            // 최초 실행 시 -> login activity / 자동 로그인 -> main activity
                            SharedPrefereneUtil prefereneUtil = new SharedPrefereneUtil(SplashActivity.this);

//                            if(prefereneUtil.getLoginchk("login_chk",true)){
//
//                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                startActivity(intent);
//
//                            }else{

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);

//                            }

                        }catch (Exception e){

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);

                        }

                    }

                    else{
                        // offline
                        Toast.makeText(getApplicationContext(),"네트워크 연결을 확인해주세요.",Toast.LENGTH_LONG).show();

                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"네트워크 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                }


            }
        }, 3000);

    }
}
