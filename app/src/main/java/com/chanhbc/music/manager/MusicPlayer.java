package com.chanhbc.music.manager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicPlayer extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {
    private static final int PLAYER_IDLE = -1;
    private static final int PLAYER_PLAY = 1;
    private static final int PLAYER_PAUSE = 2;
    private MediaPlayer mediaPlayer;
    private int state;

    public MusicPlayer() {
        Log.d("onStart..", "aaa");
    }

    @Override
    public void onCreate() {
        Log.d("onCreate1", "aa");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinderMedia();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand..", "aaa");
        return START_STICKY;
    }

    public MusicPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void setup(String path) {
        try {
            state = PLAYER_IDLE;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupOnline(Context context, String path) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, Uri.parse(path));
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playSongOnline(Context context, String urlSong) {
        Log.d("AHIHI", urlSong);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, Uri.parse(urlSong));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getTimeTotal() {
        return mediaPlayer.getDuration() / 1000;
    }

    public void play() {
        if (state == PLAYER_IDLE || state == PLAYER_PAUSE) {
            state = PLAYER_PLAY;
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (state == PLAYER_PLAY || state == PLAYER_PAUSE) {
            state = PLAYER_IDLE;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void pause() {
        if (state == PLAYER_PLAY) {
            mediaPlayer.pause();
            state = PLAYER_PAUSE;
        }
    }

    public int getTimeCurrent() {
        if (state != PLAYER_IDLE) {
            return mediaPlayer.getCurrentPosition() / 1000;
        } else
            return 0;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void seek(int time) {
        mediaPlayer.seekTo(time);
    }

    public boolean playing() {
        return mediaPlayer.isPlaying();
    }

    public void loop(boolean loop) {
        mediaPlayer.setLooping(loop);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        Log.d("Percent", "onBufferingUpdate persent: " + i);
    }

    public class MyBinderMedia extends Binder {
        public MusicPlayer getService() {
            return MusicPlayer.this;
        }
    }
}
