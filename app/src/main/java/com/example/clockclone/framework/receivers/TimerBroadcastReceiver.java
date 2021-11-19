package com.example.clockclone.framework.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.clockclone.framework.services.StopwatchService;
import com.example.clockclone.framework.services.TimerService;
import com.example.clockclone.util.Constants;

public class TimerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionType = intent.getIntExtra(Constants.Extras.Titles.TIMER_ACTION_TYPE, Constants.Extras.Timer.PAUSE);
        Intent timerServiceIntent = new Intent(context, TimerService.class);
        timerServiceIntent.putExtra(Constants.Extras.Titles.TIMER_ACTION_TYPE, actionType);
        context.startService(timerServiceIntent);
    }
}
