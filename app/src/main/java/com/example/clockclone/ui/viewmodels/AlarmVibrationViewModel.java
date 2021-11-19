package com.example.clockclone.ui.viewmodels;

import com.example.clockclone.domain.ui.VibrationPatternUI;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlarmVibrationViewModel extends ViewModel {

    private int patternID;
    private boolean vibrationEnabled;

    public AlarmVibrationViewModel(int patternID, boolean vibrationEnabled) {
        this.patternID = patternID;
        this.vibrationEnabled = vibrationEnabled;
    }

    public void setPatternID(int patternID) {
        this.patternID = patternID;
    }

    public int getPatternID() {
        return patternID;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
    }
}
