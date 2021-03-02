package com.robotemi.sdk.BrokenRecord;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.permission.Permission;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements

        OnRobotReadyListener {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private List<String> locations;

    private Robot robot;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Thread sequenceThread;

    /**
     * Hiding keyboard after every button press
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    /**
     * Setting up all the event listeners
     */
    @Override
    protected void onStart() {
        super.onStart();

        robot.addOnRobotReadyListener(this);
//        robot.showTopBar();
    }

    /**
     * Removing the event listeners upon leaving the app.
     */
    @Override
    protected void onStop() {
        super.onStop();
        robot.removeOnRobotReadyListener(this);

        robot.stopMovement();
        if (robot.checkSelfPermission(Permission.FACE_RECOGNITION) == Permission.GRANTED) {
            robot.stopFaceRecognition();
        }

    }

    /**
     * Places this application in the top bar for a quick access shortcut.
     */
    @Override
    public void onRobotReady(boolean isReady) {
        if (isReady) {
            try {
                final ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
                // Robot.getInstance().onStart() method may change the visibility of top bar.
                robot.onStart(activityInfo);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        verifyStoragePermissions(this);
//        robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.
//        robot.addOnRequestPermissionResultListener(this);
//        robot.addOnTelepresenceEventChangedListener(this);
//        robot.addOnFaceRecognizedListener(this);
//        robot.addOnLoadMapStatusChangedListener(this);
//        robot.addOnDisabledFeatureListUpdatedListener(this);
//        robot.addOnSdkExceptionListener(this);
    }

    @Override
    protected void onDestroy() {
//        robot.removeOnRequestPermissionResultListener(this);
//        robot.removeOnTelepresenceEventChangedListener(this);
//        robot.removeOnFaceRecognizedListener(this);
//        robot.removeOnSdkExceptionListener(this);
//        robot.removeOnLoadMapStatusChangedListener(this);
//        robot.removeOnDisabledFeatureListUpdatedListener(this);
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        super.onDestroy();
    }

    /**
     * Have the robot speak while displaying what is being said.
     */
    public void speak(View view) {
        TtsRequest ttsRequest = TtsRequest.create(etSpeak.getText().toString().trim(), true);
        robot.speak(ttsRequest);
        hideKeyboard();
    }

    public void onButtonAClick(View v) {
        if (sequenceThread == null) {
            sequenceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<String> locations = robot.getLocations();
                        for (String location : locations) {
                            String text = "going to location: " + location;
                            System.out.println(text);
                            TtsRequest ttsRequest = TtsRequest.create(text, true);
                            robot.speak(ttsRequest);
                            robot.goTo(location);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        TtsRequest ttsRequest = TtsRequest.create("Sequence Interrupted, returning to home base", true);
                        robot.speak(ttsRequest);
                        robot.goTo("home base");
                    }
                }
            });
            sequenceThread.start();
        } else {
            System.out.println("sequenceThread is not null, please fix this bug ");
        }
    }
    public void onInterruptButtonClicked(View v){
        sequenceThread.interrupt();

    }

}