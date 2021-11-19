package com.example.clockclone.ui.viewmodels.factories;

import android.app.Application;
import android.net.Uri;

import com.example.clockclone.ui.viewmodels.AddEditAlarmViewModel;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AddEditAlarmViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private int alarmID;
    private int snoozeSettings;
    private int initialVolume;
    private String defaultSoundName;
    private Uri defaultSoundUri;
    private int vibrationSettings;
    private boolean sundayFirst;

    public AddEditAlarmViewModelFactory(Application application, int alarmID, int snoozeSettings, int initialVolume, String defaultSoundName, Uri defaultSoundUri, int vibrationSettings, boolean sundayFirst) {
        this.application = application;
        this.alarmID = alarmID;
        this.snoozeSettings = snoozeSettings;
        this.initialVolume = initialVolume;
        this.defaultSoundName = defaultSoundName;
        this.defaultSoundUri = defaultSoundUri;
        this.vibrationSettings = vibrationSettings;
        this.sundayFirst = sundayFirst;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new AddEditAlarmViewModel(application, alarmID, snoozeSettings, initialVolume, defaultSoundName, defaultSoundUri, vibrationSettings, sundayFirst);
    }
}
