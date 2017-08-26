package com.example.user.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                if(networkInfo != null){

                    if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                            && networkInfo.isConnectedOrConnecting()){
                        // network connect

                        try {

                         //    최초 실행 시 -> login activity / 자동 로그인 -> main activity

                            if(new SharedPrefereneUtil(getApplicationContext()).getLoginchk()){

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                            }else{

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                              }

                        }catch (Exception e){

                            Log.d("error",e.toString());

                        }

                    }

                    else{
                        // offline
                        Toast.makeText(getApplicationContext(),R.string.network_chk,Toast.LENGTH_LONG).show();

                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),R.string.network_chk,Toast.LENGTH_LONG).show();
                }

                finish();


            }
        }, 3000);

    }
}
