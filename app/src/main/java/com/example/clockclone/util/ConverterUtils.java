package com.example.clockclone.util;

import android.net.Uri;

import com.example.clockclone.domain.room.Alarm;
import com.example.clockclone.domain.ui.AlarmFormUI;
import com.example.clockclone.domain.ui.AlarmSummaryUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.lifecycle.LiveDataReactiveStreams;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ConverterUtils {

    private static final SimpleDateFormat databaseFormat = new SimpleDateFormat(Constants.Formats.ALARM);

    public static AlarmFormUI alarmDBToAlarmFormUI(Alarm alarm, String soundName, boolean sundayFirst) {
        int snoozeEnabledMask = alarm.snoozeSettings & Constants.Snooze.SNOOZE_MASK_ENABLED;
        boolean snoozeEnabled = (snoozeEnabledMask & Constants.Snooze.SNOOZE_FLAG_ENABLED) > 0;
        int snoozeInterval = alarm.snoozeSettings & Constants.Snooze.SNOOZE_MASK_INTERVAL;
        int snoozeRepeat = alarm.snoozeSettings & Constants.Snooze.SNOOZE_MASK_REPEAT;
        int vibrationEnabledMask = alarm.vibrationSettings & Constants.Vibrate.VIBRATION_MASK_FLAGS;
        boolean vibrationEnabled = (vibrationEnabledMask & Constants.Vibrate.VIBRATION_FLAG_ENABLED) > 0;
        int vibrationPattern = alarm.vibrationSettings & Constants.Vibrate.VIBRATION_MASK_PATTERN_ID;
        String vibrationName = Constants.Vibrate.vibrationPatternTitles[vibrationPattern];
        Calendar occurrenceDate = null;
        if (alarm.date != null) {
            try {
                Date date = databaseFormat.parse(alarm.date);
                occurrenceDate = Calendar.getInstance();
                occurrenceDate.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        ArrayList<Boolean> daysOfWeek = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int weekdayBit = 1 << i;
            boolean weekday = (weekdayBit & alarm.daysOfWeek) > 0;
            daysOfWeek.add(weekday);
        }

        AlarmFormUI alarmFormUI = new AlarmFormUI(alarm.timestamp / 60, alarm.timestamp % 60, 1, occurrenceDate, true, daysOfWeek, alarm.name, vibrationEnabled, vibrationPattern, vibrationName, Uri.parse(alarm.soundUri), soundName, alarm.volume, alarm.increasingVolume, snoozeEnabled, snoozeInterval, snoozeRepeat, sundayFirst);
        return alarmFormUI;
    }

    public static Alarm alarmFormUIToAlarmDB(int alarmID, AlarmFormUI alarmFormUI) {
        int weekdays = 0;
        for (int i = 0; i < 7; i++) {
            if (alarmFormUI.getDaysOfWeek().get(i)) {
                weekdays |= 1 << i;
            }
        }

        Calendar calendar = alarmFormUI.getOccurrenceDate();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String format = databaseFormat.format(calendar.getTime());
        int vibrationEnabled = alarmFormUI.isVibrationEnabled() ? Constants.Vibrate.VIBRATION_FLAG_ENABLED : 0;
        int vibrationSettings = vibrationEnabled | alarmFormUI.getVibrationPattern();
        int snoozeEnabled = alarmFormUI.isSnooze() ? Constants.Snooze.SNOOZE_FLAG_ENABLED : 0;
        int snoozeSettings = snoozeEnabled | alarmFormUI.getSnoozeInterval() | alarmFormUI.getSnoozeRepeat();
        Alarm alarm;
        Integer id = alarmID == -1 ? null : alarmID;
        alarm = new Alarm(id, true, alarmFormUI.getTimestamp(), format, weekdays,
                alarmFormUI.getName(), vibrationSettings, snoozeSettings, alarmFormUI.getSoundUri().toString(), alarmFormUI.getVolume(), alarmFormUI.isIncreasingVolume());
        return alarm;
    }

//    public static AlarmSummaryUI alarmSummaryDBToAlarmSummaryUI(AlarmSummary alarmSummary) {
//        Date occurrenceDate = null;
//        if(alarmSummary.date != null) {
//            try {
//                occurrenceDate = databaseFormat.parse(alarmSummary.date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        AlarmSummaryUI alarmSummaryUI = new AlarmSummaryUI(alarmSummary.id, alarmSummary.timestamp, alarmSummary.onceOrRecurring, occurrenceDate, alarmSummary.daysOfWeek, alarmSummary.name, alarmSummary.enabled);
//        return alarmSummaryUI;
//    }
}
