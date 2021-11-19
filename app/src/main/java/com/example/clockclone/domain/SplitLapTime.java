package com.example.clockclone.domain;

public class SplitLapTime {

    private int index;
    private long splitTime;
    private long lapTime;

    public SplitLapTime(int index, long splitTime, long lapTime) {
        this.index = index;
        this.splitTime = splitTime;
        this.lapTime = lapTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getSplitTime() {
        return splitTime;
    }

    public void setSplitTime(long splitTime) {
        this.splitTime = splitTime;
    }

    public long getLapTime() {
        return lapTime;
    }

    public void setLapTime(long lapTime) {
        this.lapTime = lapTime;
    }
}
