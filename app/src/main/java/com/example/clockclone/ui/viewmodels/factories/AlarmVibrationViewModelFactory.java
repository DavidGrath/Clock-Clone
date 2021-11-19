package com.example.clockclone.ui.viewmodels.factories;

import com.example.clockclone.ui.viewmodels.AlarmVibrationViewModel;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AlarmVibrationViewModelFactory implements ViewModelProvider.Factory {
    private int patternID;
    private boolean vibrationEnabled;

    public AlarmVibrationViewModelFactory(int patternID, boolean vibrationEnabled) {
        this.patternID = patternID;
        this.vibrationEnabled = vibrationEnabled;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new AlarmVibrationViewModel(patternID, vibrationEnabled);
    }
}
