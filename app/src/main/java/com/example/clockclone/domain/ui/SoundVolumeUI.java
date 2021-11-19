package com.example.clockclone.domain.ui;

import androidx.databinding.ObservableInt;

public class SoundVolumeUI {
    private AlarmSoundUI selectedAlarmSound;
    private int volume;

    public SoundVolumeUI(AlarmSoundUI selectedAlarmSound, int volume) {
        this.selectedAlarmSound = selectedAlarmSound;
        this.volume = volume;
    }

    public AlarmSoundUI getSelectedAlarmSound() {
        return selectedAlarmSound;
    }

    public void setSelectedAlarmSound(AlarmSoundUI selectedAlarmSound) {
        this.selectedAlarmSound = selectedAlarmSound;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
