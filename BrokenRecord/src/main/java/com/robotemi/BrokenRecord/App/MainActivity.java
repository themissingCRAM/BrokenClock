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
import com.robotemi.BrokenRecord.Enumeration.MediaType;
import com.robotemi.BrokenRecord.GoogleAPIKey.KeyForYoutube;
import com.robotemi.BrokenRecord.Interface.MainActivityInterface;
import com.robotemi.sdk.BrokenRecord.R;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.permission.Permission;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements OnRobotReadyListener,
        OnGoToLocationStatusChangedListener,
        Robot.TtsListener,
        MainActivityInterface {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Robot robot;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Thread sequenceThread;
    private TimeSlot currentTimeSlot;
    private boolean isSpeaking = false;
    private final String HOME_BASE = "home base";

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
        robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.


        Intent intent = getIntent();
        if (intent.hasExtra("isFinished")) {
            Boolean isFinished = (Boolean) intent.getExtras().get("isFinished");
            if (isFinished) {
                System.out.println("under is finished  if block");
                currentTimeSlot = (TimeSlot) intent.getSerializableExtra("currentTimeSlot");
                afterVideosPlayed();
                robot.addOnRobotReadyListener(this);
                robot.addOnGoToLocationStatusChangedListener(this);
                robot.addTtsListener(this);
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

    //
//    /**
//     * Have the robot speak while displaying what is being said.
//     */
//    public void speak(View view) {
////        TtsRequest ttsRequest = TtsRequest.create(etSpeak.getText().toString().trim(), true);
////        robot.speak(ttsRequest);
//        hideKeyboard();
//    }


    //For Ryan's own testing purposes
    public void onButtonAClick(View v) {
        if (sequenceThread == null) {
            robot.addOnGoToLocationStatusChangedListener(this);
            List<String> locations = new ArrayList<>(robot.getLocations());
            locations.remove(HOME_BASE);
            Collections.sort(locations);
            locations.add(0, HOME_BASE);

            Multimedia bruh = new Multimedia("2ZIpFytCSVc", "Brush sound effect 2",
                    MediaType.video, true);
            ArrayList<Multimedia> links = new ArrayList<>();
            links.add(bruh);
            currentTimeSlot = new TimeSlot(new GregorianCalendar(), new ArrayList<>(locations), links, "test");

            initialSequence();

        }
        assert sequenceThread != null : "This is a bug";
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
        String text = "Starting timeSlot Session";
        System.out.println(text);
        // nextLocationPointer is already set in the TimeSlot Constructor
        int nextLocationPointer = 1;
        sequenceThread = new Thread(() -> {
            TtsRequest ttsRequest = TtsRequest.create(text, true);
            robot.speak(ttsRequest);
            robot.goTo(currentTimeSlot.getLocations().get(nextLocationPointer));
        });
        sequenceThread.start();

    }

    @Override
    public void onGoToLocationStatusChanged(@NotNull String s, @NotNull String s1, int i, @NotNull String s2) {
System.out.println("############# OnGoToLocationStatusChangedListener");
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

            Intent intent = new Intent(getApplicationContext(), YoutubeActivity.class);
            intent.putExtra("currentTimeSlot", currentTimeSlot);
            startActivity(intent);
            sequenceThread.interrupt();
            sequenceThread = null;
        });
        sequenceThread.start();


    }

    public void afterVideosPlayed() {
        assert sequenceThread == null : "Sequence Thread is not null";


        sequenceThread = new Thread(() -> {
            int previousLocation = currentTimeSlot.getNextLocationPointer();
            int nextLocationPointer = previousLocation + 1;
            if (nextLocationPointer < currentTimeSlot.getLocations().size()) {
                currentTimeSlot.setNextLocationPointer(nextLocationPointer);
                String currentLocationName = currentTimeSlot.getLocations().get(nextLocationPointer);
                System.out.println("next, I am going to location: " + currentLocationName);
                String thankYouText = "Thank you for your time";
                TtsRequest ttsRequest = TtsRequest.create(thankYouText, true);
                robot.speak(ttsRequest);
                robot.goTo(currentLocationName);
            } else {
                // nextLocationPointer == testTimeSlot.getLocations().size() means sequence is completed
                TtsRequest ttsRequest = TtsRequest.create("Sequence complete, going back to home base", true);
                robot.speak(ttsRequest);
                waitForTemiToFinishTts();
                robot.goTo(HOME_BASE);
                robot.removeOnGoToLocationStatusChangedListener(this);
                sequenceThread.interrupt();
                sequenceThread = null;
            }
            try {
                Thread.sleep(500);
            } catch (
                    InterruptedException e) {
                e.printStackTrace();
            }
        });
        sequenceThread.start();
    }

    @Override
    public void onTtsStatusChanged(@NotNull TtsRequest ttsRequest) {
        System.out.println("onTtStatusChange: " + ttsRequest.getStatus());
        if (ttsRequest.getStatus().equals(TtsRequest.Status.COMPLETED)) {
            setSpeaking(false);
        }
    }


    public void waitForTemiToFinishTts() {
        setSpeaking(true); // start of speech
        System.out.println("In the middle of a speech");
        while (isSpeaking) {
        }
        System.out.println("End of Speech");
    }


    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void setSpeaking(boolean speaking) {
        isSpeaking = speaking;
    }
}