package com.example.clockclone.domain.ui;

import android.net.Uri;

import java.util.Objects;

public class AlarmSoundUI {
    private final String title;
    private final Uri uri;

    public AlarmSoundUI(String title, Uri uri) {
        this.title = title;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmSoundUI that = (AlarmSoundUI) o;
        return title.equals(that.title) &&
                uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, uri);
    }
}
