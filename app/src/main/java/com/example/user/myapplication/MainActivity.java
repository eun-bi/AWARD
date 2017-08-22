package com.example.user.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.user.myapplication.MakeAward.MakeAwardActivity1;
import com.example.user.myapplication.MakeAward.MakeAwardActivity3;
import com.example.user.myapplication.MyFeedAward.MyFeedAwardFragment;
import com.example.user.myapplication.MyPage.MyPageFragment;

public class MainActivity extends AppCompatActivity {

    private TabLayout main_bottom_tabLayout;

    private boolean mFlag = false;

    MyFeedAwardFragment fragment_MyFeedAward = new MyFeedAwardFragment();
    MyPageFragment fragment_mypage = new MyPageFragment();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_fragment, fragment_MyFeedAward);
            transaction.commit();
        }

        initView();
        setEvent();

    }

    private void initView() {
        main_bottom_tabLayout = (TabLayout) findViewById(R.id.main_bottom_tabLayout);
    }

    private void setEvent() {
        main_bottom_tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            int pre = 0;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeFragment(tab.getPosition(), pre);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                /* 시상하기 눌렀을 시 다른 탭 아이템 선택해제 되는 것을 방지하기 위해 이전에 선택되어 있던 탭 position 기억 */
                pre = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    //두번 눌렀을 시 scroll 가장 상단으로 이동
                    MyFeedAwardFragment.moveScroll();
                }
                else if(tab.getPosition() == 1) {
                    /*
                        시상을 했다가 취소하고 다시 돌아온 경우
                        그 탭이 이미 눌려져 있는 것으로 인지하여
                        시상하기가 안되는 오류해결
                    */
                    Intent intent = new Intent(getApplicationContext(), MakeAwardActivity1.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void changeFragment(int position, int pre) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch(position) {
            /* Change fragment  */
            case 0:
                transaction.replace(R.id.content_fragment, fragment_MyFeedAward, "Feed");
                transaction.commit();
                break;

            case 1:
                Intent intent = new Intent(getApplicationContext(), MakeAwardActivity1.class);
                startActivity(intent);
                main_bottom_tabLayout.getTabAt(pre).select();
                break;

            case 2:
                transaction.replace(R.id.content_fragment, fragment_mypage, "Profile");
                transaction.commit();
                break;
        }
    }

    /* 액티비티 종료시 더블 더블터치 필요 */
    @Override
    public boolean onKeyDown ( int KeyCode, KeyEvent event )
    {
        if ( event.getAction() == KeyEvent.ACTION_DOWN )
        {
            int depth = getSupportFragmentManager().getBackStackEntryCount();
            if( KeyCode == KeyEvent.KEYCODE_BACK && depth == 0)
            {
                /* 뒤로 버튼을 눌렀을때 해야할 행동 */
                if(!mFlag)
                {
                    Toast.makeText(MainActivity.this, "'뒤로' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                    mFlag = true;
                    mKillHandler.sendEmptyMessageDelayed(0, 2000);
                    return false;
                }
                else
                {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }

        }

        return super.onKeyDown(KeyCode, event);
    }

    /* 종료버튼이 한번 더 눌리지 않으면 mFlag 값 복원한다 */
    Handler mKillHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 0)
            {
                mFlag = false;
            }
        }
    };




}
