package com.companionfree.nanodegree.project1.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.model.CustomTrack;

import java.io.IOException;

/**
 * Created by Kyle on 6/13/2015
 */
public class PlaybackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener{
        public static final String ACTION_PLAY = "com.companionfree.nanodegree.project1.action.PLAY";
        MediaPlayer mMediaPlayer = null;
        private static final int NOTIFICATION_ID = 355;

    // TODO Handling the AUDIO_BECOMING_NOISY Intent

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_PLAY)) {

            String trackUrl = intent.getStringExtra(CustomTrack.SONG_URL);
            Log.d("Spotify", "Song url: " + trackUrl);

            mMediaPlayer = new MediaPlayer(); // initialize it here
            mMediaPlayer.setOnPreparedListener(this);

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(trackUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
            WifiManager.WifiLock wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

            wifiLock.acquire();


            mMediaPlayer.prepareAsync(); // TODO prepare async to not block main thread


            String songName;
            // assign the song name to songName
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), intent.getClass()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(getApplicationContext());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                builder.addAction(R.mipmap.ic_launcher, "first", null);
                builder.addAction(R.mipmap.ic_launcher, "second", null);
                builder.addAction(R.mipmap.ic_launcher, "third", null);
            }

            Notification notification = builder.build();
            notification.tickerText = "text";
            notification.icon = R.mipmap.ic_launcher;

            Notification.Action[] actions = new Notification.Action[2];

            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.setLatestEventInfo(getApplicationContext(), "MusicPlayerSample",
                    "Playing: " + "name", pi);
            startForeground(NOTIFICATION_ID, notification);




            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // could not get audio focus.
            }

        }


        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** Called when MediaPlayer is ready */
        public void onPrepared(MediaPlayer player) {
            Log.d("Spotify", "MediaPlayer Starting");
            player.start();
        }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        Log.d("Spotify", "MediaPlayer Error, resetting");
        mp.reset();
        return false;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        // Do something based on focus change...
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
//                if (mMediaPlayer == null) initMediaPlayer(); TODO
//                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
//                mMediaPlayer.setVolume(1.0f, 1.0f);
//                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }
}
