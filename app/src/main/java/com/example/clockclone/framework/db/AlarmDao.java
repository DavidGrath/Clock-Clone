package com.example.clockclone.framework.db;

import com.example.clockclone.domain.room.Alarm;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface AlarmDao {

    @Query("SELECT * FROM Alarm")
    Observable<List<Alarm>> getAllAlarms();

//    @Query("SELECT id, enabled, timestamp, onceOrRecurring, date, daysOfWeek, name from Alarm")
//    Observable<List<AlarmSummary>> getAllAlarmsSummarized();

    @Query("SELECT * FROM Alarm WHERE id = :id")
    Maybe<Alarm> getAlarm(int id);

    @Insert
    Maybe<Long> addAlarm(Alarm alarm);

    //Another weird thing. Maybe<Void> won't call onSuccess, and thus LiveData won't trigger. Sigh
    @Update
    Maybe<Integer> updateAlarm(Alarm alarm);

    @Query("UPDATE Alarm SET enabled = :enabled WHERE id = :id")
    Maybe<Void> setAlarmEnabled(int id, boolean enabled);

    @Query("DELETE FROM Alarm WHERE id = :id")
    Maybe<Void> deleteAlarm(int id);

    //TODO Remove later
    @Query("DELETE FROM Alarm")
    Maybe<Void> deleteAllAlarms();
}
