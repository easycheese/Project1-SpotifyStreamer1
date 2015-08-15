package com.companionfree.nanodegree.project1.model;

/**
 * Created by Kyle on 6/18/2015
 */
public class MusicStatusEvent {

    public boolean isPlaying;
    public Playlist currentPlaylist;
    public boolean isSkipping;

    public MusicStatusEvent(boolean isPlaying, Playlist currentPlaylist, boolean isSkipping) {
        this.isPlaying = isPlaying;
        this.currentPlaylist = currentPlaylist;
        this.isSkipping = isSkipping;
    }
}
