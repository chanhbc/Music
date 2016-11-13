package com.chanhbc.music.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chanhbc.music.App;
import com.chanhbc.music.R;
import com.chanhbc.music.model.SongOnline;

import java.util.ArrayList;

public class MusicOnlineAdapter extends BaseAdapter {
    private ArrayList<SongOnline> songOnlines;
    private LayoutInflater inflater;

    public MusicOnlineAdapter(ArrayList<SongOnline> songOnlines) {
        this.songOnlines = songOnlines;
        inflater = LayoutInflater.from(App.getContext());
    }

    @Override
    public int getCount() {
        return songOnlines.size();
    }

    @Override
    public SongOnline getItem(int i) {
        return songOnlines.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_song, viewGroup, false);
            holder = new Holder();
            holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.tvTitle = (TextView) view.findViewById(R.id.tv_song);
            holder.tvSinger = (TextView) view.findViewById(R.id.tv_artist);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        SongOnline songOnline = songOnlines.get(i);
        holder.tvTitle.setText(songOnline.getName());
        holder.tvSinger.setText(songOnline.getArtist());
        Glide.with(viewGroup.getContext()).load(songOnline.getImageUrl()).into(holder.ivIcon);
        return view;
    }

    private class Holder {
        private ImageView ivIcon;
        private TextView tvTitle;
        private TextView tvSinger;
    }
}
