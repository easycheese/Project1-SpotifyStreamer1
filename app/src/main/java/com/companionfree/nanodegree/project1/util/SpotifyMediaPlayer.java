package com.companionfree.nanodegree.project1.util;

import android.media.MediaPlayer;

import com.companionfree.nanodegree.project1.model.MusicStatusTimeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by Kyle on 6/19/2015
 */
public class SpotifyMediaPlayer extends MediaPlayer {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture beeperhandle;


    @Override
    public void start() throws IllegalStateException {
        super.start();

        final Runnable seekBarCheck = new Runnable() {
            public void run() {
                EventBus.getDefault().post(new MusicStatusTimeEvent(getCurrentPosition()));
            }
        };

        beeperhandle = scheduler.scheduleAtFixedRate(seekBarCheck, 100, 100, TimeUnit.MILLISECONDS);

    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        beeperhandle.cancel(true);
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        beeperhandle.cancel(true);
    }

    public boolean isPaused() {
        return getCurrentPosition() != 0;
    }

}
