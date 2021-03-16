
package com.robotemi.BrokenRecord.Interface;

import android.view.View;

import com.robotemi.BrokenRecord.Entity.TimeSlot;

public interface MainActivityInterface {
    void onButtonAClick(View view);

    void onClickBedButton(View view);

    void onInterruptButtonClicked(View v);

    TimeSlot getCurrentTimeSlot();
     String findLocationWithPointerNumber(TimeSlot currentTimeSlot, int nextLocationPointer);
}