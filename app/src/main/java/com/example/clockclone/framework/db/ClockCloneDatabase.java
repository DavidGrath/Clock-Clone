package com.example.clockclone.framework.db;

import android.content.Context;

import com.example.clockclone.domain.room.Alarm;
import com.example.clockclone.util.Constants;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Alarm.class}, version = Constants.Database.VERSION)
public abstract class ClockCloneDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();

    private static ClockCloneDatabase INSTANCE = null;

    public static ClockCloneDatabase getInstance(Context context) {
        ClockCloneDatabase instance = INSTANCE;
        if(instance != null) {
            return INSTANCE;
        }
        instance = Room.databaseBuilder(context, ClockCloneDatabase.class, "clock-clone")
                .build();
        INSTANCE = instance;
        return INSTANCE;
    }
}
