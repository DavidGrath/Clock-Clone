package com.example.clockclone.framework.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.example.clockclone.R;
import com.example.clockclone.data.StopwatchHelper;
import com.example.clockclone.domain.SplitLapTime;
import com.example.clockclone.domain.StopwatchState;
import com.example.clockclone.framework.receivers.StopwatchBroadcastReceiver;
import com.example.clockclone.ui.activities.MainActivity;
import com.example.clockclone.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class StopwatchService extends Service implements StopwatchHelper {

    StopwatchBinder stopwatchBinder = null;

    //I believe it should be a prime less than 100
    private final long DELTA = 73L;

    private boolean serviceInForeground = false;
    private StopwatchState stopwatchState = StopwatchState.OFF;
    private BehaviorSubject<StopwatchState> stopwatchStateBehaviorSubject = BehaviorSubject.create();
    private long time = 0L;
    private long lapTime = 0L;
    private boolean lapping = false;
    ScheduledFuture<?> scheduledFuture;
    private BehaviorSubject<Long> runningTime = BehaviorSubject.create();
    private BehaviorSubject<Long> lappingTime = BehaviorSubject.create();
    private ArrayList<SplitLapTime> splitLapTotals = new ArrayList<>();
    private BehaviorSubject<List<SplitLapTime>> splittingLapTotals = BehaviorSubject.create();

    private SimpleDateFormat lessThanHourFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat hourGreaterFormat = new SimpleDateFormat("kk:mm:ss");
    private TimeZone GMT = TimeZone.getTimeZone("GMT");

    private final int REQUEST_CODE_STOPWATCH = 300;

    private final int REQUEST_CODE_STOPWATCH_STOP = 201;
    private final int REQUEST_CODE_STOPWATCH_LAP = 202;
    private final int REQUEST_CODE_STOPWATCH_RESUME = 203;
    private final int REQUEST_CODE_STOPWATCH_RESET = 204;


    private NotificationCompat.Action stopAction;
    private NotificationCompat.Action lapAction;
    private NotificationCompat.Action resumeAction;
    private NotificationCompat.Action resetAction;

    private NotificationManagerCompat managerCompat;

    private Intent stopwatchFragmentIntent;
    private PendingIntent stopwatchFragmentPendingIntent;

    private long lastTimeAppGoneToBackground = System.currentTimeMillis();

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);
    Runnable stopwatchRunnable = ()->{
        time += DELTA;
        if(lapping) {
            lapTime += DELTA;
            lappingTime.onNext(lapTime);
        }
        runningTime.onNext(time);
    };
    private Handler notificationUpdateHandler = new Handler(Looper.getMainLooper());
    Runnable notificationUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if(serviceInForeground) {
                managerCompat.notify(Constants.Notification.ID.STOPWATCH, buildNotification(time, stopwatchState));
                notificationUpdateHandler.postDelayed(this, 1_000L);
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stopwatchBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return START_STICKY;
        }
        int actionType = intent.getIntExtra(Constants.Extras.Titles.STOPWATCH_ACTION_TYPE, -1);
        if(actionType != -1) {
            switch (actionType) {
                case Constants.Extras.Stopwatch.STOP:
                    stop();
                    if(serviceInForeground) {
                        notificationUpdateHandler.removeCallbacksAndMessages(null);
                        managerCompat.notify(Constants.Notification.ID.STOPWATCH, buildNotification(time, stopwatchState));
                    }
                    break;
                case Constants.Extras.Stopwatch.RESUME:
                    resume();
                    if(serviceInForeground) {
                        notificationUpdateHandler.post(notificationUpdateRunnable);
                    }
                    break;
                case Constants.Extras.Stopwatch.LAP:
                    lap();
                    break;
                case Constants.Extras.Stopwatch.RESET:
                    reset();
                    notificationUpdateHandler.removeCallbacksAndMessages(null);
                    stopForeground(true);
                    stopSelf();
                    break;
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopwatchBinder = new StopwatchBinder(this);

        lessThanHourFormat.setTimeZone(GMT);
        hourGreaterFormat.setTimeZone(GMT);

        managerCompat = NotificationManagerCompat.from(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildChannel(Constants.Notification.ChannelID.STOPWATCH);
        }
        Intent stopwatchBroadcastIntent = new Intent(this, StopwatchBroadcastReceiver.class);

        Intent stopIntent = new Intent(stopwatchBroadcastIntent)
                .putExtra(Constants.Extras.Titles.STOPWATCH_ACTION_TYPE, Constants.Extras.Stopwatch.STOP);

        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_STOPWATCH_STOP, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        stopAction = new NotificationCompat.Action(null, "Stop", stopPendingIntent);

        Intent lapIntent = new Intent(stopwatchBroadcastIntent)
                .putExtra(Constants.Extras.Titles.STOPWATCH_ACTION_TYPE, Constants.Extras.Stopwatch.LAP);
        PendingIntent lapPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_STOPWATCH_LAP, lapIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        lapAction = new NotificationCompat.Action(null, "Lap", lapPendingIntent);

        Intent resumeIntent = new Intent(stopwatchBroadcastIntent)
                .putExtra(Constants.Extras.Titles.STOPWATCH_ACTION_TYPE, Constants.Extras.Stopwatch.RESUME);
        PendingIntent resumePendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_STOPWATCH_RESUME, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        resumeAction = new NotificationCompat.Action(null, "Resume", resumePendingIntent);

        Intent resetIntent = new Intent(stopwatchBroadcastIntent)
                .putExtra(Constants.Extras.Titles.STOPWATCH_ACTION_TYPE, Constants.Extras.Stopwatch.RESET);
        PendingIntent resetPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_STOPWATCH_RESET, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        resetAction = new NotificationCompat.Action(null, "Reset", resetPendingIntent);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if(event == Lifecycle.Event.ON_START) {
                    appComeToForeground();
                } else if (event == Lifecycle.Event.ON_STOP) {
                    appGoneToBackground();
                }
            }
        });
        stopwatchFragmentIntent = new Intent(this, MainActivity.class)
                .putExtra(Constants.Extras.Titles.MAIN_ACTIVITY_FRAGMENT_INDEX, Constants.Positions.STOPWATCH);
        stopwatchFragmentPendingIntent = PendingIntent.getActivity(this, REQUEST_CODE_STOPWATCH, stopwatchFragmentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void start() {
        if(stopwatchState == StopwatchState.OFF) {
            scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(stopwatchRunnable, 0L, DELTA, TimeUnit.MILLISECONDS);
            stopwatchState = StopwatchState.RUNNING;
            stopwatchStateBehaviorSubject.onNext(stopwatchState);
        }
    }

    @Override
    public void stop() {
        if(stopwatchState == StopwatchState.RUNNING) {
            scheduledFuture.cancel(true);
            stopwatchState = StopwatchState.PAUSED;
            stopwatchStateBehaviorSubject.onNext(stopwatchState);
        }
    }

    @Override
    public void resume() {
        if(stopwatchState == StopwatchState.PAUSED) {
            scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(stopwatchRunnable, 0L, DELTA, TimeUnit.MILLISECONDS);
            stopwatchState = StopwatchState.RUNNING;
            stopwatchStateBehaviorSubject.onNext(stopwatchState);
        }
    }

    @Override
    public void lap() {
        if(stopwatchState != StopwatchState.RUNNING) {
            return;
        }
        if(!lapping) {
            lapping = true;
            lapTime = time;
        }
        int index = splitLapTotals.size() + 1;
        SplitLapTime splitLapTime = new SplitLapTime(index, time, lapTime);
        splitLapTotals.add(splitLapTime);
        splittingLapTotals.onNext(splitLapTotals);
        lapTime = 0L;
        lappingTime.onNext(lapTime);
    }

    @Override
    public void reset() {
        if(stopwatchState == StopwatchState.PAUSED) {
            time = 0L;
            runningTime.onNext(time);
            stopwatchState = StopwatchState.OFF;
            stopwatchStateBehaviorSubject.onNext(stopwatchState);
        }
        if(lapping) {
            lapping = false;
            lapTime = 0L;
            splitLapTotals.clear();
            lappingTime.onNext(lapTime);
            splittingLapTotals.onNext(splitLapTotals);
        }
    }

    @Override
    public Observable<Long> getRunningTime() {
        return runningTime;
    }

    @Override
    public Observable<Long> getLappingTime() {
        return lappingTime;
    }

    @Override
    public Observable<List<SplitLapTime>> getSplitLapTotals() {
        return splittingLapTotals;
    }

    @Override
    public Observable<StopwatchState> getState() {
        return stopwatchStateBehaviorSubject;
    }

    private void appGoneToBackground() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        lastTimeAppGoneToBackground = System.currentTimeMillis();
        if (stopwatchState != StopwatchState.OFF) {
            startForeground(Constants.Notification.ID.STOPWATCH, buildNotification(time, stopwatchState));
            notificationUpdateHandler.post(notificationUpdateRunnable);
        }
        serviceInForeground = true;
    }

    public void appComeToForeground() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        switch (stopwatchState) {
            case OFF:
                break;
            case RUNNING:
            case PAUSED:
                stopForeground(true);
                break;
        }
        serviceInForeground = false;
    }

    private Notification buildNotification(long time, StopwatchState stopwatchState) {
        int hour = (int) time / 3_600_000;
        String formatted;
        if(hour >= 1) {
            formatted = hourGreaterFormat.format(time);
        } else {
            formatted = lessThanHourFormat.format(time);
        }
        int latestLapIndex = splitLapTotals.size() + 1;
        String lapText = "Lap " + latestLapIndex;
        NotificationCompat.Action firstAction, secondAction;
        if(stopwatchState == StopwatchState.RUNNING) {
            firstAction = stopAction;
            secondAction = lapAction;
        } else {
            firstAction = resumeAction;
            secondAction = resetAction;
        }
        Notification notification = new NotificationCompat.Builder(this, Constants.Notification.ChannelID.STOPWATCH)
                .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
                .setContentTitle("Stopwatch")
                .setContentText(formatted)
                .setShowWhen(true)
                .setWhen(lastTimeAppGoneToBackground)
                .addAction(firstAction)
                .addAction(secondAction)
                .setContentIntent(stopwatchFragmentPendingIntent)
                .build();
        return notification;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void buildChannel(String id) {
        NotificationChannelCompat channelCompat = new NotificationChannelCompat.Builder(id, NotificationManagerCompat.IMPORTANCE_MAX)
                .setVibrationEnabled(false)
                .setName("Stopwatch")
                .build();
        managerCompat.createNotificationChannel(channelCompat);
    }

    public class StopwatchBinder extends Binder {

        public StopwatchService stopwatchService;

        public StopwatchBinder(StopwatchService service) {
            this.stopwatchService = service;
        }

    }
}
