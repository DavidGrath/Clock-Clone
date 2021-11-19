package com.example.clockclone.data;

import com.example.clockclone.domain.SplitLapTime;
import com.example.clockclone.domain.StopwatchState;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface StopwatchHelper {
    void start();
    void stop();
    void lap();
    void reset();
    void resume();
    Observable<Long> getRunningTime();
    Observable<Long> getLappingTime();
    Observable<List<SplitLapTime>> getSplitLapTotals();
    Observable<StopwatchState> getState();
}
