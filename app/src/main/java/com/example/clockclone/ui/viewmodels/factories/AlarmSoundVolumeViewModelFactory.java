package com.example.clockclone.ui.viewmodels.factories;

import com.example.clockclone.domain.ui.AlarmSoundUI;
import com.example.clockclone.domain.ui.SoundVolumeUI;
import com.example.clockclone.ui.viewmodels.AlarmSoundVolumeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AlarmSoundVolumeViewModelFactory implements ViewModelProvider.Factory {

    private SoundVolumeUI soundVolumeUI;
    private List<AlarmSoundUI> alarmSounds;

    public AlarmSoundVolumeViewModelFactory(SoundVolumeUI soundVolumeUI, List<AlarmSoundUI> alarmSounds) {
        this.soundVolumeUI = soundVolumeUI;
        this.alarmSounds = alarmSounds;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new AlarmSoundVolumeViewModel(soundVolumeUI, alarmSounds);
    }
}
