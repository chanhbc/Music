package com.chanhbc.music.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chanhbc.music.App;
import com.chanhbc.music.R;
import com.chanhbc.music.adapter.MusicOfflineAdapter;
import com.chanhbc.music.manager.MusicPlayer;
import com.chanhbc.music.manager.SongOfflineManager;
import com.chanhbc.music.model.SongOffline;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class MusicOffline extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener, View.OnLongClickListener {
    private static final int UPDATE_STATE_PLAY = 0;
    private static final int NEXT_MUSIC = 1;
    // khai bao các bien
    private ListView lvListMusic;
    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivPrevious;
    private ImageView ivRepeat;
    private ImageView ivShuffle;
    private TextView tvTimeCurrent;
    private TextView tvTimeTotal;
    private TextView tvSong;
    private TextView tvArtist;
    private SeekBar sbProTime;
    private MusicOfflineAdapter adapter;
    private SongOfflineManager songOfflineManager;
    private ArrayList<SongOffline> songOfflines;
    private String path;
    private MusicPlayer musicPlayer;
    private String pathMusic;
    private SongOffline songOffline;
    private boolean isPlaying = false;
    private Handler handler;
    private int timeCurrent;
    private int repeat = 0;
    private int shuffle = 0;
    private boolean isNotOver;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_music);

        Intent intent = new Intent(MusicOffline.this, MusicPlayer.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        Log.d("onCreate","a");
        startService(intent);

        path = getIntent().getStringExtra("PATH");
        initializeComponents();
        loadControl();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case UPDATE_STATE_PLAY:
                        isNotOver = musicPlayer.playing();
                        tvTimeCurrent.setText(getTimeFormat(message.arg1));
                        sbProTime.setProgress(message.arg1);
                        break;

                    case NEXT_MUSIC:
                        isPlaying = false;
                        ivPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                        switch (repeat % 3) {
                            case 0:
                                isNotOver = false;
                                break;

                            case 1:
                                nextMusic();
                                break;

                            case 2:
                                nextMusic();
                                break;

                            default:
                                break;
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }



    //tao ket noi service
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // nhan ket qua tro ve
            Log.d("onServiceConnected","a");
            MusicPlayer.MyBinderMedia media = (MusicPlayer.MyBinderMedia) iBinder;
            musicPlayer = media.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("onServiceDisconnected","a");
        }
    };

    private void loadControl() {
        sp = (App.getContext()).getSharedPreferences("Control", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        shuffle = sp.getInt("Shuffle", 0);
        repeat = sp.getInt("Repeat", 0);
        if (shuffle == 1) {
            ivShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
        }
        if (repeat == 1) {
            ivRepeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
        } else if (repeat == 2) {
            ivRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
        }
    }

    private void loadListMusic() {
        songOfflineManager = new SongOfflineManager();
        songOfflineManager.getSongOfflines(path);
        songOfflines = songOfflineManager.getSongOfflines();
        adapter = new MusicOfflineAdapter(songOfflines);
        lvListMusic.setAdapter(adapter);
        if (songOfflines.size() != 0) {
            songOffline = songOfflines.get(0);
            pathMusic = songOffline.getData();
            musicPlayer.stop();
            musicPlayer.setup(pathMusic);
            long time = songOffline.getDuration() / 1000;
            tvArtist.setText(songOffline.getArtist());
            tvSong.setText(songOffline.getSong());
            tvTimeTotal.setText(getTimeFormat(time));
        }
    }

    private void initializeComponents() {
        // ánh xạ
        lvListMusic = (ListView) findViewById(R.id.lv_listmusic);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivPrevious = (ImageView) findViewById(R.id.iv_previous);
        ivRepeat = (ImageView) findViewById(R.id.iv_repeat);
        ivShuffle = (ImageView) findViewById(R.id.iv_shuffle);
        tvTimeCurrent = (TextView) findViewById(R.id.tv_current_time);
        tvTimeTotal = (TextView) findViewById(R.id.tv_total_time);
        tvSong = (TextView) findViewById(R.id.tv_song);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        sbProTime = (SeekBar) findViewById(R.id.sb_process_time);

        tvArtist.setSelected(true);
        tvSong.setSelected(true);

        //bắt sự kiện onclick
        ivNext.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivRepeat.setOnClickListener(this);
        ivShuffle.setOnClickListener(this);
        lvListMusic.setOnItemClickListener(this);
        sbProTime.setOnSeekBarChangeListener(this);
        ivNext.setOnLongClickListener(this);
        ivPrevious.setOnLongClickListener(this);

        musicPlayer = new MusicPlayer();
        songOfflines = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissionStorage();
        } else {
            loadListMusic();
        }
    }

    private boolean checkPermissionStorage() {
        if (ContextCompat.checkSelfPermission(MusicOffline.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicOffline.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            loadListMusic();
        }
        return true;
    }

    private void saveControl() {
        sp = (App.getContext()).getSharedPreferences("Control", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("Shuffle");
        editor.commit();
        editor.remove("Repeat");
        editor.commit();
        editor.putInt("Shuffle", shuffle);
        editor.apply();
        editor.putInt("Repeat", repeat);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        saveControl();
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == 0) {
                    loadListMusic();
                    return;
                } else if (grantResults[0] == -1) {
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_shuffle:
                shuffle = (++shuffle) % 2;
                switch (shuffle) {
                    case 0:
                        Toast.makeText(this, "Not shuffle", Toast.LENGTH_SHORT).show();
                        ivShuffle.setImageResource(R.drawable.ic_shuffle_white_24dp);
                        break;

                    case 1:
                        Toast.makeText(this, "Shuffle", Toast.LENGTH_SHORT).show();
                        ivShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                        break;

                    default:
                        break;
                }
                break;

            case R.id.iv_previous:
                if (timeCurrent >= 5) {
                    timeCurrent = 0;
                    musicPlayer.stop();
                    musicPlayer.setup(songOffline.getData());
                    sbProTime.setProgress(0);
                    playMusic();
                } else {
                    if (shuffle % 2 == 0) {
                        int index = songOfflines.indexOf(songOffline);
                        if (index > 0) {
                            songOffline = songOfflines.get(index - 1);
                        } else if (index == 0) {
                            songOffline = songOfflines.get(songOfflines.size() - 1);
                        }
                    } else {
                        songOffline = songOfflines.get((new Random()).nextInt(songOfflines.size()));
                    }
                    timeCurrent = 0;
                    musicPlayer.stop();
                    musicPlayer.setup(songOffline.getData());
                    sbProTime.setProgress(0);
                    playMusic();
                }
                Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
                break;

            case R.id.iv_play:
                if (isPlaying) {
                    pauseMusic();
                    Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
                } else {
                    if (isNotOver == false) {
                        timeCurrent = 0;
                        musicPlayer.stop();
                        musicPlayer.setup(songOffline.getData());
                        sbProTime.setProgress(0);
                        playMusic();
                        break;
                    }
                    playMusic();
                    Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iv_next:
                nextMusic();
                Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
                break;

            case R.id.iv_repeat:
                repeat = (++repeat) % 3;
                switch (repeat) {
                    case 0:
                        Toast.makeText(this, "Not repeat", Toast.LENGTH_SHORT).show();
                        ivRepeat.setImageResource(R.drawable.ic_repeat_white_24dp);
                        break;

                    case 1:
                        Toast.makeText(this, "Repeat one", Toast.LENGTH_SHORT).show();
                        ivRepeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                        break;

                    case 2:
                        Toast.makeText(this, "Repeat all", Toast.LENGTH_SHORT).show();
                        ivRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                        break;

                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }

    private void nextMusic() {
        if (repeat != 1) {
            if (shuffle % 2 == 0) {
                int index = songOfflines.indexOf(songOffline);
                if (index < songOfflines.size() - 1) {
                    songOffline = songOfflines.get(index + 1);
                } else if (index == songOfflines.size() - 1) {
                    songOffline = songOfflines.get(0);
                }
            } else {
                songOffline = songOfflines.get((new Random()).nextInt(songOfflines.size()));
            }
        }
        timeCurrent = 0;
        musicPlayer.stop();
        musicPlayer.setup(songOffline.getData());
        sbProTime.setProgress(0);
        playMusic();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.lv_listmusic:
                songOffline = adapter.getItem(i);
                timeCurrent = 0;
                musicPlayer.stop();
                musicPlayer.setup(songOffline.getData());
                sbProTime.setProgress(0);
                playMusic();
                break;

            default:
                break;
        }
    }

    private void playMusic() {
        isPlaying = true;
        pathMusic = songOffline.getData();
        final long time = songOffline.getDuration() / 1000;
        tvArtist.setText(songOffline.getArtist());
        tvSong.setText(songOffline.getSong());
        tvTimeTotal.setText(getTimeFormat(time));
        ivPlay.setImageResource(R.drawable.ic_pause_white_24dp);
        musicPlayer.play();
        isNotOver = musicPlayer.playing();
        sbProTime.setMax((int) time);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isPlaying) {
                    timeCurrent = musicPlayer.getTimeCurrent();
                    Message message = new Message();
                    message.what = UPDATE_STATE_PLAY;
                    message.arg1 = timeCurrent;
                    handler.sendMessage(message);
                    if (isNotOver == false) {
                        Message message1 = new Message();
                        message1.what = NEXT_MUSIC;
                        handler.sendMessage(message1);
                        return;
                    }
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

    private void pauseMusic() {
        isPlaying = false;
        ivPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        musicPlayer.pause();
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (timeCurrent != i && timeCurrent != 0)
            musicPlayer.seek(sbProTime.getProgress() * 1000);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.iv_previous:
                if ((timeCurrent - 5) > 0) {
                    musicPlayer.seek((timeCurrent - 5) * 1000);
                }
                break;

            case R.id.iv_next:
                if ((timeCurrent + 5) < songOffline.getDuration()) {
                    musicPlayer.seek((timeCurrent + 5) * 1000);
                }
                break;

            default:
                break;
        }
        return true;
    }
}
