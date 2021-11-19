package com.example.clockclone.util;

import com.example.clockclone.BuildConfig;

public class Constants {

    public static final String APP_NAME = "ClockClone";
    public static class Preferences {
        public static final String TIMER_PRESETS = "timer_presets";
        public static final String SUNDAY_FIRST = "sunday_first";
    }
    public static final class Positions {
        public static final int ALARMS = 0;
        public static final int WORLD_CLOCK = 1;
        public static final int STOPWATCH = 2;
        public static final int TIMER = 3;
    }
    public static final class Notification {
        public static final class ID {
            public static final int STOPWATCH = 100;
            public static final int TIMER = 101;
            public static final int TIMER_ALERT = 102;
        }
        public static final class ChannelID {
            public static final String STOPWATCH = "stopwatch";
            public static final String TIMER = "timer";
            public static final String TIMER_ALERT = "timer_alert";
        }
        public static final class ChannelName {
            public static final String STOPWATCH = "Stopwatch";
            public static final String TIMER = "Timer";
            public static final String TIMER_ALERT = "Time's Up!";
        }
    }
    public static class Extras {
        public static class Titles {
            public static final String STOPWATCH_ACTION_TYPE = "stopwatch_action_type";
            public static final String TIMER_ACTION_TYPE = "timer_action_type";
            public static final String MAIN_ACTIVITY_FRAGMENT_INDEX = "main_activity_fragment_index";
            public static final String EDIT_ALARM_ID = "edit_alarm_id";
            public static final String SNOOZE_ACTIVITY_VALUE = "snooze_activity_value";
            public static final String SOUND_VOLUME_ACTIVITY_SOUND_TITLE_INPUT = "sound_volume_activity_sound_title_input";
            public static final String SOUND_VOLUME_ACTIVITY_SOUND_URI_INPUT = "sound_volume_activity_sound_uri_input";
            public static final String SOUND_VOLUME_ACTIVITY_SOUND_VOLUME_INPUT = "sound_volume_activity_sound_volume_input";
            public static final String SOUND_VOLUME_ACTIVITY_SOUND_TITLE_RESULT = "sound_volume_activity_sound_title_result";
            public static final String SOUND_VOLUME_ACTIVITY_SOUND_URI_RESULT = "sound_volume_activity_sound_uri_result";
            public static final String SOUND_VOLUME_ACTIVITY_SOUND_VOLUME_RESULT = "sound_volume_activity_sound_volume_result";
            public static final String VIBRATION_ACTIVITY_INPUT = "vibration_activity_input";
            public static final String VIBRATION_ACTIVITY_OUTPUT = "vibration_activity_output";
        }
        public static class Stopwatch {
            public static final int STOP = 71;
            public static final int LAP = 72;
            public static final int RESUME = 73;
            public static final int RESET = 74;

        }
        public static class Timer {
            public static final int PAUSE = 200;
            public static final int CANCEL = 201;
            public static final int RESUME = 202;
            public static final int CANCEL_ALERT = 203;
        }
    }
    public static class AnimationDurations {
        public static final long SHORT = 100L;
        public static final long MEDIUM = 200L;
        public static final long LONG = 500L;
    }
    public static class Accuweather {
        public static final String BASE_URL = "https://dataservice.accuweather.com/";
    }
    public static class Weekdays {
        public static final int SUNDAY = 1;
        public static final int MONDAY = 1 << 1;
        public static final int TUESDAY = 1 << 2;
        public static final int WEDNESDAY = 1 << 3;
        public static final int THURSDAY = 1 << 4;
        public static final int FRIDAY = 1 << 5;
        public static final int SATURDAY = 1 << 6;
    }
    public static class Database {
        public static final int VERSION = 1;
    }
    public static class Snooze {
        public static final int SNOOZE_MASK_REPEAT = 0b111;
        public static final int SNOOZE_REPEAT_3_TIMES = 1;
        public static final int SNOOZE_REPEAT_5_TIMES = 2;
        public static final int SNOOZE_REPEAT_FOREVER = 3;

        public static final int SNOOZE_MASK_INTERVAL = 0b111 << 3;
        public static final int SNOOZE_INTERVAL_5_MINUTES = 1 << 3;
        public static final int SNOOZE_INTERVAL_10_MINUTES = 2 << 3;
        public static final int SNOOZE_INTERVAL_15_MINUTES = 3 << 3;
        public static final int SNOOZE_INTERVAL_30_MINUTES = 4 << 3;

        public static final int SNOOZE_MASK_ENABLED = 0b1 << 6;
        public static final int SNOOZE_FLAG_ENABLED = 0b1 << 6;
    }

    public static class Vibrate {
        public static final long[] PATTERN_BASIC_CALL = new long[]{0, 1000, 1000};
        public static final long[] PATTERN_HEARTBEAT = new long[]{0, 150, 50, 150, 50};
        public static final long[] PATTERN_TICKTOCK = new long[]{0, 250, 200, 250, 200};
        public static final long[] PATTERN_WALTZ = new long[]{0, 500, 150, 100, 375, 100, 100};
        public static final long[] PATTERN_ZIG_ZIG_ZIG = new long[]{0, 300, 150, 300, 150, 300, 150};
        public static final long[][] PATTERNS = new long[][]{
                PATTERN_BASIC_CALL,
                PATTERN_HEARTBEAT,
                PATTERN_TICKTOCK,
                PATTERN_WALTZ,
                PATTERN_ZIG_ZIG_ZIG
        };

        public static final int VIBRATION_MASK_PATTERN_ID = 0b111;
        public static final int VIBRATION_BASIC_CALL = 0;
        public static final int VIBRATION_HEARTBEAT = 1;
        public static final int VIBRATION_TICKTOCK = 2;
        public static final int VIBRATION_WALTZ = 3;
        public static final int VIBRATION_ZIG_ZIG_ZIG = 4;

        public static final int VIBRATION_MASK_FLAGS = 0b1 << 3;
        public static final int VIBRATION_FLAG_ENABLED = 1 << 3;

        public static final String[] vibrationPatternTitles =
                new String[]{"Basic Call", "Heartbeat", "Ticktock", "Waltz", "Zig-zig-zig"};
    }

    public static class Formats {
        public static final String ALARM = "yyyy-MM-dd'T'HH:mm'Z'";
    }
}
