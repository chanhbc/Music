package com.chanhbc.music.manager;

import android.util.Log;

import com.chanhbc.music.model.SongOnline;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SongOnlineManager {
    private static final String SONG_JSON_URL = "http://mp3.zing.vn/html5xml/song-xml/";
    //private static final String SONG_XML_URL = "http://mp3.zing.vn/xml/song-xml/";

    public SongOnlineManager() {

    }

    public void getSongOnlines(final String url, final OnGetSongOnlineListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<SongOnline> songOnlines = new ArrayList<>();
                    Document document = Jsoup.connect(url).get();
                    Element element = document.select("div.table-body").first();
                    Elements items = element.select("li");
                    for (int i = 0; i < items.size(); i++) {
                        String dataCode = items.get(i).attr("data-code");
                        Element item = items.get(i).select("div.e-item").first();
                        String imageUrl = item.select("img").attr("src");
                        String name = item.select("h3").first().select("a").attr("title");
                        String artist = item.select("div.inblock.ellipsis").text();
                        SongOnline songOnline = new SongOnline(dataCode, name, artist, imageUrl);
                        songOnlines.add(songOnline);
                    }
                    listener.completed(songOnlines);
                } catch (IOException e) {
                    listener.error(e);
                }
            }
        }).start();
    }

    public void getSongURL(final String dataCode, final OnGetSongOnlineURLSource listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(SONG_JSON_URL + dataCode).ignoreContentType(true).get();
                    String text = document.text();
                    Gson gson = new Gson();
                    SongOnlineDataSource dataSource = (new Gson()).fromJson(text, SongOnlineDataSource.class);
                    String urlSource = dataSource.getData().get(0).getSourceList();
                    listener.completed(urlSource);
                } catch (IOException e) {
                    listener.error(e);
                }
            }
        }).start();

    }

    private class SongOnlineDataSource {
        @SerializedName("data")
        private List<SourceList> data;

        private class SourceList {
            @SerializedName("source_list")
            private String[] sourceList;

            public String getSourceList() {
                return "http://" + sourceList[1];
            }
        }

        public List<SourceList> getData() {
            return data;
        }
    }

    public interface OnGetSongOnlineURLSource {
        void completed(String URLSong);

        void error(Exception e);
    }

    public interface OnGetSongOnlineListener {
        void completed(ArrayList<SongOnline> songOnlines);

        void error(Exception e);
    }

}
