package com.example.clockclone.util;

import android.text.TextUtils;

import com.example.clockclone.domain.WorldClockCity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;

public class GeneralUtils {
    public static String formatStopwatchTime(long time) {
        boolean underAnHour;
        int hourPart = (int) time / 3_600_000;
        int minutePart = (int) (time % 3_600_000)/ 60_000;
        int secondPart = (int) ((time % 3_600_000)% 60_000) / 1_000;
        int millisPart = (int) time % 1_000;
        underAnHour = hourPart < 1;
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String minutesFormatted = decimalFormat.format(minutePart);
        String secondsFormatted = decimalFormat.format(secondPart);
        String millisFormatted = new DecimalFormat(".00").format(millisPart);
        StringBuilder stringBuilder = new StringBuilder();
        if(!underAnHour) {
            String hoursFormatted = decimalFormat.format(hourPart);
            stringBuilder.append(hoursFormatted)
                    .append(":");
        }
        stringBuilder.append(minutesFormatted)
                .append(":")
                .append(secondsFormatted)
//                .append(".")
                .append(millisFormatted);
        return stringBuilder.toString();
    }

    public static String timeZoneDifference(WorldClockCity worldClockCity) {
        TimeZone hereTimeZone = Calendar.getInstance(Locale.getDefault()).getTimeZone();
        TimeZone thereTimeZone = TimeZone.getTimeZone(worldClockCity.getTimeZone());
        int trueHereOffset = hereTimeZone.getRawOffset();
        int trueThereOffset = thereTimeZone.getRawOffset();
        if(hereTimeZone.useDaylightTime()) {
            trueHereOffset += hereTimeZone.getDSTSavings();
        }
        if(thereTimeZone.useDaylightTime()) {
            trueThereOffset += thereTimeZone.getDSTSavings();
        }
        int difference = (trueThereOffset - trueHereOffset)/3_600_000;
        String answer;
        if(difference == 0) {
            answer = "Same as local time";
        } else if(difference < 0) {
            answer = (-1 * difference) + " hours behind";
        } else {
            answer = difference + " hours ahead";
        }
        return answer;
    }

    public static interface FindInterface<T> {
        boolean satisfies(T t);
    }
    public static <T> int find(List<T> items, FindInterface<T> filter) {
        int index = -1;
        int length = items.size();
        for(int i = 0; i < length; i++) {
            if(filter.satisfies(items.get(i))) {
                index = i;
                return index;
            }
        }
        return index;
    }
    public static <T> int find(T[] items, FindInterface<T> filter) {
        int index = -1;
        int length = items.length;
        for(int i = 0; i < length; i++) {
            if(filter.satisfies(items[i])) {
                index = i;
                return index;
            }
        }
        return index;
    }

    private static String[] weekdayArray = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    public static String weekdaysToString(int weekdays) {
        if(weekdays == 0) {
            return "";
        }
        ArrayList<String> weekdaysList = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            if((weekdays & (1 << i)) > 0) {
                weekdaysList.add(weekdayArray[i]);
            }
        }
        return TextUtils.join(", ", weekdaysList);
    }

    public static String weekdaysToString(Boolean[] daysOfWeek) {
        ArrayList<String> weekdaysList = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            if(daysOfWeek[i]) {
                weekdaysList.add(weekdayArray[i]);
            }
        }
        return TextUtils.join(", ", weekdaysList);
    }

    public static String snoozeIntToString(int snooze) {
        int enabledMask = snooze & Constants.Snooze.SNOOZE_MASK_ENABLED;
        boolean enabled = (enabledMask & Constants.Snooze.SNOOZE_FLAG_ENABLED) > 0;
        if(!enabled) {
            return "Off";
        }
        int snoozeInterval = snooze & Constants.Snooze.SNOOZE_MASK_INTERVAL;
        int snoozeRepeat = snooze & Constants.Snooze.SNOOZE_MASK_REPEAT;
        String interval = "";
        switch (snoozeInterval) {
            case Constants.Snooze.SNOOZE_INTERVAL_5_MINUTES:
                interval = "5 minutes";
                break;
            case Constants.Snooze.SNOOZE_INTERVAL_10_MINUTES:
                interval = "10 minutes";
                break;
            case Constants.Snooze.SNOOZE_INTERVAL_15_MINUTES:
                interval = "15 minutes";
                break;
            case Constants.Snooze.SNOOZE_INTERVAL_30_MINUTES:
                interval = "30 minutes";
                break;
        }
        String repeat = "";
        switch (snoozeRepeat) {
            case Constants.Snooze.SNOOZE_REPEAT_3_TIMES:
                repeat = "3 times";
                break;
            case Constants.Snooze.SNOOZE_REPEAT_5_TIMES:
                repeat = "5 times";
                break;
            case Constants.Snooze.SNOOZE_REPEAT_FOREVER:
                repeat = "Forever";
                break;
        }
        String value = interval + ", " + repeat;
        return value;
    }
}
