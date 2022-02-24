package com.example.clockclone.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.example.clockclone.R;
import com.example.clockclone.databinding.ActivityAlarmSoundVolumeBinding;
import com.example.clockclone.domain.ui.AlarmSoundUI;
import com.example.clockclone.domain.ui.SoundVolumeUI;
import com.example.clockclone.ui.adapters.AlarmSoundAdapter;
import com.example.clockclone.ui.viewmodels.AlarmSoundVolumeViewModel;
import com.example.clockclone.ui.viewmodels.factories.AlarmSoundVolumeViewModelFactory;
import com.example.clockclone.util.Constants;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmSoundVolumeActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAlarmSoundVolumeBinding binding;
    private AlarmSoundVolumeViewModel viewModel;
    private Ringtone selectedRingtone;
    //Stop after 5 seconds
    private Handler ringtonePreviewHandler;
    private int maxVolume;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private long[] mutePattern = new long[]{0, 500};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmSoundVolumeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String title = intent.getStringExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_TITLE_INPUT);
        Uri uri = intent.getParcelableExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_URI_INPUT);
        int initialVolume = intent.getIntExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_VOLUME_INPUT, 0);
        AlarmSoundUI alarmSoundUI = new AlarmSoundUI(title, uri);
        SoundVolumeUI soundVolumeUI = new SoundVolumeUI(alarmSoundUI, initialVolume);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        ringtonePreviewHandler = new Handler(Looper.getMainLooper());
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = ringtoneManager.getCursor();
        int selectedIndex = 0;
        Uri alarmUriNoQuery = alarmSoundUI.getUri().buildUpon()
                .clearQuery().build();
        ArrayList<AlarmSoundUI> alarmSounds = new ArrayList<>();
        int index = 0;
        while(cursor.moveToNext()) {
            String alarmTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            int id = cursor.getInt(RingtoneManager.ID_COLUMN_INDEX);
            Uri alarmUri = Uri.parse(cursor.getString(RingtoneManager.URI_COLUMN_INDEX)).buildUpon()
                    .appendPath(Integer.toString(id)).build();
            if(alarmUri.equals(alarmUriNoQuery)) {
                selectedIndex = index;
            }
            AlarmSoundUI alarmSound = new AlarmSoundUI(alarmTitle, alarmUri);
            alarmSounds.add(alarmSound);
            index++;
        }

        viewModel = new AlarmSoundVolumeViewModelFactory(soundVolumeUI, alarmSounds).create(AlarmSoundVolumeViewModel.class);
        ListAdapter alarmSoundsAdapter = new AlarmSoundAdapter(alarmSounds);
        binding.listviewAlarmSounds.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        binding.listviewAlarmSounds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlarmSoundUI alarmSound = alarmSounds.get(position);
                viewModel.setSelectedAlarmSound(alarmSound);
                if(selectedRingtone != null && selectedRingtone.isPlaying()) {
                    selectedRingtone.stop();
                    ringtonePreviewHandler.removeCallbacksAndMessages(null);
                }
                selectedRingtone = RingtoneManager.getRingtone(AlarmSoundVolumeActivity.this, alarmSound.getUri());
                selectedRingtone.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build());
                selectedRingtone.play();
                ringtonePreviewHandler.postDelayed(() -> {
                    if(selectedRingtone.isPlaying()) {
                        selectedRingtone.stop();
                    }
                }, 5_000L);
            }
        });
        binding.listviewAlarmSounds.setAdapter(alarmSoundsAdapter);
        binding.listviewAlarmSounds.setItemChecked(selectedIndex, true);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, initialVolume, 0);
        binding.seekbarAlarmVolume.setMax(maxVolume);
        binding.seekbarAlarmVolume.setOnSeekBarChangeListener(new SeekBarListenerStub() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0){
                    binding.imageviewSoundVolumeSpeaker.setImageResource(R.drawable.ic_baseline_volume_off_24);
                    vibrator.cancel();
                    vibrator.vibrate(mutePattern, -1);
                } else {
                    binding.imageviewSoundVolumeSpeaker.setImageResource(R.drawable.ic_baseline_volume_up_24);
                }
                //fromUser sounds a bit confusing in this context
                boolean programmatically = !fromUser;
                viewModel.setVolume(progress, programmatically);
                if(fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                }

            }
        });
        binding.buttonAlarmSoundVolumeDone.setOnClickListener(this);
        viewModel.getVolumeLiveData().observe(this, (volume) -> {
            binding.seekbarAlarmVolume.setProgress(volume);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ringtonePreviewHandler.removeCallbacksAndMessages(null);
        if(selectedRingtone != null && selectedRingtone.isPlaying()) {
            selectedRingtone.stop();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == binding.buttonAlarmSoundVolumeDone) {
            Intent finish = new Intent();
            SoundVolumeUI soundVolumeUI = viewModel.getSoundVolumeUI();
            finish.putExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_TITLE_RESULT, soundVolumeUI.getSelectedAlarmSound().getTitle());
            finish.putExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_URI_RESULT, soundVolumeUI.getSelectedAlarmSound().getUri());
            finish.putExtra(Constants.Extras.Titles.SOUND_VOLUME_ACTIVITY_SOUND_VOLUME_RESULT, soundVolumeUI.getVolume());
            setResult(RESULT_OK, finish);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(viewModel.getVolume() - 1 >= 0) {
                    viewModel.setVolume(viewModel.getVolume() - 1, true);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(viewModel.getVolume() + 1 <= maxVolume) {
                    viewModel.setVolume(viewModel.getVolume() + 1, true);
                }
                return true;
        }
        return false;
    }
}

abstract class SeekBarListenerStub implements SeekBar.OnSeekBarChangeListener {
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}