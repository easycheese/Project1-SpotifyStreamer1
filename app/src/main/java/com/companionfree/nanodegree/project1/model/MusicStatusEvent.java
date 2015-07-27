package com.companionfree.nanodegree.project1.model;

/**
 * Created by Kyle on 6/18/2015
 */
public class MusicStatusEvent {

    public boolean isPlaying;
    public Playlist currentPlaylist;

    public MusicStatusEvent(boolean isPlaying, Playlist currentPlaylist) {
        this.isPlaying = isPlaying;
        this.currentPlaylist = currentPlaylist;
    }
}
