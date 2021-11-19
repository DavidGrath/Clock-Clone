package com.example.clockclone.ui.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.clockclone.R;
import com.example.clockclone.data.TimerHelper;
import com.example.clockclone.databinding.FragmentTimerBinding;
import com.example.clockclone.domain.TimerState;
import com.example.clockclone.framework.services.TimerService;
import com.example.clockclone.ui.viewmodels.TimerViewModel;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class TimerFragment extends Fragment implements View.OnClickListener {

    private static final int HOUR = 0;
    private static final int MINUTE = 1;
    private static final int SECOND = 2;

    private FragmentTimerBinding fragmentTimerBinding;
    private TimerViewModel viewModel;
    private TimerState timerState = TimerState.OFF;
    private boolean bound = false;

    private DecimalFormat decimalFormat = new DecimalFormat("00");
    private DateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private NumberPicker.Formatter numberPickerFormatter = new NumberPicker.Formatter() {
        @Override
        public String format(int value) {
            return decimalFormat.format(value);
        }
    };
    private NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            switch (picker.getId()) {
                case R.id.timer_picker_hour:
                    viewModel.setDuration(HOUR, newVal);
                    break;
                case R.id.timer_picker_minute:
                    viewModel.setDuration(MINUTE, newVal);
                    break;
                case R.id.timer_picker_second:
                    viewModel.setDuration(SECOND, newVal);
                    break;

            }
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder timerBinder = (TimerService.TimerBinder) service;
            TimerService timerService = timerBinder.getService();
            TimerHelper timerHelper = timerService;
            viewModel.setTimerHelper(timerHelper);
            observeMembers();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTimerBinding = FragmentTimerBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(TimerViewModel.class);
        simpleDateFormat.setTimeZone(GMT);
        return fragmentTimerBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initClickListeners();
        Intent serviceIntent = new Intent(requireContext(), TimerService.class);
        requireActivity().startService(serviceIntent);
        requireActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        bound = true;
        fragmentTimerBinding.timerPickerHour.setFormatter(numberPickerFormatter);
        fragmentTimerBinding.timerPickerHour.setOnValueChangedListener(valueChangeListener);
        fragmentTimerBinding.timerPickerHour.setMaxValue(59);
        fragmentTimerBinding.timerPickerMinute.setFormatter(numberPickerFormatter);
        fragmentTimerBinding.timerPickerMinute.setOnValueChangedListener(valueChangeListener);
        fragmentTimerBinding.timerPickerMinute.setMaxValue(59);
        fragmentTimerBinding.timerPickerSecond.setFormatter(numberPickerFormatter);
        fragmentTimerBinding.timerPickerSecond.setOnValueChangedListener(valueChangeListener);
        fragmentTimerBinding.timerPickerSecond.setMaxValue(59);
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
        if (v == fragmentTimerBinding.buttonTimerStart) {
            viewModel.startTimer();
        } else if(v == fragmentTimerBinding.buttonTimerPauseResume) {
            if(timerState == TimerState.COUNTING) {
                viewModel.pauseTimer();
            } else {
                viewModel.resumeTimer();
            }
        } else if(v == fragmentTimerBinding.buttonTimerCancel) {
            viewModel.cancelTimer();
        }

    }

    private void observeMembers() {
        viewModel.isZero.observe(requireActivity(), (isZero) -> {
            fragmentTimerBinding.buttonTimerStart.setEnabled(!isZero);
        });
        viewModel.getTimerStateLiveData().observe(requireActivity(), (timerState) -> {
            this.timerState = timerState;
            switch (timerState) {
                case OFF:
                    fragmentTimerBinding.layoutTimerSetScreen.setVisibility(View.VISIBLE);
                    fragmentTimerBinding.layoutTimerCountScreen.setVisibility(View.GONE);
                    fragmentTimerBinding.buttonTimerStart.setVisibility(View.VISIBLE);
                    fragmentTimerBinding.buttonTimerPauseResume.setVisibility(View.GONE);
                    fragmentTimerBinding.buttonTimerPauseResume.setText("PAUSE");
                    fragmentTimerBinding.buttonTimerCancel.setVisibility(View.GONE);
                    break;
                case COUNTING:
                    fragmentTimerBinding.layoutTimerSetScreen.setVisibility(View.GONE);
                    fragmentTimerBinding.layoutTimerCountScreen.setVisibility(View.VISIBLE);
                    fragmentTimerBinding.buttonTimerStart.setVisibility(View.GONE);
                    fragmentTimerBinding.buttonTimerPauseResume.setVisibility(View.VISIBLE);
                    fragmentTimerBinding.buttonTimerPauseResume.setText("PAUSE");
                    fragmentTimerBinding.buttonTimerCancel.setVisibility(View.VISIBLE);
                    break;
                case PAUSED:
                    fragmentTimerBinding.layoutTimerSetScreen.setVisibility(View.GONE);
                    fragmentTimerBinding.layoutTimerCountScreen.setVisibility(View.VISIBLE);
                    fragmentTimerBinding.buttonTimerStart.setVisibility(View.GONE);
                    fragmentTimerBinding.buttonTimerPauseResume.setVisibility(View.VISIBLE);
                    fragmentTimerBinding.buttonTimerPauseResume.setText("RESUME");
                    fragmentTimerBinding.buttonTimerCancel.setVisibility(View.VISIBLE);
                    break;
            }
        });
        viewModel.getTimeLeftLiveData().observe(requireActivity(), (timeLeft) -> {
            String formatted = simpleDateFormat.format(timeLeft);
            fragmentTimerBinding.textviewTimerTimeLeft.setText(formatted);
        });
    }

    private void initClickListeners() {
        fragmentTimerBinding.buttonTimerStart.setOnClickListener(this);
        fragmentTimerBinding.buttonTimerPauseResume.setOnClickListener(this);
        fragmentTimerBinding.buttonTimerCancel.setOnClickListener(this);
    }
    public static TimerFragment createInstance() {
        TimerFragment timerFragment = new TimerFragment();
        return timerFragment;
    }
}
