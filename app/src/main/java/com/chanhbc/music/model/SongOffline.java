package com.chanhbc.music.model;

public class SongOffline {
    private String song;
    private String artist;
    private int icon;
    private String data;
    private long duration;

    public SongOffline(String song, String artist, int icon, String data, long duration) {
        this.song = song;
        this.artist = artist;
        this.icon = icon;
        this.data = data;
        this.duration = duration;
    }

    public String getSong() {
        return song;
    }

    public String getArtist() {
        return artist;
    }

    public int getIcon() {
        return icon;
    }

    public String getData() {
        return data;
    }

    public long getDuration() {
        return duration;
    }
}
