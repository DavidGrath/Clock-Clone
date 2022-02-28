package com.example.clockclone.framework.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import com.example.clockclone.R;
import com.example.clockclone.data.TimerHelper;
import com.example.clockclone.domain.TimerState;
import com.example.clockclone.framework.receivers.TimerBroadcastReceiver;
import com.example.clockclone.ui.activities.MainActivity;
import com.example.clockclone.ui.activities.TimeUpActivity;
import com.example.clockclone.util.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.ProcessLifecycleOwner;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class TimerService extends Service implements TimerHelper {

    private final DateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private final int REQUEST_CODE_TIMER = 200;
    private final int REQUEST_CODE_TIME_UP = 300;

    private final int REQUEST_CODE_PAUSE = 100;
    private final int REQUEST_CODE_RESUME = 101;
    private final int REQUEST_CODE_CANCEL = 102;
    private final int REQUEST_CODE_CANCEL_ALERT = 103;


    private TimerBinder timerBinder;
    private SharedPreferences sharedPreferences;
    private ArrayList<Long> presets;

    private boolean serviceInForeground = false;
    private NotificationCompat.Action pauseAction;
    private NotificationCompat.Action cancelAction;
    private NotificationCompat.Action resumeAction;

    private NotificationManagerCompat notificationManagerCompat;

    private Intent timerIntent;
    private PendingIntent timerPendingIntent;

    private Handler notificationUpdateHandler;
    private Runnable notificationUpdateRunnable;
    private long lastTimeAppGoneToBackground = System.currentTimeMillis();

    private final long DELTA = 250L;

    private long timeLeft = 0L;
    private long duration = 0L;
    private TimerState timerState = TimerState.OFF;

    private BehaviorSubject<Long> timeLeftObservable = BehaviorSubject.create();
    private BehaviorSubject<Long> overtimeObservable = BehaviorSubject.create();
    private BehaviorSubject<TimerState> timerStateObservable = BehaviorSubject.create();


    private ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(10);
    private ScheduledFuture<?> timerFuture;
    private Runnable timerRunnable = () -> {
        if(timeLeft - DELTA > 0) {
            timeLeft -= DELTA;
            timeLeftObservable.onNext(timeLeft);
        } else {
            timerState = TimerState.OFF;
            timeLeft = 0L;
            timerFuture.cancel(true);
            timeLeftObservable.onNext(timeLeft);
            timerStateObservable.onNext(timerState);
            onDone();
        }
    };
    long overtime = 0L;
    private boolean onOvertime = false;
    Handler overtimeHandler = new Handler(Looper.getMainLooper());
    Runnable overtimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateOvertime();
            Notification n = buildTimeUpNotification(overtime);
            notificationManagerCompat.notify(Constants.Notification.ID.TIMER_ALERT, n);
            overtimeHandler.postDelayed(this, 1_000L);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return START_STICKY;
        }
        int actionType = intent.getIntExtra(Constants.Extras.Titles.TIMER_ACTION_TYPE, -1);
        if(onOvertime && actionType == Constants.Extras.Timer.CANCEL_ALERT) {
            cancelOvertime();
        }
        if(serviceInForeground) {
            if (actionType != -1) {
                switch (actionType) {
                    case Constants.Extras.Timer.PAUSE:
                        if(timerState == TimerState.COUNTING) {
                            pause();
                            notificationUpdateHandler.removeCallbacksAndMessages(null);
                        }
                        break;
                    case Constants.Extras.Timer.CANCEL:
                        if(timerState == TimerState.COUNTING) {
                            cancel();
                            stopForeground(true);
                        }
                        break;
                    case Constants.Extras.Timer.RESUME:
                        if(timerState == TimerState.PAUSED) {
                            resume();
                            notificationUpdateHandler.post(notificationUpdateRunnable);
                        }
                        break;
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timerBinder = new TimerBinder(this);
        simpleDateFormat.setTimeZone(GMT);
        sharedPreferences = getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
        presets = new ArrayList<>();
        String presetsList = sharedPreferences.getString(Constants.Preferences.TIMER_PRESETS, "");
        //TODO Fix empty string give single element
        if(!presetsList.isEmpty()) {
            String[] split = presetsList.split(",");
            for (String s : split) {
                presets.add(Long.parseLong(s));
            }
        }
        ProcessLifecycleOwner.get().getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if(event == Lifecycle.Event.ON_START) {
                appComeToForeground();
            } else if (event == Lifecycle.Event.ON_STOP) {
                appGoneToBackground();
            }
        });

        notificationManagerCompat = NotificationManagerCompat.from(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildChannels();
        }
        Intent timerServiceIntent = new Intent(this, TimerBroadcastReceiver.class);

        Intent pauseIntent = new Intent(timerServiceIntent)
                .putExtra(Constants.Extras.Titles.TIMER_ACTION_TYPE, Constants.Extras.Timer.PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_PAUSE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pauseAction = new NotificationCompat.Action(null, "Pause", pausePendingIntent);

        Intent cancelIntent = new Intent(timerServiceIntent)
                .putExtra(Constants.Extras.Titles.TIMER_ACTION_TYPE, Constants.Extras.Timer.CANCEL);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_CANCEL, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        cancelAction = new NotificationCompat.Action(null, "Cancel", cancelPendingIntent);

        Intent resumeIntent = new Intent(timerServiceIntent)
                .putExtra(Constants.Extras.Titles.TIMER_ACTION_TYPE, Constants.Extras.Timer.RESUME);
        PendingIntent resumePendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_RESUME, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        resumeAction = new NotificationCompat.Action(null, "Resume", resumePendingIntent);

        notificationUpdateHandler = new Handler(Looper.getMainLooper());
        notificationUpdateRunnable = new Runnable() {
            @Override
            public void run() {
//                if(serviceInForeground) {
                    if(timerState == TimerState.OFF) {
                        notificationUpdateHandler.removeCallbacksAndMessages(null);
                        stopForeground(true);
                        return;
                    }
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        Notification notification = buildPreOreoNotification(timeLeft);
                        startForeground(Constants.Notification.ID.TIMER, notification);
                        notificationUpdateHandler.postDelayed(this, 1_000L);
                    } else {
                        Notification timerNotification = buildNotification(timeLeft, timerState);
                        notificationManagerCompat.notify(Constants.Notification.ID.TIMER, timerNotification);
                        notificationUpdateHandler.postDelayed(this, 1_000L);
                    }
//                }
            }
        };
    }

    private void appGoneToBackground() {
        lastTimeAppGoneToBackground = System.currentTimeMillis();
        switch (timerState) {
            case OFF:
                break;
            case COUNTING: {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    Notification notification = buildPreOreoNotification(timeLeft);
                    startForeground(Constants.Notification.ID.TIMER, notification);
                    notificationUpdateHandler.post(notificationUpdateRunnable);
                } else {
                    Notification notification = buildNotification(timeLeft, timerState);
                    startForeground(Constants.Notification.ID.TIMER, notification);
                    notificationUpdateHandler.post(notificationUpdateRunnable);
                }
            }
                break;
            case PAUSED: {
                Notification notification = buildNotification(timeLeft, timerState);
                startForeground(Constants.Notification.ID.TIMER, notification);
                notificationUpdateHandler.post(notificationUpdateRunnable);
            }
                break;
        }

        serviceInForeground = true;
    }

    private void appComeToForeground() {
        switch (timerState) {
            case OFF:
                break;
            case COUNTING:
            case PAUSED: {
                stopForeground(true);
            }
                break;
        }
        serviceInForeground = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return timerBinder;
    }

    @Override
    public Observable<Long> getTimeLeft() {
        return timeLeftObservable;
    }

    @Override
    public Observable<Long> getOvertime() {
        return overtimeObservable;
    }

    @Override
    public Observable<TimerState> getTimerState() {
        return timerStateObservable;
    }

    @Override
    public void start(long duration) {
        if(timerState == TimerState.OFF) {
            this.duration = duration;
            timeLeft = duration;
            timerState = TimerState.COUNTING;
            timerStateObservable.onNext(timerState);
            timeLeftObservable.onNext(timeLeft);
            timerFuture = threadPoolExecutor.scheduleAtFixedRate(timerRunnable, 0L, DELTA, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void pause() {
        if(timerState == TimerState.COUNTING) {
            timerFuture.cancel(true);
            timerState = TimerState.PAUSED;

            timerStateObservable.onNext(timerState);
        }
    }

    @Override
    public void resume() {
        if(timerState == TimerState.PAUSED) {
            timerFuture = threadPoolExecutor.scheduleAtFixedRate(timerRunnable, 0L, DELTA, TimeUnit.MILLISECONDS);

            timerState = TimerState.COUNTING;
            timerStateObservable.onNext(timerState);
        }
    }

    @Override
    public void cancel() {
        if(timerState != TimerState.OFF) {
            timerFuture.cancel(true);
            timeLeft = 0;
            timerState = TimerState.OFF;

            timeLeftObservable.onNext(timeLeft);
            timerStateObservable.onNext(timerState);
        }
    }

    @Override
    public void cancelOvertime() {
        notificationManagerCompat.cancel(Constants.Notification.ID.TIMER_ALERT);
        overtimeHandler.removeCallbacksAndMessages(null);
        overtime = 0L;
        overtimeObservable.onNext(overtime);
        onOvertime = false;
    }

    @Override
    public long getTotalDuration() {
        return duration;
    }

    @Override
    public List<Long> getPresets() {
        return presets;
    }

    @Override
    public void addPreset(long duration) {
        presets.add(duration);
        String presetsString = TextUtils.join(",", presets);
        sharedPreferences.edit()
                .putString(Constants.Preferences.TIMER_PRESETS, presetsString)
                .apply();
    }

    @Override
    public void removePreset(int index) {
        presets.remove(index);
        String presetsString = TextUtils.join(",", presets);
        sharedPreferences.edit()
                .putString(Constants.Preferences.TIMER_PRESETS, presetsString)
                .apply();
    }

    private void onDone() {
        if(onOvertime) {
            cancelOvertime();
        }
        onOvertime = true;
        Notification notification = buildTimeUpNotification(overtime);
        overtimeHandler.post(overtimeRunnable);
        notificationManagerCompat.notify(Constants.Notification.ID.TIMER_ALERT, notification);
    }
    private void updateOvertime() {
        overtime += 1_000L;
        overtimeObservable.onNext(overtime);
    }

    private Notification buildTimeUpNotification(long time) {
        Intent broadcastIntent = new Intent(this, TimerBroadcastReceiver.class);
        broadcastIntent.putExtra(Constants.Extras.Titles.TIMER_ACTION_TYPE, Constants.Extras.Timer.CANCEL_ALERT);
        PendingIntent stopTimeUpIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_CANCEL_ALERT, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action deleteAction = new NotificationCompat.Action(null, "Dismiss", stopTimeUpIntent);

        Intent timeUpIntent = new Intent(this, TimeUpActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //I've reduced the priority of this feature. Will tend to it later
        PendingIntent fullScreenIntent = PendingIntent.getActivity(this, REQUEST_CODE_TIME_UP, timeUpIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String formatted = "-" + simpleDateFormat.format(time);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.Notification.ChannelID.TIMER_ALERT)
                .setSmallIcon(R.drawable.ic_baseline_hourglass_empty_24)
                .setContentTitle("Time's Up!")
                .setContentText(formatted)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setOngoing(true)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                .bigText(formatted)
//                .setBigContentTitle("Time's Up!"))
//                .setFullScreenIntent(fullScreenIntent, true)
                .setOnlyAlertOnce(true)
                .addAction(deleteAction)
                .setCategory(Notification.CATEGORY_ALARM)
                .setDeleteIntent(stopTimeUpIntent);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL));

        }
        Notification notification = builder.build();
        return notification;
    }

    private Notification buildNotification(long timeLeft, TimerState timerState) {
        timerIntent = new Intent(this, MainActivity.class)
                .putExtra(Constants.Extras.Titles.MAIN_ACTIVITY_FRAGMENT_INDEX, Constants.Positions.TIMER);
        timerPendingIntent = PendingIntent.getActivity(this, REQUEST_CODE_TIMER, timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action firstAction;
        if(timerState == TimerState.COUNTING) {
            firstAction = pauseAction;
        } else {
            firstAction = resumeAction;
        }
        NotificationCompat.Action secondAction = cancelAction;
        String formatted = simpleDateFormat.format(timeLeft);
        Notification notification = new NotificationCompat.Builder(this, Constants.Notification.ChannelID.TIMER)
                .setSmallIcon(R.drawable.ic_baseline_hourglass_empty_24)
                .setContentTitle("Timer")
                .setContentText(formatted)
                .setOnlyAlertOnce(true)
                .setShowWhen(true)
                .setWhen(lastTimeAppGoneToBackground)
                .setContentIntent(timerPendingIntent)
                .addAction(firstAction)
                .addAction(secondAction)
                .setAutoCancel(true)
                .build();
        return notification;
    }

    private Notification buildPreOreoNotification(long timeLeft) {
        timerIntent = new Intent(this, MainActivity.class)
                .putExtra(Constants.Extras.Titles.MAIN_ACTIVITY_FRAGMENT_INDEX, Constants.Positions.TIMER);
        timerPendingIntent = PendingIntent.getActivity(this, REQUEST_CODE_TIMER, timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String formatted = simpleDateFormat.format(timeLeft);
        Notification notification = new NotificationCompat.Builder(this, Constants.Notification.ChannelID.TIMER)
                .setSmallIcon(R.drawable.ic_baseline_hourglass_empty_24)
                .setContentTitle("Timer")
                .setContentText(formatted)
                .setOnlyAlertOnce(true)
                .setContentIntent(timerPendingIntent)
                .setAutoCancel(true)
                .build();
        return notification;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void buildChannels() {
        NotificationChannelCompat timerChannel = new NotificationChannelCompat.Builder(Constants.Notification.ChannelID.TIMER, NotificationManagerCompat.IMPORTANCE_MAX)
                .setVibrationEnabled(false)
                .setName(Constants.Notification.ChannelName.TIMER)
                .build();
        notificationManagerCompat.createNotificationChannel(timerChannel);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        NotificationChannelCompat timerAlertChannel = new NotificationChannelCompat.Builder(Constants.Notification.ChannelID.TIMER_ALERT, NotificationManagerCompat.IMPORTANCE_MAX)
                .setName(Constants.Notification.ChannelName.TIMER_ALERT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), audioAttributes)
                .build();
        notificationManagerCompat.createNotificationChannel(timerAlertChannel);
    }

    public static class TimerBinder extends Binder {
        private final TimerService service;

        public TimerBinder(TimerService service) {
            this.service = service;
        }

        public TimerService getService() {
            return service;
        }
    }
}
