package com.chanhbc.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chanhbc.music.R;
import com.chanhbc.music.adapter.ViewPagerAdapter;
import com.chanhbc.music.model.LayoutPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ViewPagerAdapter.OnReceiveDataListener {
    private ViewPager vpMusic;
    private ViewPagerAdapter adapter;
    private ArrayList<LayoutPager> pagers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents();
    }

    private void initializeComponents() {
        vpMusic = (ViewPager) findViewById(R.id.vp_music);
        pagers = new ArrayList<>();
        pagers.add(new LayoutPager(R.layout.activity_music_offline));
        pagers.add(new LayoutPager(R.layout.activity_music_online));
        adapter = new ViewPagerAdapter(pagers);
        vpMusic.setAdapter(adapter);
        adapter.setOnReceiveDataListener(this);
    }

    @Override
    public void onReceiveData(String receiveData) {
        switch (receiveData) {
            case ViewPagerAdapter.LL1:
                switch (vpMusic.getCurrentItem()) {
                    case 0:
                        Intent intent = new Intent(this, MusicOffline.class);
                        intent.putExtra("PATH", "ALL");
                        startActivity(intent);
                        break;

                    case 1:
                        Intent intent1 = new Intent(this, MusicOnline.class);
                        intent1.putExtra("KEY", "http://mp3.zing.vn/bang-xep-hang/bai-hat-Viet-Nam/IWZ9Z08I.html");
                        startActivity(intent1);
                        break;

                    default:
                        break;
                }
                break;

            case ViewPagerAdapter.LL2:
                switch (vpMusic.getCurrentItem()) {
                    case 0:
                        Intent intent = new Intent(this, MusicOffline.class);
                        intent.putExtra("PATH", "/Download");
                        startActivity(intent);
                        break;

                    case 1:
                        Intent intent1 = new Intent(this, MusicOnline.class);
                        intent1.putExtra("KEY", "http://mp3.zing.vn/bang-xep-hang/bai-hat-Au-My/IWZ9Z0BW.html");
                        startActivity(intent1);
                        break;

                    default:
                        break;
                }
                break;

            case ViewPagerAdapter.LL3:
                switch (vpMusic.getCurrentItem()) {
                    case 0:
                        Intent intent = new Intent(this, MusicOffline.class);
                        intent.putExtra("PATH", "/Zing Mp3");
                        startActivity(intent);
                        break;

                    case 1:
                        Intent intent1 = new Intent(this, MusicOnline.class);
                        intent1.putExtra("KEY", "http://mp3.zing.vn/bang-xep-hang/bai-hat-Han-Quoc/IWZ9Z0BO.html");
                        startActivity(intent1);
                        break;

                    default:
                        break;
                }
                break;
        }
    }

}
