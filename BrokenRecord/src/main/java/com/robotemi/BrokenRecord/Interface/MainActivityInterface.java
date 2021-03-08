package com.robotemi.BrokenRecord.Interface;

import android.view.View;

public interface MainActivityInterface {
    void onButtonAClick(View view);
    void onInterruptButtonClicked(View v);
    void waitForTemiToFinishTts();
    boolean isSpeaking();
    void setSpeaking (boolean speaking);
}