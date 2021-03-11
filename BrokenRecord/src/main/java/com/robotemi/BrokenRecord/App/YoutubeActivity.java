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
import java.util.List;

public class YoutubeActivity extends YouTubeBaseActivity {
    private static final String ISFINISHED = "isFinished";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        TimeSlot currentTimeSlot = (TimeSlot) getIntent().getSerializableExtra("currentTimeSlot");
        int volume = getIntent().getIntExtra(MainActivity.PREVIOUSVOLUMEBEFOREPLAYINGVIDEO, 5);
        System.out.println("@YoutubeActivity onCreate() Volume received: "+volume);
        String location = currentTimeSlot.getLocations().get(currentTimeSlot.getNextLocationPointer());
        System.out.println("Location: "+location);


        final ArrayList<Multimedia> medias = currentTimeSlot.getLocationVideos().get(location);
        System.out.println("List of media at this location");
        for(Multimedia media: medias){
            System.out.println(media.getName());
        }
        final int mediasLength = medias.size();

        YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                List<String> videoIds = new ArrayList<>();
                for (Multimedia media : medias) {
                    videoIds.add(media.getMultiMediaLink());
                }
                youTubePlayer.loadVideos(videoIds);
                youTubePlayer.setFullscreen(true);
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    private int j = 0;
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
                        j++;
                        if (j >= mediasLength) {
                            youTubePlayer.release();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra(MainActivity.CURRENTTIMESLOT, currentTimeSlot);
                            intent.putExtra(ISFINISHED, true);
                            intent.putExtra(MainActivity.PREVIOUSVOLUMEBEFOREPLAYINGVIDEO,volume);

                            startActivity(intent);

                            finish();

                        } else {
                            youTubePlayer.loadVideo(medias.get(j).getMultiMediaLink());
                            youTubePlayer.play();

                        }

                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        System.err.println(errorReason.toString());
                    }
                });

                youTubePlayer.loadVideo(medias.get(0).getMultiMediaLink());
                youTubePlayer.play();

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize(KeyForYoutube.API_KEY, onInitializedListener);

    }
}