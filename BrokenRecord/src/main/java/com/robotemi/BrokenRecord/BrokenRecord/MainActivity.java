package com.robotemi.BrokenRecord.BrokenRecord;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.robotemi.BrokenRecord.Entity.TimeSlot;
import com.robotemi.sdk.BrokenRecord.R;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.permission.Permission;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements OnRobotReadyListener,
        OnGoToLocationStatusChangedListener,
        Robot.TtsListener {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Robot robot;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Thread sequenceThread;
    private TimeSlot testTimeSlot;
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
       // robot.addOnGoToLocationStatusChangedListener(this);
        robot.addTtsListener(this);
//        robot.showTopBar();
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
        //robot.removeOnGoToLocationStatusChangedListener(this);

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
     * Hiding keyboard after every button press
     */

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

    public void onButtonAClick(View v) {
        if (sequenceThread == null) {
            robot.addOnGoToLocationStatusChangedListener(this);
            List<String> locations = new ArrayList<>(robot.getLocations());
            locations.remove(HOME_BASE);
            Collections.sort(locations);
            testTimeSlot = new TimeSlot(new GregorianCalendar(), new ArrayList<>(locations), new ArrayList<>(),"test");
            testTimeSlot.setCurrentLocationPointer(0);
            String text = "Firstly, I am going to location: " + testTimeSlot.getLocations().get(testTimeSlot.getCurrentLocationPointer());
            System.out.println(text);
            sequenceThread = new Thread(() -> {
                TtsRequest ttsRequest = TtsRequest.create(text, true);
                robot.speak(ttsRequest);
                robot.goTo(testTimeSlot.getLocations().get(testTimeSlot.getCurrentLocationPointer()));
            });
            sequenceThread.start();
        }
        assert sequenceThread != null : "This is a bug";
    }

    public void onInterruptButtonClicked(View v) {

        robot.stopMovement();
        sequenceThread.interrupt();
        robot.removeOnGoToLocationStatusChangedListener(this);
        TtsRequest request = TtsRequest.create("Interrupt button pressed, going back to home base ",true);
        robot.speak(request);

        robot.goTo(HOME_BASE);
    }

    @Override
    public void onGoToLocationStatusChanged(@NotNull String s, @NotNull String s1, int i, @NotNull String s2) {
//        System.out.println("Entered onGoTOLocationStatusChanged. Status:" + s1);
        if (!s1.equals(OnGoToLocationStatusChangedListener.COMPLETE))
            return;
        if(sequenceThread.isAlive())
            sequenceThread.interrupt();

        sequenceThread = new Thread(() -> {

            int previousLocation = testTimeSlot.getCurrentLocationPointer();
            TtsRequest request = TtsRequest.create("Hi here are some educational videos to watch " , true);
            robot.speak(request);
            waitForTemiToFinishTts();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            int currentLocation = previousLocation + 1;
            if (currentLocation < testTimeSlot.getLocations().size()) {
                testTimeSlot.setCurrentLocationPointer(currentLocation);
                String currentLocationName = testTimeSlot.getLocations().get(currentLocation);
                String text = "next, I am going to location: " + currentLocationName;
                System.out.println(text);
//                TtsRequest ttsRequest = TtsRequest.create(text, true);
//                robot.speak(ttsRequest);
                robot.goTo(currentLocationName);
            } else {
                testTimeSlot.setCurrentLocationPointer(TimeSlot.AT_THE_END);
                TtsRequest ttsRequest = TtsRequest.create("Sequence complete, going back to home base", true);
                robot.speak(ttsRequest);
                waitForTemiToFinishTts();
                robot.goTo(HOME_BASE);
                robot.removeOnGoToLocationStatusChangedListener(this);

            }
        });
        sequenceThread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTtsStatusChanged(@NotNull TtsRequest ttsRequest) {
        System.out.println("onTtStatusChange: "+ttsRequest.getStatus());
        if (ttsRequest.getStatus().equals(TtsRequest.Status.COMPLETED)) {
            isSpeaking = false;
        }

    }
    private void waitForTemiToFinishTts() {
        isSpeaking = true; // start of speech
        System.out.println("Start of Speech");

        while (isSpeaking) {
            System.out.println("speaking");

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }//onTtsStatusChanged will tell this method that speech has stopped.
        }
        System.out.println("End of Speech");
        //end of speech
    }


}