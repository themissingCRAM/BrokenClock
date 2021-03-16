package com.robotemi.BrokenRecord.App;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.robotemi.BrokenRecord.Entity.YouTubeVideo;
import com.robotemi.BrokenRecord.Entity.TimeSlot;
import com.robotemi.BrokenRecord.GoogleAPIKey.KeyForYoutube;
import com.robotemi.BrokenRecord.Interface.MainActivityInterface;
import com.robotemi.sdk.BrokenRecord.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YoutubeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoutubeFragment extends YouTubePlayerFragment implements MainActivityInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    MainActivityInterface mainActivity;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public YoutubeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YoutubeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoutubeFragment newInstance(String param1, String param2) {
        YoutubeFragment fragment = new YoutubeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
//
//    private static final String ISFINISHED = "isFinished";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivityInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_youtube);

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) getView().findViewById(R.id.youtube_player);
        TimeSlot currentTimeSlot = getCurrentTimeSlot();
        //int volume = getIntent().getIntExtra(MainActivity.PREVIOUSVOLUMEBEFOREPLAYINGVIDEO, 5);
        //System.out.println("@YoutubeActivity onCreate() Volume received: " + volume);

        String locationName = findLocationWithPointerNumber(currentTimeSlot, currentTimeSlot.getNextLocationPointer());
        System.out.println("Location: " + locationName);
        final YouTubeVideo[] youTubeVideoIds = (YouTubeVideo[]) currentTimeSlot.getLocationVideos().get(locationName);
        System.out.println("List of media at this location");
        for (YouTubeVideo media : youTubeVideoIds) {
            System.out.println(media.getName());
        }
        final int mediasLength = youTubeVideoIds.length;

        YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                List<String> videoIds = new ArrayList<>();
                for (YouTubeVideo media : youTubeVideoIds) {
                    videoIds.add(media.getVideoId());
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
//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            intent.putExtra(MainActivity.CURRENTTIMESLOT, currentTimeSlot);
//                            intent.putExtra(ISFINISHED, true);
////                            intent.putExtra(MainActivity.PREVIOUSVOLUMEBEFOREPLAYINGVIDEO, volume);
//                            startActivity(intent);
//                            finish();

                        } else {
                            youTubePlayer.loadVideo(youTubeVideoIds[j].getVideoId());
                            youTubePlayer.play();

                        }

                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        System.err.println(errorReason.toString());
                    }
                });

                youTubePlayer.loadVideo(youTubeVideoIds[0].getVideoId());
                youTubePlayer.play();

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize(KeyForYoutube.API_KEY, onInitializedListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_youtube, container, false);
    }

    @Override
    public void onButtonAClick(View view) {

    }

    @Override
    public void onClickBedButton(View view) {

    }

    @Override
    public void onInterruptButtonClicked(View v) {

    }

    @Override
    public TimeSlot getCurrentTimeSlot() {
        return mainActivity.getCurrentTimeSlot();
    }

    @Override
    public String findLocationWithPointerNumber(TimeSlot currentTimeSlot, int nextLocationPointer) {
       return mainActivity.findLocationWithPointerNumber(currentTimeSlot,nextLocationPointer);
    }
}