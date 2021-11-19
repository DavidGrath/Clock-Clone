package com.example.clockclone.ui.viewmodels;

import android.app.Application;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.clockclone.data.MainRepository;
import com.example.clockclone.domain.room.Alarm;
import com.example.clockclone.domain.ui.AlarmFormUI;
import com.example.clockclone.domain.ui.AlarmSoundUI;
import com.example.clockclone.domain.ui.SoundVolumeUI;
import com.example.clockclone.framework.ClockClone;
import com.example.clockclone.framework.db.AlarmDao;
import com.example.clockclone.framework.db.ClockCloneDatabase;
import com.example.clockclone.util.Constants;
import com.example.clockclone.util.ConverterUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class AddEditAlarmViewModel extends AndroidViewModel {

    private MainRepository mainRepository;
    private AlarmFormUI alarmFormUI;
    private BehaviorSubject<AlarmFormUI> alarmFormBehaviorSubject;

    private int alarmID;

    public AddEditAlarmViewModel(@NonNull @NotNull Application application, int alarmID, int snoozeSettings, int initialVolume, String defaultSoundName, Uri defaultSoundUri, int vibrationSettings, boolean sundayFirst) {
        super(application);
        ((ClockClone) application).daggerApplicationComponent.inject(this);
        this.alarmID = alarmID;
        alarmFormBehaviorSubject = BehaviorSubject.create();
        if (alarmID == -1) {
            int snoozeEnabledMask = snoozeSettings & Constants.Snooze.SNOOZE_MASK_ENABLED;
            boolean snoozeEnabled = (snoozeEnabledMask & Constants.Snooze.SNOOZE_FLAG_ENABLED) > 0;
            int snoozeInterval = snoozeSettings & Constants.Snooze.SNOOZE_MASK_INTERVAL;
            int snoozeRepeat = snoozeSettings & Constants.Snooze.SNOOZE_MASK_REPEAT;
            int vibrationEnabledMask = vibrationSettings & Constants.Vibrate.VIBRATION_MASK_FLAGS;
            boolean vibrationEnabled = (vibrationEnabledMask & Constants.Vibrate.VIBRATION_FLAG_ENABLED) > 0;
            int vibrationPattern = vibrationSettings & Constants.Vibrate.VIBRATION_MASK_PATTERN_ID;
            String vibrationName = Constants.Vibrate.vibrationPatternTitles[vibrationPattern];
            alarmFormUI = new AlarmFormUI(initialVolume, defaultSoundName, defaultSoundUri, vibrationEnabled, vibrationPattern, vibrationName, snoozeEnabled, snoozeInterval, snoozeRepeat, sundayFirst);
            alarmFormBehaviorSubject.onNext(alarmFormUI);
        } else {
            mainRepository.getAlarm(alarmID)
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess(alarm -> {
                        Ringtone ringtone = RingtoneManager.getRingtone(application, Uri.parse(alarm.soundUri));
                        String soundName = ringtone.getTitle(application);
                        alarmFormUI = ConverterUtils.alarmDBToAlarmFormUI(alarm, soundName, sundayFirst);
                        alarmFormBehaviorSubject.onNext(alarmFormUI);
                    }).subscribe();
        }
    }

    public LiveData<?> setAlarm() {
        Alarm alarm = ConverterUtils.alarmFormUIToAlarmDB(alarmID, alarmFormUI);
        if (alarmID == -1) {
            return LiveDataReactiveStreams.fromPublisher(mainRepository.addAlarm(alarm)
                    .subscribeOn(Schedulers.io()).toFlowable());
        } else {
            return LiveDataReactiveStreams.fromPublisher(mainRepository.updateAlarm(alarm)
                    .subscribeOn(Schedulers.io()).toFlowable());
        }
    }

    public LiveData<AlarmFormUI> getAlarmFormUI() {
        //TODO This is a workaround to prevent the queried DB entity from being returned. Please find
        // a suitable alternative
        if(alarmFormUI != null) {
            alarmFormBehaviorSubject.onNext(alarmFormUI);
        }
        return LiveDataReactiveStreams.fromPublisher(alarmFormBehaviorSubject.toFlowable(BackpressureStrategy.LATEST));
    }

    public void changeAlarmDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        alarmFormUI.setOccurrenceDate(calendar);
    }

    public void setAlarmName(@Nullable String newName) {
        alarmFormUI.setName(newName);
    }

    public String getAlarmName() {
        return alarmFormUI.getAlarmName();
    }

    public int getAlarmSnooze() {
        int snoozeEnabled = alarmFormUI.isSnooze() ? Constants.Snooze.SNOOZE_FLAG_ENABLED : 0;
        int snoozeInterval = alarmFormUI.getSnoozeInterval();
        int snoozeRepeat = alarmFormUI.getSnoozeRepeat();
        return snoozeEnabled | snoozeInterval | snoozeRepeat;
    }

    public void setAlarmSnooze(boolean enabled, int interval, int repeat) {
        alarmFormUI.setSnooze(enabled);
        alarmFormUI.setSnoozeInterval(interval);
        alarmFormUI.setSnoozeRepeat(repeat);
    }

    public SoundVolumeUI getAlarmSoundVolume() {
        AlarmSoundUI alarmSoundUI = new AlarmSoundUI(alarmFormUI.getSoundName(), alarmFormUI.getSoundUri());
        SoundVolumeUI soundVolumeUI = new SoundVolumeUI(alarmSoundUI, alarmFormUI.getVolume());
        return soundVolumeUI;
    }

    public void setAlarmSoundVolume(String soundName, Uri soundUri, int volume) {
        alarmFormUI.setSoundName(soundName);
        alarmFormUI.setSoundUri(soundUri);
        alarmFormUI.setVolume(volume);
    }

    public void setAlarmVibration(boolean enabled, int vibrationPattern) {
        alarmFormUI.setVibrationEnabled(enabled);
        alarmFormUI.setVibrationPattern(vibrationPattern);
    }

    public boolean getIncreasingVolume() {
        return alarmFormUI.isIncreasingVolume();
    }

    public void setIncreasingVolume(boolean increasingVolume) {
        alarmFormUI.setIncreasingVolume(increasingVolume);
    }

    @Inject
    public void setMainRepository(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    public int getVibrationSettings() {
        int enabled = alarmFormUI.isVibrationEnabled() ? Constants.Vibrate.VIBRATION_FLAG_ENABLED : 0;
        int patternID = alarmFormUI.getVibrationPattern();
        return enabled | patternID;
    }
}
