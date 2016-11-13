package com.chanhbc.music.model;

public class SongOnline {
    private String pathData;
    private String name;
    private String artist;
    private String imageUrl;

    public SongOnline(String pathData, String name, String artist, String imageUrl) {
        this.    pathData = pathData;
        this.name = name;
        this.artist = artist;
        this.imageUrl = imageUrl;
    }

    public String getPathData() {
        return     pathData;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
