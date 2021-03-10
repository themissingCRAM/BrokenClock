package com.robotemi.BrokenRecord.App;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.robotemi.BrokenRecord.Entity.Multimedia;
import com.robotemi.BrokenRecord.Entity.TimeSlot;
import com.robotemi.BrokenRecord.GoogleAPIKey.KeyForYoutube;
import com.robotemi.sdk.BrokenRecord.R;

import java.util.ArrayList;

public class YoutubeActivity extends YouTubeBaseActivity {

    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);


        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        Intent intent = getIntent();
        TimeSlot currentTimeSlot = (TimeSlot) intent.getSerializableExtra("currentTimeSlot");
        Multimedia media = currentTimeSlot.getMultimediaLinksLinks().get(0);
        if (!media.isOnline()) {
            throw new IllegalStateException("multimedia should be online to play on youtube");
        }
        YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                setYouTubePlayer(youTubePlayer);
                getYouTubePlayer().setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {

                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {

                    }

                    @Override
                    public void onVideoEnded() {
                        System.out.println("Video has ended.");
                        youTubePlayer.release();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("currentTimeSlot", currentTimeSlot);
                        intent.putExtra("isFinished",true);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        System.err.println(errorReason.toString());
                    }
                });
                getYouTubePlayer().loadVideo(media.getMultiMediaLink());
                getYouTubePlayer().play();

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize(KeyForYoutube.API_KEY, onInitializedListener);

    }


//    public void playVideo(ArrayList<Multimedia> links){
//       // String bruhSoundEffect2 = "2ZIpFytCSVc";
//        for(Multimedia link: links) {
//
//        }
//    }

    public YouTubePlayer getYouTubePlayer() {
        return youTubePlayer;
    }

    public void setYouTubePlayer(YouTubePlayer youTubePlayer) {
        this.youTubePlayer = youTubePlayer;
    }

}