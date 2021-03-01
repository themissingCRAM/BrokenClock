package com.robotemi.sdk.BrokenRecord;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;

import com.robotemi.sdk.BrokenRecord.R;
import com.robotemi.sdk.NlpResult;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.activitystream.ActivityStreamPublishMessage;
import com.robotemi.sdk.exception.OnSdkExceptionListener;
import com.robotemi.sdk.exception.SdkException;
import com.robotemi.sdk.face.ContactModel;
import com.robotemi.sdk.face.OnFaceRecognizedListener;
import com.robotemi.sdk.listeners.OnBeWithMeStatusChangedListener;
import com.robotemi.sdk.listeners.OnConstraintBeWithStatusChangedListener;
import com.robotemi.sdk.listeners.OnConversationStatusChangedListener;
import com.robotemi.sdk.listeners.OnDetectionDataChangedListener;
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener;
import com.robotemi.sdk.listeners.OnDisabledFeatureListUpdatedListener;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.listeners.OnRobotLiftedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.listeners.OnTelepresenceEventChangedListener;
import com.robotemi.sdk.listeners.OnTtsVisualizerFftDataChangedListener;
import com.robotemi.sdk.listeners.OnTtsVisualizerWaveFormDataChangedListener;
import com.robotemi.sdk.listeners.OnUserInteractionChangedListener;
import com.robotemi.sdk.map.OnLoadMapStatusChangedListener;
import com.robotemi.sdk.model.CallEventModel;
import com.robotemi.sdk.model.DetectionData;
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener;
import com.robotemi.sdk.navigation.listener.OnDistanceToLocationChangedListener;
import com.robotemi.sdk.navigation.listener.OnReposeStatusChangedListener;
import com.robotemi.sdk.navigation.model.Position;
import com.robotemi.sdk.permission.OnRequestPermissionResultListener;
import com.robotemi.sdk.permission.Permission;
import com.robotemi.sdk.sequence.OnSequencePlayStatusChangedListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.robotemi.sdk.map.z


