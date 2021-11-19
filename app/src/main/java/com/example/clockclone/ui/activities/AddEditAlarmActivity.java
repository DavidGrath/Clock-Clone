package com.example.clockclone.ui.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.example.clockclone.databinding.ActivityAddEditAlarmBinding;
import com.example.clockclone.domain.ui.AlarmFormUI;
import com.example.clockclone.domain.ui.AlarmSoundUI;
import com.example.clockclone.domain.ui.SoundVolumeUI;
import com.example.clockclone.ui.viewmodels.AddEditAlarmViewModel;
import com.example.clockclone.ui.viewmodels.factories.AddEditAlarmViewModelFactory;
import com.example.clockclone.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Calendar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class AddEditAlarmActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private ActivityAddEditAlarmBinding binding;
    private AddEditAlarmViewModel viewModel;
    private AddEditAlarmActivity activity;
    private AudioManager audioManager;
    private SharedPreferences preferences;

    private DecimalFormat decimalFormat = new DecimalFormat("00");
    private NumberPicker.Formatter numberPickerFormatter = new NumberPicker.Formatter() {
        @Override
        public String format(int value) {
            return decimalFormat.format(value);
        }
    };
    private ActivityResultContract<Integer, Integer> snoozeContract = new ActivityResultContract<Integer, Integer>() {
        @NonNull
        @NotNull
        @Override
        public Intent createIntent(@NonNull @NotNull Context context, Integer input) {
            Intent intent = new Intent(activity, AlarmSnoozeActivity.class);
            intent.putExtra(Constants.Extras.Titles.SNOOZE_ACTIVITY_VALUE, input);
            return intent;
        }

        @Override
        public Integer parseResult(int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent intent) {
            if (intent == null || resultCode != RESULT_OK) {
                return -1;
            }
            Integer result = intent.getIntExtra(Constants.Extras.Titles.SNOOZE_ACTIVITY_VALUE, 0);
            return result;
        }
    };
    private ActivityResultLauncher<Integer> snoozeLauncher;

    ActivityResultCallback<Integer> snoozeCallback = new ActivityResultCallback<Integer>() {
        @Override
        public void onActivityResult(Integer result) {
            if (result != -1) {
                int enabledMask = result & Constants.Snooze.SNOOZE_MASK_ENABLED;
                boolean enabled = (enabledMask & Constants.Snooze.SNOOZE_FLAG_ENABLED) > 0;
                int interval = result & Constants.Snooze.SNOOZE_MASK_INTERVAL;
                int repeat = result & Constants.Snooze.SNOOZE_MASK_REPEAT;
                viewModel.setAlarmSnooze(enabled, interval, repeat);
            }
        }
    };

    private ActivityResultContract<SoundVolumeUI, SoundVolumeUI> soundVolumeContract = new ActivityResultContract<SoundVolumeUI, SoundVolumeUI>() {
        @NonNull
        @NotNull
        @Override
        public Intent createIntent(@NonNull @NotNull Context context, SoundVolumeUI input) {
            Intent intent = new Intent(context, AlarmSoundVolumeActivity.class);
            intent.putExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_TITLE_INPUT, input.getSelectedAlarmSound().getTitle());
            intent.putExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_URI_INPUT, input.getSelectedAlarmSound().getUri());
            intent.putExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_VOLUME_INPUT, input.getVolume());
            return intent;
        }

        @Override
        public SoundVolumeUI parseResult(int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent intent) {
            if (intent == null || resultCode != RESULT_OK) {
                return null;
            }
            String name = intent.getStringExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_TITLE_RESULT);
            Uri uri = (Uri) intent.getParcelableExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_URI_RESULT);
            int volume = intent.getIntExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_VOLUME_RESULT, audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
            SoundVolumeUI soundVolumeUI = new SoundVolumeUI(new AlarmSoundUI(name, uri), volume);
            return soundVolumeUI;
        }
    };

    private ActivityResultLauncher<SoundVolumeUI> soundVolumeLauncher;

    private ActivityResultCallback<SoundVolumeUI> soundVolumeCallback = new ActivityResultCallback<SoundVolumeUI>() {
        @Override
        public void onActivityResult(SoundVolumeUI result) {
            if (result != null) {
                viewModel.setAlarmSoundVolume(result.getSelectedAlarmSound().getTitle(), result.getSelectedAlarmSound().getUri(), result.getVolume());
            }
        }
    };

    private ActivityResultContract<Integer, Integer> vibrationContract = new ActivityResultContract<Integer, Integer>() {
        @NonNull
        @NotNull
        @Override
        public Intent createIntent(@NonNull @NotNull Context context, Integer input) {
            Intent intent = new Intent(context, AlarmVibrationActivity.class);
            intent.putExtra(Constants.Extras.Titles.VIBRATION_ACTIVITY_INPUT, input);
            return intent;
        }

        @Override
        public Integer parseResult(int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent intent) {
            if (resultCode != RESULT_OK || intent == null) {
                return -1;
            }
            int result = intent.getIntExtra(Constants.Extras.Titles.VIBRATION_ACTIVITY_OUTPUT, -1);
            return result;
        }
    };

    private ActivityResultLauncher<Integer> vibrationLauncher;

    private ActivityResultCallback<Integer> vibrationCallback = new ActivityResultCallback<Integer>() {
        @Override
        public void onActivityResult(Integer result) {
            if (result != -1) {
                int flagsMask = result & Constants.Vibrate.VIBRATION_MASK_FLAGS;
                boolean enabled = (flagsMask & Constants.Vibrate.VIBRATION_FLAG_ENABLED) > 0;
                int patternID = result & Constants.Vibrate.VIBRATION_MASK_PATTERN_ID;
                viewModel.setAlarmVibration(enabled, patternID);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        Intent intent = getIntent();
        int alarmID = intent.getIntExtra(Constants.Extras.Titles.EDIT_ALARM_ID, -1);

        int snoozeSettings = Constants.Snooze.SNOOZE_FLAG_ENABLED | Constants.Snooze.SNOOZE_INTERVAL_30_MINUTES | Constants.Snooze.SNOOZE_REPEAT_5_TIMES;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Uri defaultAlarmUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(this, defaultAlarmUri);
        int volumeCurrent = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        int vibrationSettings = Constants.Vibrate.VIBRATION_FLAG_ENABLED | Constants.Vibrate.VIBRATION_WALTZ;
        preferences = getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
        boolean sundayFirst = preferences.getBoolean(Constants.Preferences.SUNDAY_FIRST, true);
        viewModel = new AddEditAlarmViewModelFactory(getApplication(), alarmID, snoozeSettings, volumeCurrent, ringtone.getTitle(this), defaultAlarmUri, vibrationSettings, sundayFirst).create(AddEditAlarmViewModel.class);
        viewModel.getAlarmFormUI().observe(this, new Observer<AlarmFormUI>() {
            @Override
            public void onChanged(AlarmFormUI alarmFormUI) {
                binding.setAlarmFormUI(alarmFormUI);
                viewModel.getAlarmFormUI().removeObserver(this);
            }
        });

        binding.buttonAddEditAlarmCancel.setOnClickListener(this);
        binding.buttonAddEditAlarmSave.setOnClickListener(this);
        binding.textviewAddEditAlarmChangeDate.setOnClickListener(this);
        binding.layoutAlarmName.setOnClickListener(this);
        binding.layoutSnooze.setOnClickListener(this);
        binding.layoutSoundVolume.setOnClickListener(this);
        binding.layoutVibration.setOnClickListener(this);

        binding.numberpickerHour.setFormatter(numberPickerFormatter);
        binding.numberpickerHour.setMaxValue(23);
        binding.numberpickerHour.setWrapSelectorWheel(false);

        binding.numberpickerMinute.setFormatter(numberPickerFormatter);
        binding.numberpickerMinute.setMaxValue(59);
        binding.numberpickerMinute.setWrapSelectorWheel(false);

        binding.numberpickerAmPm.setMaxValue(1);
        binding.numberpickerAmPm.setDisplayedValues(new String[]{"AM", "PM"});
        binding.numberpickerAmPm.setWrapSelectorWheel(false);

        snoozeLauncher = registerForActivityResult(snoozeContract, snoozeCallback);
        soundVolumeLauncher = registerForActivityResult(soundVolumeContract, soundVolumeCallback);
        vibrationLauncher = registerForActivityResult(vibrationContract, vibrationCallback);
    }
    @Override
    public void onClick(View v) {
        if (v == binding.buttonAddEditAlarmCancel) {
            finish();
        } else if (v == binding.buttonAddEditAlarmSave) {
            LiveData<?> added = viewModel.setAlarm();
            //Simple one-off pattern
            added.observe(this, new Observer<Object>() {
                @Override
                public void onChanged(Object o) {
                    added.removeObserver(this);
                    finish();
                }
            });
        } else if (v == binding.textviewAddEditAlarmChangeDate) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, dayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        } else if (v == binding.layoutAlarmName) {
            EditText editText = new EditText(this);
            editText.setText(viewModel.getAlarmName() != null ? viewModel.getAlarmName() : "");
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Set alarm name")
                    .setView(editText)
                    .setPositiveButton("Set", (dialog, which) -> {
                        String text = editText.getText().toString();
                        if (text.isEmpty()) {
                            viewModel.setAlarmName(null);
                        } else {
                            viewModel.setAlarmName(text);
                        }

                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (v == binding.layoutSnooze) {
            snoozeLauncher.launch(viewModel.getAlarmSnooze());
        } else if (v == binding.layoutSoundVolume) {
            soundVolumeLauncher.launch(viewModel.getAlarmSoundVolume());
        } else if (v == binding.layoutVibration) {
            vibrationLauncher.launch(viewModel.getVibrationSettings());
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        viewModel.changeAlarmDate(calendar.getTime());
    }
}
