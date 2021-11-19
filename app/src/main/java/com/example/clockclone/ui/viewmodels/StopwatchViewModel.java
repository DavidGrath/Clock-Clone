package com.example.clockclone.ui.viewmodels;

import android.app.Application;

import com.example.clockclone.data.MainRepository;
import com.example.clockclone.data.StopwatchHelper;
import com.example.clockclone.domain.SplitLapTime;
import com.example.clockclone.domain.StopwatchState;
import com.example.clockclone.framework.ClockClone;
import com.example.clockclone.framework.services.StopwatchService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.core.BackpressureStrategy;

public class StopwatchViewModel extends AndroidViewModel {

    private MainRepository mainRepository;

    public StopwatchViewModel(@NonNull @NotNull Application application) {
        super(application);
        ((ClockClone) application).daggerApplicationComponent.inject(this);
    }

    public LiveData<Long> getRunningTime() {
        return LiveDataReactiveStreams.fromPublisher(mainRepository.getRunningTime().toFlowable(BackpressureStrategy.BUFFER));
    }

    public LiveData<Long> getLappingTime() {
        return LiveDataReactiveStreams.fromPublisher(mainRepository.getLappingTime().toFlowable(BackpressureStrategy.BUFFER));
    }

    public LiveData<StopwatchState> getStopwatchState() {
        return LiveDataReactiveStreams.fromPublisher(mainRepository.getStopwatchState().toFlowable(BackpressureStrategy.BUFFER));
    }

    public LiveData<List<SplitLapTime>> getSplitLapTimes() {
        return LiveDataReactiveStreams.fromPublisher(mainRepository.getSplitLapTimes().toFlowable(BackpressureStrategy.BUFFER));
    }

    public void setStopwatchHelper(StopwatchHelper stopwatchHelper) {
        mainRepository.setStopwatchHelper(stopwatchHelper);
    }

    public StopwatchHelper getStopwatchHelper() {
        return mainRepository.getStopwatchHelper();
    }

    @Inject
    public void setMainRepository(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }
}
