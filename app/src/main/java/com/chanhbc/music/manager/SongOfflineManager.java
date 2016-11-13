package com.chanhbc.music.manager;

import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.chanhbc.music.R;
import com.chanhbc.music.model.SongOffline;

import java.io.File;
import java.util.ArrayList;

public class SongOfflineManager {
    private ArrayList<SongOffline> songOfflines = new ArrayList<>();
    private ArrayList<SongOffline> allSong = new ArrayList<>();

    public SongOfflineManager() {

    }

    public ArrayList<SongOffline> getSongOfflines() {
        return songOfflines;
    }

    public void getSongOfflines(String path) {
        if (path.equals("ALL")) {
            Log.d("NULL NULL", "a");
            getSongOfflines("/Zing Mp3");
            getSongOfflines("/Download");
            getSongOfflines("/UCDownloads");
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + path);
        File[] files = file.listFiles();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (files[i].isFile() && (fileName.endsWith(".mp3") || fileName.endsWith(".flac"))) {
                mmr.setDataSource(files[i].getPath());
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String[] p = fileName.split("_");
                if (artist == null) {
                    try {
                        artist = p[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        artist = "unknown";
                    }
                }
                if (title == null) {
                    try {
                        title = p[0];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        title = fileName.substring(0, fileName.length() - 4);
                    }
                }
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                songOfflines.add(new SongOffline(title, artist, R.drawable.music_player, files[i].getAbsolutePath(), Long.parseLong(duration)));
            } else if (files[i].isDirectory()) {
                getSongOfflines(path + "/" + fileName);
            }
        }
    }
}
