package com.robotemi.BrokenRecord.App;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.robotemi.BrokenRecord.Entity.Multimedia;
import com.robotemi.BrokenRecord.Entity.TimeSlot;
import com.robotemi.BrokenRecord.Entity.YouTubeVideo;
import com.robotemi.BrokenRecord.Interface.MainActivityInterface;
import com.robotemi.sdk.BrokenRecord.R;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.permission.Permission;

import org.jetbrains.annotations.NotNull;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements OnRobotReadyListener,
        OnGoToLocationStatusChangedListener,
        Robot.TtsListener, MainActivityInterface {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static final String PREVIOUSVOLUMEBEFOREPLAYINGVIDEO = "previousVolumeBeforePlayingVideo";
    public static final String CURRENTTIMESLOT = "currentTimeSlot";


    private Robot robot;
    private Thread sequenceThread;
    private TimeSlot currentTimeSlot;
    private volatile boolean isSpeaking = false;
    private final String HOME_BASE = "home base";
    private int previousVolumeBeforePlayingVideo;


    /**
     * Setting up all the event listeners
     */
    @Override
    protected void onStart() {
        super.onStart();
        robot = Robot.getInstance();
        robot.addOnRobotReadyListener(this);
        robot.addTtsListener(this);
    }

    /**
     * Removing the event listeners upon leaving the app.
     */
    @Override
    protected void onStop() {
        super.onStop();
        robot.removeTtsListener(this);
        robot.removeOnRobotReadyListener(this);
        robot.removeTtsListener(this);
        robot.removeOnGoToLocationStatusChangedListener(this);

        sequenceThread = null;
        robot.stopMovement();
        if (robot.checkSelfPermission(Permission.FACE_RECOGNITION) == Permission.GRANTED) {
            robot.stopFaceRecognition();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        verifyStoragePermissions(this);
        // robot.requestPermissions(new ArrayList<>(Collections.singleton(SETTINGS)), Permission.GRANTED);

        //TODO: Volume adjusting not working
        robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.


        Intent intent = getIntent();
        if (intent.hasExtra("isFinished")) {

            Boolean isFinished = (Boolean) Objects.requireNonNull(intent.getExtras()).get("isFinished");
            if (isFinished) {
                System.out.println("under is finished  if block");
                currentTimeSlot = (TimeSlot) intent.getSerializableExtra(CURRENTTIMESLOT);

                afterVideosPlayed();
                robot.addOnRobotReadyListener(this);
                robot.addOnGoToLocationStatusChangedListener(this);
                robot.addTtsListener(this);
                robot.setVolume(intent.getIntExtra(PREVIOUSVOLUMEBEFOREPLAYINGVIDEO, 5));
                System.out.println("Volume after coming from Youtube Activity: " + robot.getVolume());
            }

        }
    }

    @Override
    protected void onDestroy() {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        super.onDestroy();
    }


    /**
     * Places this application in the top bar for a quick access shortcut.
     */
    @Override
    public void onRobotReady(boolean isReady) {
        if (isReady) {
            try {
                final ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
                // robot.onStart() method may change the visibility of top bar.
                robot.onStart(activityInfo);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }


    //For Ryan's own testing purposes
    public void onButtonAClick(View v) {
        robot.addOnGoToLocationStatusChangedListener(this);

//        Multimedia bruh = new Multimedia("2ZIpFytCSVc", "Brush sound effect 2",
////                MediaType.video, true);
//        Multimedia japan = new Multimedia("8EGliGWfuNI", "Japanese sound effect 2",
//                MediaType.video, true);
        YouTubeVideo bruh = new YouTubeVideo("Brush sound effect 2", "2ZIpFytCSVc");
        YouTubeVideo japan = new YouTubeVideo("Japan sound effect", "8EGliGWfuNI");

        HashMap<String, Multimedia[]> videosLocations = new HashMap<>();
        videosLocations.put("1", new YouTubeVideo[]{bruh, japan});
        videosLocations.put("2", new YouTubeVideo[]{bruh});

        currentTimeSlot = new TimeSlot(new GregorianCalendar(), "test", videosLocations);

        initialSequence();

    }

    public void onClickBedButton(View v) {
        robot.addOnGoToLocationStatusChangedListener(this);
        String bedId = (String) v.getResources().getResourceName(v.getId());
        bedId = bedId.split("/")[1];
        String bedNumber = bedId.replaceAll("[^\\d.]", "");
//        System.out.println(bedNumber);
//        robot.goTo(bedNumber);

//        Multimedia bruh = new Multimedia("2ZIpFytCSVc", "Brush sound effect 2",
////                MediaType.video, true);
////        Multimedia smile = new Multimedia("PUroX5MX3xk", "Smile Emoji",
////                MediaType.video, true);

        YouTubeVideo bruh = new YouTubeVideo("Brush sound effect 2", "2ZIpFytCSVc");
        YouTubeVideo smile = new YouTubeVideo("Smily emoji", "PUroX5MX3xk");
//
//
        HashMap<String, Multimedia[]> videosLocations = new HashMap<>();
        videosLocations.put(bedNumber, new Multimedia[]{bruh, smile});
        currentTimeSlot = new TimeSlot(new GregorianCalendar(), "test", videosLocations);

        initialSequence();

    }

    public void onInterruptButtonClicked(View v) {
        robot.stopMovement();
        if (sequenceThread.isAlive())
            sequenceThread.interrupt();
        robot.removeOnGoToLocationStatusChangedListener(this);
        TtsRequest request = TtsRequest.create("Interrupt button pressed, going back to home base ", true);
        robot.speak(request);
        robot.goTo(HOME_BASE);
    }

    public void initialSequence() {
        assert sequenceThread == null : "SequenceThead should be null";
        String text = "Starting timeSlot Session";
        System.out.println(text);
        // nextLocationPointer is already set in the TimeSlot Constructor

        sequenceThread = new Thread(() -> {
            TtsRequest ttsRequest = TtsRequest.create(text, true);
            robot.speak(ttsRequest);

            String locationName = findLocationWithPointerNumber(getCurrentTimeSlot(), getCurrentTimeSlot().getNextLocationPointer());
            robot.goTo(locationName);
        });
        sequenceThread.start();

    }

    @Override
    public void onGoToLocationStatusChanged(@NotNull String s, @NotNull String s1, int i, @NotNull String s2) {
        //  System.out.println("############# OnGoToLocationStatusChangedListener");
        if (!s1.equals(OnGoToLocationStatusChangedListener.COMPLETE)) {
            return;
        }
        if (sequenceThread.isAlive()) {
            sequenceThread.interrupt();
        }
        sequenceThread = new Thread(() -> {
            TtsRequest request = TtsRequest.create("Hi here are some educational videos to watch ", true);
            robot.speak(request);
            waitForTemiToFinishTts();
            this.previousVolumeBeforePlayingVideo = robot.getVolume();
            robot.setVolume(9);
            Intent intent = new Intent(getApplicationContext(), YoutubeActivity.class);
            intent.putExtra(CURRENTTIMESLOT, getCurrentTimeSlot());
            intent.putExtra(PREVIOUSVOLUMEBEFOREPLAYINGVIDEO, previousVolumeBeforePlayingVideo);
            startActivity(intent);
            sequenceThread.interrupt();
            sequenceThread = null;
        });
        sequenceThread.start();


    }

    public void afterVideosPlayed() {
        assert sequenceThread == null : "Sequence Thread is not null";
        sequenceThread = new Thread(() -> {
            int previousLocation = getCurrentTimeSlot().getNextLocationPointer();
            int nextLocationPointer = previousLocation + 1;
            if (nextLocationPointer < getCurrentTimeSlot().getLocationVideos().size()) {
                getCurrentTimeSlot().setNextLocationPointer(nextLocationPointer);
                String locationName = findLocationWithPointerNumber(getCurrentTimeSlot(), nextLocationPointer);
                System.out.println("next, I am going to location: " + locationName);
                String thankYouText = "Thank you for your time";
                TtsRequest ttsRequest = TtsRequest.create(thankYouText, true);
                robot.speak(ttsRequest);
                robot.goTo(locationName);
            } else {
                // nextLocationPointer == testTimeSlot.getLocations().size() means sequence is completed
                TtsRequest ttsRequest = TtsRequest.create("Sequence complete, going back to home base", true);
                robot.speak(ttsRequest);
                robot.goTo(HOME_BASE);
                robot.removeOnGoToLocationStatusChangedListener(this);
                sequenceThread.interrupt();
                sequenceThread = null;
            }

        });
        sequenceThread.start();
    }

    @Override
    public void onTtsStatusChanged(@NotNull TtsRequest ttsRequest) {
        if (ttsRequest.getStatus().equals(TtsRequest.Status.COMPLETED)) {
            isSpeaking = false;
        }
    }


    protected void waitForTemiToFinishTts() {
        isSpeaking = true; // start of speech
        while (isSpeaking) {

        }
    }


    public String findLocationWithPointerNumber(TimeSlot currentTimeSlot, int nextLocationPointer) {
        String locationName = "";
        int i = 0;
        for (String key : currentTimeSlot.getLocationVideos().keySet()) {
            if (i == nextLocationPointer) {
                locationName = key;
            }
            i++;
        }
        return locationName;
    }

    public TimeSlot getCurrentTimeSlot() {
        return currentTimeSlot;
    }
}