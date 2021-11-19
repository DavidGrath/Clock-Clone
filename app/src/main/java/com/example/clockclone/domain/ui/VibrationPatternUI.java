package com.example.clockclone.domain.ui;

public class VibrationPatternUI {
    private final String title;
    private final long[] pattern;

    public VibrationPatternUI(String title, long[] pattern) {
        this.title = title;
        this.pattern = pattern;
    }

    public String getTitle() {
        return title;
    }

    public long[] getPattern() {
        return pattern;
    }
}