public class MainActivity extends AppCompatActivity implements
        Robot.NlpListener,
        OnRobotReadyListener,
        Robot.ConversationViewAttachesListener,
        Robot.WakeupWordListener,
        Robot.ActivityStreamPublishListener,
        Robot.TtsListener,
        OnBeWithMeStatusChangedListener,
        OnGoToLocationStatusChangedListener,
        OnLocationsUpdatedListener,
        OnConstraintBeWithStatusChangedListener,
        OnDetectionStateChangedListener,
        Robot.AsrListener,
        OnTelepresenceEventChangedListener,
        OnRequestPermissionResultListener,
        OnDistanceToLocationChangedListener,
        OnCurrentPositionChangedListener,
        OnSequencePlayStatusChangedListener,
        OnRobotLiftedListener,
        OnDetectionDataChangedListener,
        OnUserInteractionChangedListener,
        OnFaceRecognizedListener,
        OnConversationStatusChangedListener,
        OnTtsVisualizerWaveFormDataChangedListener,
        OnTtsVisualizerFftDataChangedListener,
        OnReposeStatusChangedListener,
        OnLoadMapStatusChangedListener,
        OnDisabledFeatureListUpdatedListener,
        OnSdkExceptionListener {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private EditText etSpeak, etSaveLocation, etGoTo, etDistance, etX, etY, etYaw, etNlu;

    private List<String> locations;

    private Robot robot;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
        robot.addNlpListener(this);
        robot.addOnBeWithMeStatusChangedListener(this);
        robot.addOnGoToLocationStatusChangedListener(this);
        robot.addConversationViewAttachesListenerListener(this);
        robot.addWakeupWordListener(this);
        robot.addTtsListener(this);
        robot.addOnLocationsUpdatedListener(this);
        robot.addOnConstraintBeWithStatusChangedListener(this);
        robot.addOnDetectionStateChangedListener(this);
        robot.addAsrListener(this);
        robot.addOnDistanceToLocationChangedListener(this);
        robot.addOnCurrentPositionChangedListener(this);
        robot.addOnSequencePlayStatusChangedListener(this);
        robot.addOnRobotLiftedListener(this);
        robot.addOnDetectionDataChangedListener(this);
        robot.addOnUserInteractionChangedListener(this);
        robot.addOnConversationStatusChangedListener(this);
        robot.addOnTtsVisualizerWaveFormDataChangedListener(this);
        robot.addOnTtsVisualizerFftDataChangedListener(this);
        robot.addOnReposeStatusChangedListener(this);
        robot.showTopBar();
    }

    /**
     * Removing the event listeners upon leaving the app.
     */
    @Override
    protected void onStop() {
        super.onStop();
        robot.removeOnRobotReadyListener(this);
        robot.removeNlpListener(this);
        robot.removeOnBeWithMeStatusChangedListener(this);
        robot.removeOnGoToLocationStatusChangedListener(this);
        robot.removeConversationViewAttachesListenerListener(this);
        robot.removeWakeupWordListener(this);
        robot.removeTtsListener(this);
        robot.removeOnLocationsUpdateListener(this);
        robot.removeOnDetectionStateChangedListener(this);
        robot.removeAsrListener(this);
        robot.removeOnDistanceToLocationChangedListener(this);
        robot.removeOnCurrentPositionChangedListener(this);
        robot.removeOnSequencePlayStatusChangedListener(this);
        robot.removeOnRobotLiftedListener(this);
        robot.removeOnDetectionDataChangedListener(this);
        robot.addOnUserInteractionChangedListener(this);
        robot.stopMovement();
        if (robot.checkSelfPermission(Permission.FACE_RECOGNITION) == Permission.GRANTED) {
            robot.stopFaceRecognition();
        }
        robot.removeOnConversationStatusChangedListener(this);
        robot.removeOnTtsVisualizerWaveFormDataChangedListener(this);
        robot.removeOnTtsVisualizerFftDataChangedListener(this);
        robot.removeOnReposeStatusChangedListener(this);
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
        initViews();
        verifyStoragePermissions(this);
        robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.
        robot.addOnRequestPermissionResultListener(this);
        robot.addOnTelepresenceEventChangedListener(this);
        robot.addOnFaceRecognizedListener(this);
        robot.addOnLoadMapStatusChangedListener(this);
        robot.addOnDisabledFeatureListUpdatedListener(this);
        robot.addOnSdkExceptionListener(this);
    }

    @Override
    protected void onDestroy() {
        robot.removeOnRequestPermissionResultListener(this);
        robot.removeOnTelepresenceEventChangedListener(this);
        robot.removeOnFaceRecognizedListener(this);
        robot.removeOnSdkExceptionListener(this);
        robot.removeOnLoadMapStatusChangedListener(this);
        robot.removeOnDisabledFeatureListUpdatedListener(this);
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        super.onDestroy();
    }

    public void initViews() {
//        etSpeak = findViewById(R.id.etSpeak);
//        etSaveLocation = findViewById(R.id.etSaveLocation);
//        etGoTo = findViewById(R.id.etGoTo);
//        etDistance = findViewById(R.id.etDistance);
//        tvLog = findViewById(R.id.tvLog);
//        tvLog.setMovementMethod(new ScrollingMovementMethod());
//        etX = findViewById(R.id.etX);
//        etY = findViewById(R.id.etY);
//        etYaw = findViewById(R.id.etYaw);
//        etNlu = findViewById(R.id.etNlu);
//        ivFace = findViewById(R.id.imageViewFace);
   //     ttsVisualizerView = findViewById(R.id.visualizerView);
    }

    /**
     * Have the robot speak while displaying what is being said.
     */
    public void speak(View view) {
        TtsRequest ttsRequest = TtsRequest.create(etSpeak.getText().toString().trim(), true);
        robot.speak(ttsRequest);
        hideKeyboard();
    }






    public void onButtonAClick(View v){
        System.out.println("TESTXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        List<String> locations = robot.getLocations();
     //   MapDataModel map =  robot.getMapData();
        for(String location:locations){
            System.out.println(location);
        }

        robot.goTo("e");
      //  System.out.println("-----------------------------------");
       // System.out.println(map.getLocations());
    }


    @Override
    public void onPublish(@NotNull ActivityStreamPublishMessage activityStreamPublishMessage) {

    }

    @Override
    public void onAsrResult(@NotNull String s) {

    }

    @Override
    public void onConversationAttaches(boolean b) {

    }

    @Override
    public void onNlpCompleted(@NotNull NlpResult nlpResult) {

    }

    @Override
    public void onTtsStatusChanged(@NotNull TtsRequest ttsRequest) {

    }

    @Override
    public void onWakeupWord(@NotNull String s, int i) {

    }

    @Override
    public void onSdkError(@NotNull SdkException e) {

    }

    @Override
    public void onFaceRecognized(@NotNull List<ContactModel> list) {

    }

    @Override
    public void onBeWithMeStatusChanged(@NotNull String s) {

    }

    @Override
    public void onConstraintBeWithStatusChanged(boolean b) {

    }

    @Override
    public void onConversationStatusChanged(int i, @NotNull String s) {

    }

    @Override
    public void onDetectionDataChanged(@NotNull DetectionData detectionData) {

    }

    @Override
    public void onDetectionStateChanged(int i) {

    }

    @Override
    public void onDisabledFeatureListUpdated(@NotNull List<String> list) {

    }

    @Override
    public void onGoToLocationStatusChanged(@NotNull String s, @NotNull String s1, int i, @NotNull String s2) {

    }

    @Override
    public void onLocationsUpdated(@NotNull List<String> list) {

    }

    @Override
    public void onRobotLifted(boolean b, @NotNull String s) {

    }

    @Override
    public void onTelepresenceEventChanged(@NotNull CallEventModel callEventModel) {

    }

    @Override
    public void onTtsVisualizerFftDataChanged(@NotNull byte[] bytes) {

    }

    @Override
    public void onTtsVisualizerWaveFormDataChanged(@NotNull byte[] bytes) {

    }

    @Override
    public void onUserInteraction(boolean b) {

    }

    @Override
    public void onLoadMapStatusChanged(int i) {

    }

    @Override
    public void onCurrentPositionChanged(@NotNull Position position) {

    }

    @Override
    public void onDistanceToLocationChanged(@NotNull Map<String, Float> map) {

    }

    @Override
    public void onReposeStatusChanged(int i, @NotNull String s) {

    }

    @Override
    public void onRequestPermissionResult(@NotNull Permission permission, int i, int i1) {

    }

    @Override
    public void onSequencePlayStatusChanged(int i) {

    }

    /**
     * Called when pointer capture is enabled or disabled for the current window.
     *
     * @param hasCapture True if the window has pointer capture.
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}