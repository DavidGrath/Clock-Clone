package com.example.clockclone.data;

import com.example.clockclone.domain.SplitLapTime;
import com.example.clockclone.domain.StopwatchState;
import com.example.clockclone.domain.WorldClockCity;
import com.example.clockclone.domain.WorldClockCityInfo;
import com.example.clockclone.domain.room.Alarm;
import com.example.clockclone.framework.db.AlarmDao;
import com.example.clockclone.framework.db.ClockCloneDatabase;
import com.example.clockclone.framework.di.scopes.ApplicationScope;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@ApplicationScope
public class MainRepository {

    //Can't do constructor injection here, so a setter is appropriate
    private StopwatchHelper stopwatchHelper = null;
    private WorldClockHelper worldClockHelper;

    Observable<Long> runningTime = null;
    Observable<Long> lappingTime = null;
    Observable<List<SplitLapTime>> splitLapTimes = null;
    Observable<StopwatchState> stopwatchState = null;
    Observable<List<WorldClockCityInfo>> weatherCityInfoList;
    Observable<Integer> interval;
    Observable<List<WorldClockCityInfo>> weatherCityInfoListInterval;
    private AlarmDao alarmDao;

    @Inject
    public MainRepository(WorldClockHelper worldClockHelper, ClockCloneDatabase database) {
        this.worldClockHelper = worldClockHelper;
        weatherCityInfoList = worldClockHelper.getWeatherInfo();
        interval = worldClockHelper.getInterval();
        weatherCityInfoListInterval = Observable.combineLatest(weatherCityInfoList, interval, (w, i)-> {
            return w;
        });
        alarmDao = database.alarmDao();
    }


    public void setStopwatchHelper(StopwatchHelper stopwatchHelper) {
        this.stopwatchHelper = stopwatchHelper;
        runningTime = stopwatchHelper.getRunningTime();
        lappingTime = stopwatchHelper.getLappingTime();
        stopwatchState = stopwatchHelper.getState();
        splitLapTimes = stopwatchHelper.getSplitLapTotals();
    }

    public StopwatchHelper getStopwatchHelper() {
        return stopwatchHelper;
    }

    public Observable<Long> getRunningTime() {
        return runningTime;
    }

    public Observable<Long> getLappingTime() {
        return lappingTime;
    }

    public Observable<List<SplitLapTime>> getSplitLapTimes() {
        return splitLapTimes;
    }

    public Observable<StopwatchState> getStopwatchState() {
        return stopwatchState;
    }

    public Observable<List<WorldClockCityInfo>> getWorldClockList() {
        return weatherCityInfoListInterval;
    }

    public void updateWeatherInfo() {
        worldClockHelper.updateWeatherInfo();
    }

    public Single<String> addCity(String timeZone) {
        return worldClockHelper.saveCity(timeZone);
    }

    public void deleteCity(String timeZone) {
        worldClockHelper.deleteCity(timeZone);
    }

    public List<WorldClockCity> getFullCityList() {
        return worldClockHelper.getFullCityList();
    }

    public void refreshWeather(String timeZone) {
        worldClockHelper.updateWeatherInfo(timeZone);
    }

    public void swapWorldClockItems(int firstIndex, int secondIndex) {
        worldClockHelper.swapCities(firstIndex, secondIndex);
    }

    public void deleteCities(List<String> timeZoneList) {
        worldClockHelper.deleteCities(timeZoneList);
    }

    public Flowable<List<Alarm>> getAllAlarms() {
        return alarmDao.getAllAlarms().subscribeOn(Schedulers.io()).toFlowable(BackpressureStrategy.BUFFER);
    }

    public void deleteAlarm(int id) {
        alarmDao.deleteAlarm(id)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteAllAlarms() {
        alarmDao.deleteAllAlarms()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void setAlarmEnabled(int id, boolean isEnabled) {
        alarmDao.setAlarmEnabled(id, isEnabled)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Maybe<Alarm> getAlarm(int id) {
        return alarmDao.getAlarm(id);
    }

    public Maybe<Long> addAlarm(Alarm alarm) {
        return alarmDao.addAlarm(alarm);
    }

    public Maybe<Integer> updateAlarm(Alarm alarm) {
        return alarmDao.updateAlarm(alarm);
    }
}
