package com.example.clockclone.data;

import com.example.clockclone.domain.TimerState;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface TimerHelper {
    Observable<Long> getTimeLeft();
    Observable<Long> getOvertime();
    Observable<TimerState> getTimerState();
    void start(long duration);
    void pause();
    void resume();
    void cancel();
    void cancelOvertime();
    long getTotalDuration();
    List<Long> getPresets();
    void addPreset(long duration);
    void removePreset(int index);
}
