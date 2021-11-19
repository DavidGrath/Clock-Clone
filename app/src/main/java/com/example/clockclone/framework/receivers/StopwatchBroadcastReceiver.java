package com.example.clockclone.framework.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.clockclone.framework.services.StopwatchService;
import com.example.clockclone.util.Constants;

public class StopwatchBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionType = intent.getIntExtra(Constants.Extras.Titles.STOPWATCH_ACTION_TYPE, Constants.Extras.Stopwatch.STOP);
        Intent stopwatchServiceIntent = new Intent(context, StopwatchService.class);
        stopwatchServiceIntent.putExtra(Constants.Extras.Titles.STOPWATCH_ACTION_TYPE, actionType);
        context.startService(stopwatchServiceIntent);
    }
}
