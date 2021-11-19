package com.example.clockclone.ui.viewmodels;

import com.example.clockclone.domain.ui.AlarmSoundUI;
import com.example.clockclone.domain.ui.SoundVolumeUI;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlarmSoundVolumeViewModel extends ViewModel {

    private SoundVolumeUI soundVolumeUI;
    private MutableLiveData<Integer> _volumeLiveData;
    private LiveData<Integer> volumeLiveData;
    private List<AlarmSoundUI> alarmSounds;

    public AlarmSoundVolumeViewModel(SoundVolumeUI soundVolumeUI, List<AlarmSoundUI> alarmSounds) {
        this.soundVolumeUI = soundVolumeUI;
        this.alarmSounds = alarmSounds;

        _volumeLiveData = new MutableLiveData<>(soundVolumeUI.getVolume());
        volumeLiveData = _volumeLiveData;
    }


    public SoundVolumeUI getSoundVolumeUI() {
        return soundVolumeUI;
    }

    public List<AlarmSoundUI> getAlarmSounds() {
        return alarmSounds;
    }

    public void setVolume(int volume, boolean programmatically) {
        soundVolumeUI.setVolume(volume);
        if(programmatically) {
            _volumeLiveData.postValue(soundVolumeUI.getVolume());
        }
    }

    public void setSelectedAlarmSound(AlarmSoundUI selectedAlarmSound) {
        this.soundVolumeUI.setSelectedAlarmSound(selectedAlarmSound);
    }

    public int getVolume() {
        return soundVolumeUI.getVolume();
    }

    public AlarmSoundUI getSelectedAlarmSound() {
        return soundVolumeUI.getSelectedAlarmSound();
    }

    public LiveData<Integer> getVolumeLiveData() {
        return volumeLiveData;
    }
}
