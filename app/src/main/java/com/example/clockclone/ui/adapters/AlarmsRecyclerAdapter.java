package com.example.clockclone.ui.adapters;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.clockclone.R;
import com.example.clockclone.databinding.RecyclerviewAlarmsItemBinding;
import com.example.clockclone.domain.ui.AlarmSummaryUI;
import com.example.clockclone.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmsRecyclerAdapter extends RecyclerView.Adapter<AlarmsRecyclerAdapter.AlarmsViewHolder> {

    private List<AlarmSummaryUI> alarmList = new ArrayList<>();
    public interface OnAlarmClickListener {
        void onAlarmClicked(AlarmSummaryUI alarmSummaryUI, int position);
        void onAlarmActiveToggled(AlarmSummaryUI alarmSummaryUI, boolean activeState);
    }
    private OnAlarmClickListener alarmClickListener = null;
    private final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    private final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private boolean sundayFirst;

    public AlarmsRecyclerAdapter(List<AlarmSummaryUI> alarmList) {
        this(alarmList, true, null);
    }

    public AlarmsRecyclerAdapter(List<AlarmSummaryUI> alarmList, boolean sundayFirst, OnAlarmClickListener alarmClickListener) {
        this.alarmList = alarmList;
        this.sundayFirst = sundayFirst;
        this.alarmClickListener = alarmClickListener;
    }

    public void setSundayFirst(boolean sundayFirst) {
        this.sundayFirst = sundayFirst;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public AlarmsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerviewAlarmsItemBinding binding = RecyclerviewAlarmsItemBinding.inflate(inflater, parent, false);
        return new AlarmsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AlarmsViewHolder holder, int position) {
        RecyclerviewAlarmsItemBinding binding = holder.binding;
        AlarmSummaryUI alarmSummaryUI = alarmList.get(position);
        binding.getRoot().setOnClickListener((view)-> {
            if(alarmClickListener != null) {
                alarmClickListener.onAlarmClicked(alarmSummaryUI, position);
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmSummaryUI.getTimestamp()/60);
        calendar.set(Calendar.MINUTE, alarmSummaryUI.getTimestamp() % 60);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        binding.textviewAlarmsTime.setText(timeFormat.format(calendar.getTime()));
        if(alarmSummaryUI.getName() != null) {
            binding.textviewAlarmsName.setVisibility(View.VISIBLE);
            binding.textviewAlarmsName.setText(alarmSummaryUI.getName());
        } else {
            binding.textviewAlarmsName.setVisibility(View.GONE);
            binding.textviewAlarmsName.setText("");
        }
        boolean onceOrRecurring = alarmSummaryUI.getDaysOfWeek() == 0;
        if(onceOrRecurring) {
            binding.textviewAlarmsDate.setVisibility(View.VISIBLE);
            binding.textviewAlarmsWeekDates.setVisibility(View.GONE);
            binding.textviewAlarmsDate.setText(dateFormat.format(alarmSummaryUI.getOccurrenceDate()));
        } else {
            String weekString = sundayFirst ? "S M T W T F S" : "M T W T F S S";
            SpannableString spannableString = new SpannableString(weekString);
            int spanColor = binding.getRoot().getContext().getResources().getColor(R.color.teal_200);
            int bitPos = sundayFirst ? 0 : 1;
            int i = 0;//sundayFirst ? 0 : 2; Thank you, JESUS
            for(int count = 0; count < 7; count++) {
                int day = 1 << bitPos;
                if((alarmSummaryUI.getDaysOfWeek() & day) > 0) {
                    spannableString.setSpan(new ForegroundColorSpan(spanColor), i, i + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                i = (i + 2) % 14;
                bitPos = (bitPos + 1) % 7;
            }

            binding.textviewAlarmsDate.setVisibility(View.GONE);
            binding.textviewAlarmsWeekDates.setVisibility(View.VISIBLE);
            binding.textviewAlarmsWeekDates.setText(spannableString);
        }
        binding.switchAlarmsOnOff.setChecked(alarmSummaryUI.isActivated());
        binding.switchAlarmsOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(alarmClickListener != null) {
                    alarmClickListener.onAlarmActiveToggled(alarmSummaryUI, isChecked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void setAlarmList(List<AlarmSummaryUI> alarmList) {
        this.alarmList = alarmList;
        notifyDataSetChanged();
    }

    public static class AlarmsViewHolder extends RecyclerView.ViewHolder {
        private RecyclerviewAlarmsItemBinding binding;

        public AlarmsViewHolder(RecyclerviewAlarmsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
