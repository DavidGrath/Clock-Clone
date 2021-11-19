package com.example.clockclone.domain.ui;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Objects;

public class AlarmSummaryUI {
    private final int id;
    private final short timestamp;
    @Nullable
    private final Date occurrenceDate;
    private final int daysOfWeek;
    @Nullable
    private final String name;
    private final boolean activated;

    public AlarmSummaryUI(int id, short timestamp, @Nullable Date occurrenceDate, int daysOfWeek, @Nullable String name, boolean activated) {
        this.id = id;
        this.timestamp = timestamp;
        this.occurrenceDate = occurrenceDate;
        this.daysOfWeek = daysOfWeek;
        this.name = name;
        this.activated = activated;
    }

    public int getId() {
        return id;
    }

    public short getTimestamp() {
        return timestamp;
    }

    public Date getOccurrenceDate() {
        return occurrenceDate;
    }

    public int getDaysOfWeek() {
        return daysOfWeek;
    }

    public String getName() {
        return name;
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmSummaryUI alarmSummaryUI = (AlarmSummaryUI) o;
        return id == alarmSummaryUI.id &&
                timestamp == alarmSummaryUI.timestamp &&
                daysOfWeek == alarmSummaryUI.daysOfWeek &&
                activated == alarmSummaryUI.activated &&
                Objects.equals(occurrenceDate, alarmSummaryUI.occurrenceDate) &&
                Objects.equals(name, alarmSummaryUI.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, occurrenceDate, daysOfWeek, name, activated);
    }
}
