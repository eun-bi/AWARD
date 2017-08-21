package com.example.user.myapplication.Setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.user.myapplication.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InfoActivity extends AppCompatActivity {

    private TextView txtPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        txtPrivacy = (TextView)findViewById(R.id.txtPrivacy);

        /* raw 파일에 있는 '개인정보 처리방침' 파일 읽어와서 보여주기*/

        try{
            InputStream inputStream = getResources().openRawResource(R.raw.privacy);

            if(inputStream!=null){
                InputStreamReader streamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(streamReader);

                String read;
                StringBuilder sb = new StringBuilder("");

                while ((read=bufferedReader.readLine())!=null){
                    sb.append(read);
                }
                inputStream.close();

                txtPrivacy.setText(sb.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
