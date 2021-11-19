package com.example.clockclone.domain.ui;

import android.net.Uri;

import com.example.clockclone.util.Constants;
import com.example.clockclone.util.GeneralUtils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.library.baseAdapters.BR;

public class AlarmFormUI extends BaseObservable {
    private int timestampHour;
    private int timestampMinute;
    private int timestampAmPm;
    private Calendar occurrenceDate;
    private boolean occurrenceDateToday;
    private ObservableArrayList<Boolean> daysOfWeek;
    private String name;
    private boolean vibrationEnabled;
    private int vibrationPattern;
    private String vibrationName;
    private Uri soundUri;
    private String soundName;
    private int volume;
    private boolean increasingVolume;
    private boolean snooze;
    private int snoozeInterval;
    private int snoozeRepeat;
    private boolean sundayFirstDay;

    private String displayDate;
    private String snoozeDisplay;

    public AlarmFormUI(int timestampHour, int timestampMinute, int timestampAmPm, Calendar occurrenceDate, boolean occurrenceDateToday, ArrayList<Boolean> daysOfWeek, String name, boolean vibrationEnabled, int vibrationPattern, String vibrationName, Uri soundUri, String soundName, int volume, boolean increasingVolume, boolean snooze, int snoozeInterval, int snoozeRepeat, boolean sundayFirstDay) {
        this.timestampHour = timestampHour;
        this.timestampMinute = timestampMinute;
        this.timestampAmPm = timestampAmPm;
        this.occurrenceDate = occurrenceDate;
        //Todo test for past date
        this.occurrenceDateToday = occurrenceDateToday;
        this.daysOfWeek = new ObservableArrayList<>();
        this.daysOfWeek.addAll(daysOfWeek);
        this.name = name;
        this.vibrationEnabled = vibrationEnabled;
        this.vibrationPattern = vibrationPattern;
        this.vibrationName = vibrationName;
        this.soundUri = soundUri;
        this.soundName = soundName;
        this.volume = volume;
        this.increasingVolume = increasingVolume;
        this.snooze = snooze;
        this.snoozeInterval = snoozeInterval;
        this.snoozeRepeat = snoozeRepeat;
        this.sundayFirstDay = sundayFirstDay;
        displayDate = "";
        snoozeDisplay = "";
        updateDisplayDate();
        updateSnoozeDisplay();
        updateVibrationDisplay();
    }

    public AlarmFormUI(int defaultVolume, String defaultSoundName, Uri defaultSoundUri, boolean vibrationEnabled, int vibrationPattern, String vibrationName, boolean snoozeEnabled, int defaultSnoozeInterval, int defaultSnoozeRepeat, boolean sundayFirstDay) {
        this.timestampHour = 6;
        this.timestampMinute = 0;
        this.timestampAmPm = 0;
        this.occurrenceDate = Calendar.getInstance();
        this.occurrenceDateToday = true;
        this.daysOfWeek = new ObservableArrayList<>();
        for(int i = 0; i < 7; i++) {
            this.daysOfWeek.add(false);
        }
        this.name = null;
        this.vibrationEnabled = vibrationEnabled;
        this.vibrationPattern = vibrationPattern;
        this.vibrationName = vibrationName;
        this.soundUri = defaultSoundUri;
        this.soundName = defaultSoundName;
        this.volume = defaultVolume;
        this.increasingVolume = true;
        this.snooze = snoozeEnabled;
        this.snoozeInterval = defaultSnoozeInterval;
        this.snoozeRepeat = defaultSnoozeRepeat;
        this.sundayFirstDay = sundayFirstDay;
        displayDate = "";
        snoozeDisplay = "";
        updateDisplayDate();
        updateSnoozeDisplay();
        updateVibrationDisplay();
    }

    public int getTimestampHour() {
        return timestampHour;
    }

    public void setTimestampHour(int timestampHour) {
        this.timestampHour = timestampHour;
        updateDisplayDate();
    }


