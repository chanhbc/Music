package com.chanhbc.music.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chanhbc.music.App;
import com.chanhbc.music.R;
import com.chanhbc.music.model.SongOffline;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.function.BiPredicate;

public class MusicOfflineAdapter extends BaseAdapter {
    private ArrayList<SongOffline> songOfflines;
    private LayoutInflater inflater;

    public MusicOfflineAdapter(ArrayList<SongOffline> songOfflines) {
        this.songOfflines = songOfflines;
        inflater = LayoutInflater.from(App.getContext());
    }

    @Override
    public int getCount() {
        return songOfflines.size();
    }

    @Override
    public SongOffline getItem(int i) {
        return songOfflines.get(i);
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
            holder.tvSong = (TextView) view.findViewById(R.id.tv_song);
            holder.tvArtist = (TextView) view.findViewById(R.id.tv_artist);
            holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.tvSong.setText(songOfflines.get(i).getSong());
        holder.tvSong.setSelected(true);
        holder.tvArtist.setText(songOfflines.get(i).getArtist());
        holder.tvArtist.setSelected(true);
        holder.ivIcon.setImageResource(songOfflines.get(i).getIcon());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songOfflines.get(i).getData());
        byte[] data = mmr.getEmbeddedPicture();
        if (data != null && data.length != 0) {
            //Bitmap bitmap1 = BitmapFactory.decodeStream(new ByteArrayInputStream(mmr.getEmbeddedPicture()));
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.ivIcon.setImageBitmap(bitmap);
        }
        return view;
    }

    private class Holder {
        private TextView tvSong;
        private TextView tvArtist;
        private ImageView ivIcon;
    }
}
