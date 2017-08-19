package com.example.user.myapplication.AwardResult;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoPlayActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;
    final String serverKey = "AIzaSyBdxc9tEbnWZxNcBtH3iW4V4MS37nyxH7E";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeplayer);
        youTubePlayerView.initialize(serverKey,this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer Player, boolean b) {

        youTubePlayer = Player;
        Intent gt = getIntent();
        String id = gt.getStringExtra("id");

        if(!b){
            if(id.startsWith("PL")){ // playlist 일 경우 첫 시작 단어가 PL
                youTubePlayer.loadPlaylist(id);
            }else{ // 아닐 경우 비디오
                youTubePlayer.loadVideo(id);
            }

        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Initialization Fail", Toast.LENGTH_LONG).show();
    }
}
