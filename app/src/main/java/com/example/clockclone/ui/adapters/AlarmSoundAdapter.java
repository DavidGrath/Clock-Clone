package com.example.clockclone.ui.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.example.clockclone.domain.ui.AlarmSoundUI;

import java.util.List;

public class AlarmSoundAdapter extends BaseAdapter {

    private List<AlarmSoundUI> alarmSounds;

    public AlarmSoundAdapter(List<AlarmSoundUI> alarmSounds) {
        this.alarmSounds = alarmSounds;
    }

    @Override
    public int getCount() {
        return alarmSounds.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmSounds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RadioButton radioButton = new RadioButton(parent.getContext());
        AlarmSoundUI alarmSound = alarmSounds.get(position);
        radioButton.setText(alarmSound.getTitle());
        //Use this to enable OnItemClick, that took a while
        radioButton.setFocusable(false);
        radioButton.setClickable(false);
        radioButton.setFocusableInTouchMode(false);
        return radioButton;
    }
}
