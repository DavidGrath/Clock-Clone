package com.example.clockclone.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.clockclone.R;
import com.example.clockclone.data.MainRepository;
import com.example.clockclone.data.StopwatchHelper;
import com.example.clockclone.databinding.FragmentStopwatchBinding;
import com.example.clockclone.domain.SplitLapTime;
import com.example.clockclone.domain.StopwatchState;
import com.example.clockclone.framework.ClockClone;
import com.example.clockclone.framework.services.StopwatchService;
import com.example.clockclone.ui.adapters.SplitLapRecyclerAdapter;
import com.example.clockclone.ui.viewmodels.StopwatchViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.rxjava3.core.BackpressureStrategy;

public class StopwatchFragment extends Fragment implements View.OnClickListener {

    private StopwatchViewModel viewModel;
    private FragmentStopwatchBinding fragmentStopwatchBinding;
    private StopwatchHelper stopwatchHelper;
    private StopwatchState cachedStopwatchState = StopwatchState.OFF;
    private SimpleDateFormat lessThanHourFormat = new SimpleDateFormat("mm:ss.SS");
    private SimpleDateFormat hourGreaterFormat = new SimpleDateFormat("kk:mm:ss.SS");
    private TimeZone GMT = TimeZone.getTimeZone("GMT");
    private SplitLapRecyclerAdapter splitLapRecyclerAdapter;
    private Window window;
    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StopwatchService.StopwatchBinder stopwatchBinder = (StopwatchService.StopwatchBinder) service;
            //Assign field indirectly through the repository
            viewModel.setStopwatchHelper(stopwatchBinder.stopwatchService);
            stopwatchHelper = viewModel.getStopwatchHelper();
            observeMembers();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentStopwatchBinding = FragmentStopwatchBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(StopwatchViewModel.class);
        lessThanHourFormat.setTimeZone(GMT);
        hourGreaterFormat.setTimeZone(GMT);
        window = requireActivity().getWindow();
        return fragmentStopwatchBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initClickListeners();
        Intent stopwatchServiceIntent = new Intent(requireContext(), StopwatchService.class);
        requireActivity().startService(stopwatchServiceIntent);
        requireActivity().bindService(stopwatchServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        bound = true;
        splitLapRecyclerAdapter = new SplitLapRecyclerAdapter(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        fragmentStopwatchBinding.recyclerviewSplitLapTimes.setLayoutManager(linearLayoutManager);
        fragmentStopwatchBinding.recyclerviewSplitLapTimes.setAdapter(splitLapRecyclerAdapter);
    }

    private void initClickListeners() {
        fragmentStopwatchBinding.buttonStopwatchStart.setOnClickListener(this);
        fragmentStopwatchBinding.buttonStopwatchStopResume.setOnClickListener(this);
        fragmentStopwatchBinding.buttonStopwatchLapReset.setOnClickListener(this);
    }

    private void observeMembers() {
        viewModel.getRunningTime().observe(requireActivity(), (time) -> {

            Calendar calendar = Calendar.getInstance(GMT);
            calendar.setTime(new Date(time));
            int hour = calendar.get(Calendar.HOUR);
            String formatted;
            if (hour >= 1) {
                formatted = hourGreaterFormat.format(time);
            } else {
                formatted = lessThanHourFormat.format(time);
            }
            fragmentStopwatchBinding.textviewStopwatchTime.setText(formatted);
        });
        viewModel.getLappingTime().observe(requireActivity(), (lapTime) -> {
            if(lapTime <= 0L) {
                fragmentStopwatchBinding.textviewStopwatchLapTime.setVisibility(View.GONE);
                return;
            }
            fragmentStopwatchBinding.textviewStopwatchLapTime.setVisibility(View.VISIBLE);
            Calendar calendar = Calendar.getInstance(GMT);
            calendar.setTime(new Date(lapTime));
            int hour = calendar.get(Calendar.HOUR);
            String formatted;
            if (hour >= 1) {
                formatted = hourGreaterFormat.format(lapTime);
            } else {
                formatted = lessThanHourFormat.format(lapTime);
            }
            fragmentStopwatchBinding.textviewStopwatchLapTime.setText(formatted);
        });

        viewModel.getStopwatchState().observe(requireActivity(), (state) -> {
            cachedStopwatchState = state;
            switch (state) {
                case OFF:
                    fragmentStopwatchBinding.layoutStopwatchStart.setVisibility(View.VISIBLE);
                    fragmentStopwatchBinding.layoutStopwatchStopLap.setVisibility(View.GONE);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                case RUNNING:
                    fragmentStopwatchBinding.layoutStopwatchStart.setVisibility(View.GONE);
                    fragmentStopwatchBinding.layoutStopwatchStopLap.setVisibility(View.VISIBLE);
                    fragmentStopwatchBinding.buttonStopwatchLapReset.setText("LAP");
                    fragmentStopwatchBinding.buttonStopwatchStopResume.setText("STOP");
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                case PAUSED:
                    fragmentStopwatchBinding.layoutStopwatchStart.setVisibility(View.GONE);
                    fragmentStopwatchBinding.layoutStopwatchStopLap.setVisibility(View.VISIBLE);
                    fragmentStopwatchBinding.buttonStopwatchLapReset.setText("RESET");
                    fragmentStopwatchBinding.buttonStopwatchStopResume.setText("RESUME");
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
            }
        });
        viewModel.getSplitLapTimes().observe(requireActivity(), (splitLapTimes) -> {
            if(splitLapTimes.isEmpty()) {
                fragmentStopwatchBinding.recyclerviewSplitLapTimes.setVisibility(View.GONE);
            } else {
                fragmentStopwatchBinding.recyclerviewSplitLapTimes.setVisibility(View.VISIBLE);
                ArrayList<SplitLapTime> copy = new ArrayList<>(splitLapTimes);
                Collections.reverse(copy);
                splitLapRecyclerAdapter.setSplitLapTimeList(copy);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(bound) {
            requireActivity().unbindService(serviceConnection);
            bound = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_stopwatch_start:
                stopwatchHelper.start();
                break;
            case R.id.button_stopwatch_lap_reset:
                if(cachedStopwatchState == StopwatchState.RUNNING) {
                    stopwatchHelper.lap();
                } else if(cachedStopwatchState == StopwatchState.PAUSED) {
                    stopwatchHelper.reset();
                }
                break;
            case R.id.button_stopwatch_stop_resume:
                if(cachedStopwatchState == StopwatchState.RUNNING) {
                    stopwatchHelper.stop();
                } else if(cachedStopwatchState == StopwatchState.PAUSED) {
                    stopwatchHelper.resume();
                }
        }
    }

    public static StopwatchFragment createInstance() {
        StopwatchFragment stopwatchFragment = new StopwatchFragment();
        return stopwatchFragment;
    }
}