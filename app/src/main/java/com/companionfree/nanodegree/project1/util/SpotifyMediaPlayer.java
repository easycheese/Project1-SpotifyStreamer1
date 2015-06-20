package com.companionfree.nanodegree.project1.util;

import android.media.MediaPlayer;

/**
 * Created by Kyle on 6/19/2015.
 */
public class SpotifyMediaPlayer extends MediaPlayer {

    public boolean isPaused() {
        return getCurrentPosition() != 0;
    }

}