    public int getTimestampMinute() {
        return timestampMinute;
    }

    public void setTimestampMinute(int timestampMinute) {
        this.timestampMinute = timestampMinute;
        updateDisplayDate();
    }

    public int getTimestampAmPm() {
        return timestampAmPm;
    }

    public void setTimestampAmPm(int timestampAmPm) {
        this.timestampAmPm = timestampAmPm;
    }

    public Calendar getOccurrenceDate() {
        return occurrenceDate;
    }

    public void setOccurrenceDate(Calendar occurrenceDate) {
        this.occurrenceDate = occurrenceDate;
        occurrenceDateToday = occurrenceDate.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        updateDisplayDate();
    }

    public ArrayList<Boolean> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(Boolean[] daysOfWeek) {
        this.daysOfWeek.clear();
        this.daysOfWeek.addAll(Arrays.asList(daysOfWeek));
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        if(!Objects.equals(this.name, name)) {
            this.name = name;
            notifyPropertyChanged(BR.name);
        }
    }

    public String getAlarmName() {
        return name;
    }

    @Bindable
    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        if(this.vibrationEnabled != vibrationEnabled) {
            this.vibrationEnabled = vibrationEnabled;
            notifyPropertyChanged(BR.vibrationEnabled);
            updateVibrationDisplay();
        }
    }

    public int getVibrationPattern() {
        return vibrationPattern;
    }

    public void setVibrationPattern(int vibrationPattern) {
        this.vibrationPattern = vibrationPattern;
        updateVibrationDisplay();
    }

    @Bindable
    public String getVibrationName() {
        return vibrationName;
    }

    private void setVibrationName(String vibrationName) {
        if(!this.vibrationName.equals(vibrationName)) {
            this.vibrationName = vibrationName;
            notifyPropertyChanged(BR.vibrationName);
        }
    }

    @Bindable
    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        if(!this.soundName.equals(soundName)) {
            this.soundName = soundName;
            notifyPropertyChanged(BR.soundName);
        }
    }

    @Bindable
    public Uri getSoundUri() {
        return soundUri;
    }

    public void setSoundUri(Uri soundUri) {
        if(!this.soundUri.equals(soundUri)) {
            this.soundUri = soundUri;
            notifyPropertyChanged(BR.soundUri);
        }
    }

    @Bindable
    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        if(this.volume != volume) {
            this.volume = volume;
            notifyPropertyChanged(BR.volume);
        }
    }

    //This property is a bit useless since I only found it on my old Samsung phone that's at home,
    //So I'll update it when the time comes (today is 14 Nov 2021)
    @Bindable
    public boolean isIncreasingVolume() {
        return increasingVolume;
    }

    public void setIncreasingVolume(boolean increasingVolume) {
        if(this.increasingVolume != increasingVolume) {
            this.increasingVolume = increasingVolume;
            notifyPropertyChanged(BR.increasingVolume);
        }
    }

    @Bindable
    public boolean isSnooze() {
        return snooze;
    }

    public void setSnooze(boolean snooze) {
        if(this.snooze != snooze) {
            this.snooze = snooze;
            notifyPropertyChanged(BR.snooze);
            updateSnoozeDisplay();
        }
    }

    public int getSnoozeInterval() {
        return snoozeInterval;
    }

    public void setSnoozeInterval(int snoozeInterval) {
        this.snoozeInterval = snoozeInterval;
        updateSnoozeDisplay();
    }

    public int getSnoozeRepeat() {
        return snoozeRepeat;
    }

    public void setSnoozeRepeat(int snoozeRepeat) {
        this.snoozeRepeat = snoozeRepeat;
        updateSnoozeDisplay();
    }

    @Bindable
    public String getDisplayDate() {
        return displayDate;
    }

    @Bindable
    public String getSnoozeDisplay() {
        return snoozeDisplay;
    }

    private void updateDisplayDate() {
        boolean daysExist = false;
        for(boolean b : daysOfWeek) {
            if(b) {
                daysExist = true;
                break;
            }
        }
        if(daysExist) {
            setDisplayDate(GeneralUtils.weekdaysToString(daysOfWeek.toArray(new Boolean[0])));
        } else {
            Calendar calendar = Calendar.getInstance();
            if(occurrenceDateToday) {
                int totalMinutesNow = (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
                int totalMinutesSet = timestampHour * 60 + timestampMinute;
                //TODO test something, equal to current minutes shows today instead of tomorrow
                if(totalMinutesSet <= totalMinutesNow) {
                    if(occurrenceDate.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                        occurrenceDate.add(Calendar.DAY_OF_YEAR, 1);
                    }
                } else {
                    occurrenceDate = Calendar.getInstance();
                }
            }
            String todayTomorrow;
            if(occurrenceDate.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) + 1) {
                todayTomorrow = "Tomorrow - ";
            } else if(occurrenceDate.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                todayTomorrow = "Today - ";
            } else {
                todayTomorrow = "";
            }
            setDisplayDate(todayTomorrow + occurrenceDate.getTime().toString());
        }
    }

    public void setDisplayDate(String displayDate) {
        if(!this.displayDate.equals(displayDate)) {
            this.displayDate = displayDate;
            notifyPropertyChanged(BR.displayDate);
        }
    }

    private void updateSnoozeDisplay() {
        int enabledInt = snooze ? Constants.Snooze.SNOOZE_FLAG_ENABLED : 0;
        setSnoozeDisplay(GeneralUtils.snoozeIntToString(enabledInt | snoozeInterval | snoozeRepeat));
    }

    private void updateVibrationDisplay() {
        if(vibrationEnabled) {
            setVibrationName(Constants.Vibrate.vibrationPatternTitles[vibrationPattern]);
        } else {
            setVibrationName("Off");
        }
    }

    public void setSnoozeDisplay(String snoozeDisplay) {
        if(!this.snoozeDisplay.equals(snoozeDisplay)) {
            this.snoozeDisplay = snoozeDisplay;
            notifyPropertyChanged(BR.snoozeDisplay);
        }
    }

    public void toggleDayOfWeek(int position) {
        daysOfWeek.set(position, !daysOfWeek.get(position));
        updateDisplayDate();
    }

    public short getTimestamp() {
        return (short) ((short) timestampHour * 60 + timestampMinute);
    }

    public boolean isSundayFirstDay() {
        return sundayFirstDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmFormUI that = (AlarmFormUI) o;
        return timestampHour == that.timestampHour &&
                timestampMinute == that.timestampMinute &&
                timestampAmPm == that.timestampAmPm &&
                occurrenceDateToday == that.occurrenceDateToday &&
                vibrationEnabled == that.vibrationEnabled &&
                vibrationPattern == that.vibrationPattern &&
                volume == that.volume &&
                increasingVolume == that.increasingVolume &&
                snooze == that.snooze &&
                snoozeInterval == that.snoozeInterval &&
                snoozeRepeat == that.snoozeRepeat &&
                Objects.equals(occurrenceDate, that.occurrenceDate) &&
                daysOfWeek.equals(that.daysOfWeek) &&
                Objects.equals(name, that.name) &&
                vibrationName.equals(that.vibrationName) &&
                soundUri.equals(that.soundUri) &&
                soundName.equals(that.soundName) &&
                displayDate.equals(that.displayDate) &&
                snoozeDisplay.equals(that.snoozeDisplay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestampHour, timestampMinute, timestampAmPm, occurrenceDate, occurrenceDateToday, daysOfWeek, name, vibrationEnabled, vibrationPattern, vibrationName, soundUri, soundName, volume, increasingVolume, snooze, snoozeInterval, snoozeRepeat, displayDate, snoozeDisplay);
    }
}
