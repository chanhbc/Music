package com.chanhbc.music.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chanhbc.music.R;
import com.chanhbc.music.adapter.MusicOnlineAdapter;
import com.chanhbc.music.manager.MusicPlayer;
import com.chanhbc.music.manager.SongOnlineManager;
import com.chanhbc.music.model.SongOnline;

import java.io.IOException;
import java.util.ArrayList;

public class MusicOnline extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final int UPDATE_BEFORE_PLAY = 0;
    private static final int UPDATE_STATE_PLAY = 1;
    private SongOnlineManager songOnlineManager;
    private ListView lvMusic;
    private SongOnline songOnline;
    private ArrayList<SongOnline> songOnlines;
    private String path;
    private MusicOnlineAdapter adapter;
    private TextView tvTimeCurrent;
    private TextView tvTimeTotal;
    private TextView tvSong;
    private TextView tvArtist;
    private SeekBar sbProTime;
    private MusicPlayer musicPlayer;
    private int time;
    private Handler handler;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_music);
        path = getIntent().getStringExtra("KEY");
        initializeComponents();
        Log.d("Handler1", "1");
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case UPDATE_BEFORE_PLAY:
                        sbProTime.setProgress(0);
                        time = mediaPlayer.getDuration();
                        sbProTime.setMax(time);
                        tvTimeTotal.setText(getTimeFormat(time));
                        break;

                    case UPDATE_STATE_PLAY:
                        tvTimeCurrent.setText(getTimeFormat(message.arg1));
                        sbProTime.setProgress(message.arg1);
                        break;

                    default:
                        Log.d("Handler1", "1");
                        break;
                }
                return false;
            }
        });
    }

    private void myHandler() {

    }

    private void loadMusic() {
        songOnlineManager.getSongOnlines(path, new SongOnlineManager.OnGetSongOnlineListener() {
            @Override
            public void completed(ArrayList<SongOnline> songOnlines) {
                MusicOnline.this.songOnlines = songOnlines;
                adapter = new MusicOnlineAdapter(songOnlines);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lvMusic.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void error(Exception e) {

            }
        });
    }

    private void initializeComponents() {
        musicPlayer = new MusicPlayer();
        songOnlineManager = new SongOnlineManager();
        songOnlines = new ArrayList<>();
        lvMusic = (ListView) findViewById(R.id.lv_listmusic);
        lvMusic.setOnItemClickListener(this);
        loadMusic();
        tvTimeCurrent = (TextView) findViewById(R.id.tv_current_time);
        tvTimeTotal = (TextView) findViewById(R.id.tv_total_time);
        tvSong = (TextView) findViewById(R.id.tv_song);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        sbProTime = (SeekBar) findViewById(R.id.sb_process_time);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.lv_listmusic:
                final SongOnline songOnline = songOnlines.get(i);
                tvArtist.setText(songOnline.getArtist());
                tvSong.setText(songOnline.getName());
                songOnlineManager.getSongURL(songOnline.getPathData(), new SongOnlineManager.OnGetSongOnlineURLSource() {
                    @Override
                    public void completed(String URLSong) {
                        playSongOnline(URLSong);
                    }

                    @Override
                    public void error(Exception e) {

                    }
                });

                break;

            default:
                break;
        }
    }

    private String getTimeFormat(long time) {
        String tm = "";
        int s;
        int m;
        int h;
        //giây
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60) {
            h = m / 60;
            m = m % 60;
            if (h > 0) {
                if (h < 10)
                    tm += "0" + h + ":";
                else
                    tm += h + ":";
            }
        }
        if (m < 10)
            tm += "0" + m + ":";
        else
            tm += m + ":";
        if (s < 10)
            tm += "0" + s;
        else
            tm += s + "";
        return tm;
    }

    private void playSongOnline(String urlSong) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                isPlaying = false;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.parse(urlSong));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    sbProTime.setProgress(0);
                    time = mediaPlayer.getDuration() / 1000;
                    sbProTime.setMax(time);
                    isPlaying = true;
                    tvTimeTotal.setText(getTimeFormat(time));
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (isPlaying) {
                                Message message = new Message();
                                message.what = UPDATE_STATE_PLAY;
                                message.arg1 = mediaPlayer.getCurrentPosition() / 1000;
                                handler.sendMessage(message);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    thread.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
