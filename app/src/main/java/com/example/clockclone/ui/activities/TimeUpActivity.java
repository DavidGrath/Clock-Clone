package com.example.clockclone.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import io.reactivex.rxjava3.core.BackpressureStrategy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import com.example.clockclone.R;
import com.example.clockclone.data.TimerHelper;
import com.example.clockclone.databinding.ActivityTimeUpBinding;
import com.example.clockclone.framework.services.TimerService;

import java.util.Calendar;
import java.util.Date;

public class TimeUpActivity extends AppCompatActivity {

    private ActivityTimeUpBinding binding;
    private TimerService.TimerBinder timerBinder = null;
    private TimerHelper timerHelper = null;
    private boolean bound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerBinder = (TimerService.TimerBinder) service;
            timerHelper = timerBinder.getService();
            observeLiveDatas();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimeUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setFullScreen();

        Intent serviceIntent = new Intent(this, TimerService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        bound = true;
        final String TAG = "GESTURES";


        GestureDetector.OnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "ON SCROLL E1: " + e1.getX() + " " + e1.getY());
                Log.d(TAG, "ON SCROLL E2: " + e2.getX() + " " + e2.getY());
                Log.d(TAG, "ON SCROLL: " + distanceX + " " + distanceY);
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "ON FLING E1: " + e1.getX() + " " + e1.getY());
                Log.d(TAG, "ON FLING E2: " + e2.getX() + " " + e2.getY());
                Log.d(TAG, "ON FLING: " + velocityX + " " + velocityY);
                timerHelper.cancelOvertime();
                return true;
            }
        };
        GestureDetectorCompat gestureDetector = new GestureDetectorCompat(this, listener);
        binding.imageviewCancelTimeUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

    }

    private void observeLiveDatas() {
        LiveData<Long> overtimeLiveData = LiveDataReactiveStreams.fromPublisher(timerHelper.getOvertime().toFlowable(BackpressureStrategy.BUFFER));
        overtimeLiveData.observe(this, (overtime) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(overtime));
            String hours = Integer.toString(calendar.get(Calendar.HOUR));
            String minutes = Integer.toString(calendar.get(Calendar.MINUTE));
            String seconds = Integer.toString(calendar.get(Calendar.SECOND));
            binding.textviewTimeUpHours.setText(hours);
            binding.textviewTimeUpMinutes.setText(minutes);
            binding.textviewTimeUpSeconds.setText(seconds);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound) {
            unbindService(serviceConnection);
            bound = false;
        }
    }

    private void setFullScreen() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        WindowCompat.setDecorFitsSystemWindows(window, false);
        WindowInsetsControllerCompat windowInsetsControllerCompat = new WindowInsetsControllerCompat(window, binding.getRoot());
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsControllerCompat.setAppearanceLightStatusBars(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        //Deprecated in API 30
        /*int visibility = View.SYSTEM_UI_FLAG_IMMERSIVE |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.getDecorView().setSystemUiVisibility(visibility);*/
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//            setShowWhenLocked(true);
//            setTurnScreenOn(true);
//        }
    }
}