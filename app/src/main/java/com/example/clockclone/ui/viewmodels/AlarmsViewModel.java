package com.example.clockclone.ui.viewmodels;

import android.app.Application;
import android.util.Log;

import com.example.clockclone.data.MainRepository;
import com.example.clockclone.domain.room.Alarm;
import com.example.clockclone.domain.ui.AlarmSummaryUI;
import com.example.clockclone.framework.ClockClone;
import com.example.clockclone.framework.db.AlarmDao;
import com.example.clockclone.framework.db.ClockCloneDatabase;
import com.example.clockclone.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.Transformations;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AlarmsViewModel extends AndroidViewModel {

    private MainRepository mainRepository;


    public AlarmsViewModel(@NonNull @NotNull Application application) {
        super(application);
        ((ClockClone) application).daggerApplicationComponent.inject(this);
    }

    @Inject
    public void setMainRepository(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    private static final SimpleDateFormat databaseFormat = new SimpleDateFormat(Constants.Formats.ALARM);

    public LiveData<List<AlarmSummaryUI>> getAlarmSummaries() {
        Flowable<List<Alarm>> flowable = mainRepository.getAllAlarms();
        LiveData<List<Alarm>> liveData = LiveDataReactiveStreams.fromPublisher(flowable);
        LiveData<List<AlarmSummaryUI>> mapped = Transformations.map(liveData, new Function<List<Alarm>, List<AlarmSummaryUI>>() {
            @Override
            public List<AlarmSummaryUI> apply(List<Alarm> input) {
                ArrayList<AlarmSummaryUI> alarmSummaryUIArrayList = new ArrayList<>();
                for(Alarm alarm : input) {
                    Date occurrenceDate = null;
                    if(alarm.date != null) {
                        try {
                            occurrenceDate = databaseFormat.parse(alarm.date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    AlarmSummaryUI alarmSummaryUI = new AlarmSummaryUI(alarm.id, alarm.timestamp, occurrenceDate, alarm.daysOfWeek, alarm.name, alarm.enabled);
                    alarmSummaryUIArrayList.add(alarmSummaryUI);
                }
                return alarmSummaryUIArrayList;
            }
        });
        return mapped;
//        Flowable<List<AlarmSummary>> flowable = alarmDao.getAllAlarmsSummarized().subscribeOn(Schedulers.io()).toFlowable(BackpressureStrategy.BUFFER);
//        LiveData<List<AlarmSummary>> liveData = LiveDataReactiveStreams.fromPublisher(flowable);
//        LiveData<List<AlarmSummaryUI>> mapped = Transformations.map(liveData, new Function<List<AlarmSummary>, List<AlarmSummaryUI>>() {
//            @Override
//            public List<AlarmSummaryUI> apply(List<AlarmSummary> input) {
//                ArrayList<AlarmSummaryUI> alarmSummaryUIArrayList = new ArrayList<>();
//                for(AlarmSummary alarmSummary : input) {
//                    alarmSummaryUIArrayList.add(ConverterUtils.alarmSummaryDBToAlarmSummaryUI(alarmSummary));
//                }
//                return alarmSummaryUIArrayList;
//            }
//        });
//        return mapped;
    }

    public void deleteAllAlarms() {
        mainRepository.deleteAllAlarms();
    }

    public void setAlarmEnabled(int id, boolean enabled) {
        mainRepository.setAlarmEnabled(id, enabled);
    }
}
