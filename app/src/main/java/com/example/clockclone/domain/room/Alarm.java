package com.example.clockclone.domain.room;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Alarm {
    @Nullable
    @PrimaryKey(autoGenerate = true)
    public Integer id = null;
    public boolean enabled;
    public short timestamp;
    public String date;
    public int daysOfWeek = 0;
    @Nullable
    public String name = null;
    public int vibrationSettings;
    public int snoozeSettings;
    public String soundUri;
    public int volume;
    public boolean increasingVolume;

    public Alarm(Integer id, boolean enabled, short timestamp, String date, int daysOfWeek, String name, int vibrationSettings, int snoozeSettings, String soundUri, int volume, boolean increasingVolume) {
        this.id = id;
        this.enabled = enabled;
        this.timestamp = timestamp;
        this.date = date;
        this.daysOfWeek = daysOfWeek;
        this.name = name;
        this.vibrationSettings = vibrationSettings;
        this.snoozeSettings = snoozeSettings;
        this.soundUri = soundUri;
        this.volume = volume;
        this.increasingVolume = increasingVolume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return enabled == alarm.enabled &&
                timestamp == alarm.timestamp &&
                daysOfWeek == alarm.daysOfWeek &&
                vibrationSettings == alarm.vibrationSettings &&
                snoozeSettings == alarm.snoozeSettings &&
                volume == alarm.volume &&
                increasingVolume == alarm.increasingVolume &&
                Objects.equals(id, alarm.id) &&
                Objects.equals(date, alarm.date) &&
                Objects.equals(name, alarm.name) &&
                soundUri.equals(alarm.soundUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, enabled, timestamp, date, daysOfWeek, name, vibrationSettings, snoozeSettings, soundUri, volume, increasingVolume);
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", enabled=" + enabled +
                ", timestamp=" + timestamp +
                ", date='" + date + '\'' +
                ", daysOfWeek=" + daysOfWeek +
                ", name='" + name + '\'' +
                ", vibrationSettings=" + vibrationSettings +
                ", snoozeSettings=" + snoozeSettings +
                ", soundUri='" + soundUri + '\'' +
                ", volume=" + volume +
                ", increasingVolume=" + increasingVolume +
                '}';
    }
}
