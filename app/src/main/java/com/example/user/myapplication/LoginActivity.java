package com.example.user.myapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.myapplication.MyFeedAward.ViewPageDotView;
import com.example.user.myapplication.network.JSONParser;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends AppCompatActivity {

    SessionCallback callback; // kakao
    CallbackManager callbackManager; // facebook

    private Button btn_login_fb, btn_login_kakao;

    private long user_id_long;
    private String user_id;
    private String user_img_path;
    private String user_name_a;

    private Uri profileUri;

    private ViewPager pager;
    private ViewPageDotView viewPageDot;

    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    private static final Integer[] IMAGES= {R.drawable.login_1,R.drawable.login_2,R.drawable.login_3};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());//Facebook sdk 초기화
        callbackManager = CallbackManager.Factory.create();

        if (savedInstanceState == null)
            setContentView(R.layout.activity_login);
        else {
            finish();
            return;
        }

        initView();
        PagerView();
        setEvent();

        /**카카오톡 로그아웃 요청**/
        // 한번 로그인이 성공하면 세션 정보가 남아있어서 로그인창이 뜨지 않고 바로 onSuccess()메서드를 호출
        //  매번 로그아웃 요청을 수행하도록 코드
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                //로그아웃 성공 후
                Toast.makeText(LoginActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setEvent() {

        btn_login_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginFacebook();
            }
        });

        btn_login_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginKakao();
            }
        });
    }

    /** 카카오톡 로그인 **/
    private void isLoginKakao() {

        //카카오톡 세션 오픈
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
//        Session.getCurrentSession().checkAndImplicitOpen();
//        Session.getCurrentSession().open(AuthType.KAKAO_TALK,LoginActivity.this);
        Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN,LoginActivity.this);

    }

    /** 페이스북 로그인 **/
    private void isLoginFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {

                                    user_id = object.getString("id");
                                    user_img_path = "http://graph.facebook.com/" + user_id + "/picture?type=large";
                                    user_name_a = object.getString("name");

                                    new LoginAsync().execute(user_id);

                                    Log.d("LoginTest", "이름 : " + object.getString("name"));
                                    Log.d("LoginTest", "이메일 : " + object.getString("email"));
                                    Log.d("LoginTest", "사용자 id : " + object.getString("id"));
                                    Log.d("LoginTest", "사용자 프로필 : " + user_img_path);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d("login_test","페이스북 로그인 취소");
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });
    }

    private void initView() {

        btn_login_fb = (Button)findViewById(R.id.btn_login_fb);
        btn_login_kakao = (Button)findViewById(R.id.btn_login_kakao);
        viewPageDot = (ViewPageDotView) findViewById(R.id.dot_view);
        pager = (ViewPager)findViewById(R.id.pager);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void PagerView() {

        viewPageDot.setNumOfCircles(IMAGES.length, this.getResources().getDimensionPixelSize(R.dimen.height_very_small));

        for(int i=0;i<IMAGES.length;i++)
            ImagesArray.add(IMAGES[i]);

        pager.setAdapter(new pagerImgAdapter(LoginActivity.this,ImagesArray));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPageDot.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                viewPageDot.onPageSelected(position);
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                viewPageDot.onPageScrollStateChanged(state);
            }
        });

       //  Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                pager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

    }

    public class pagerImgAdapter extends PagerAdapter {

        private ArrayList<Integer> IMAGES;
        private LayoutInflater inflater;
        private Context context;

        public pagerImgAdapter(Context context,ArrayList<Integer> IMAGES) {
            this.context = context;
            this.IMAGES=IMAGES;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGES.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.pager_item, view, false);

            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.img_pager);

            imageView.setImageResource(IMAGES.get(position));

            view.addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }




    /**
     * 카카오톡 로그인
     **/
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("login_test","카카오톡 세션 오픈");
            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.d("login_test","카카오 로그인 실패");
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    // 로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴
                    // 사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                    user_id_long = userProfile.getId();
                    user_img_path = userProfile.getProfileImagePath();
                    user_name_a = userProfile.getNickname();

                    user_id = Long.toString(user_id_long); // long -> string
                    new LoginAsync().execute(user_id);

                    Log.i("UserProfile", userProfile.toString());
                    Log.d("LoginTest", "사용자 일련번호 : " + user_id);
                    Log.d("LoginTest", "프로필 경로 : " + user_img_path);
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            if(exception != null){
                Log.d("login_test",exception.getMessage());
            }
        }
    }

    /**
     * AsyncTask
     **/
    class LoginAsync extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        private static final String LOGIN_URL = Award.AWARD_URL + "Award_server/Award/login_user.jsp";

        Dialog loadingDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Loading...");
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", args[0]);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute (JSONObject jsonObject){
            loadingDialog.dismiss();

            if(new SharedPrefereneUtil(getApplicationContext()).getLoginchk()){

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }else{
                Intent intent = new Intent(LoginActivity.this,NameSetActivity.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("user_img_path",user_img_path);
                intent.putExtra("user_name_a", user_name_a);
                startActivity(intent);
            }


            LoginActivity.this.finish();
        }
    }
}



