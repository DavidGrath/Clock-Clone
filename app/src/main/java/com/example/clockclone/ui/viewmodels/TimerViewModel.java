package com.example.clockclone.ui.viewmodels;

import com.example.clockclone.data.TimerHelper;
import com.example.clockclone.domain.TimerState;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.core.BackpressureStrategy;

public class TimerViewModel extends ViewModel {

    private static final int HOUR = 0;
    private static final int MINUTE = 1;
    private static final int SECOND = 2;

    //SECONDS
    private int totalTime = 0;
    private int timeHour = 0;
    private int timeMinute = 0;
    private int timeSecond = 0;

    private TimerHelper timerHelper = null;

    private MutableLiveData<Boolean> _isZero = new MutableLiveData<>(true);
    public LiveData<Boolean> isZero = _isZero;

    public void setDuration(int which, int time) {
        switch (which) {
            case HOUR:
                timeHour = time;
                break;
            case MINUTE:
                timeMinute = time;
                break;
            case SECOND:
                timeSecond = time;
                break;
        }
        totalTime = (timeHour * 3600) + (timeMinute * 60) + timeSecond;
        _isZero.postValue(totalTime == 0);
    }

    public void setTimerHelper(TimerHelper timerHelper) {
        this.timerHelper = timerHelper;
    }

    public LiveData<Long> getTimeLeftLiveData() {
        LiveData<Long> liveData = LiveDataReactiveStreams.fromPublisher(timerHelper.getTimeLeft().toFlowable(BackpressureStrategy.BUFFER));
        return liveData;
    }

    public LiveData<TimerState> getTimerStateLiveData() {
        LiveData<TimerState> liveData = LiveDataReactiveStreams.fromPublisher(timerHelper.getTimerState().toFlowable(BackpressureStrategy.BUFFER));
        return liveData;
    }

    public void startTimer() {
        if(totalTime > 0) {
            timerHelper.start(totalTime * 1000L);
        }
    }

    public void pauseTimer() {
        timerHelper.pause();
    }

    public void cancelTimer() {
        timerHelper.cancel();
    }

    public void resumeTimer() {
        timerHelper.resume();
    }

}
