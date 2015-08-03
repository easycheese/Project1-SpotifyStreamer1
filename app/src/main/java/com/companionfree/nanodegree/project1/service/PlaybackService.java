package com.companionfree.nanodegree.project1.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.PlayerFragment;
import com.companionfree.nanodegree.project1.fragment.SettingsFragment;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.companionfree.nanodegree.project1.model.MusicStatusEvent;
import com.companionfree.nanodegree.project1.model.Playlist;
import com.companionfree.nanodegree.project1.util.SpotifyMediaPlayer;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Created by Kyle on 6/13/2015
 */
public class PlaybackService extends Service implements SpotifyMediaPlayer.OnPreparedListener,
        SpotifyMediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener {

    public static final String ACTION_PLAY = "com.companionfree.nanodegree.project1.action.PLAY";
    public static final String ACTION_PREV = "com.companionfree.nanodegree.project1.action.PREV";
    public static final String ACTION_NEXT = "com.companionfree.nanodegree.project1.action.NEXT";

    private static final int NOTIFICATION_ID = 355;
    SpotifyMediaPlayer mMediaPlayer = null;
    private Playlist playList;

    private Bitmap largeIcon;

    private String WIFI_LOCK_TAG = "wifiLock";
    private String MEDIA_SESSION_TAG = "mediaSessionTag";

    // TODO Handling the AUDIO_BECOMING_NOISY Intent
    // TODO make dismissable notification on pause

    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle bundle = intent.getExtras();

        boolean newPlaylistData = false;

        if (bundle != null) {
            Playlist newPlaylist = bundle.getParcelable(PlayerFragment.PLAYLIST);

            if (playList == null) {
                Log.d(getClass().getSimpleName(), "Starting new session");
                playList = newPlaylist;
            }

            if (!playList.getCurrentTrack().id.equals(newPlaylist.getCurrentTrack().id)) { //TODO null
                Log.d(getClass().getSimpleName(), "Stopping song, new playlist loaded");
                playList = newPlaylist;
                newPlaylistData = true;
                mMediaPlayer.stop();
            }

            Log.d(getClass().getSimpleName(), "Song url: " + playList.getCurrentTrack().trackName);
        }

//        if (mMediaPlayer == null || existingPlaylistOverwritten) {
        if (mMediaPlayer == null || newPlaylistData) {

            Log.d(getClass().getSimpleName(), "Setting up new service");
            setupService();
        }

        handleAction(intent.getAction());

        return START_NOT_STICKY;
    }

    private void handleAction(String action) {
        switch (action) {
            case ACTION_PLAY:
                boolean playing = true;
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    playing = false;
                } else if (mMediaPlayer.isPaused()) {
                    mMediaPlayer.start();

                } else {
                    mMediaPlayer.prepareAsync();// TODO re-enable if not
                }

                EventBus.getDefault().post(new MusicStatusEvent(playing, playList));
                break;
            case ACTION_NEXT:
                if (mMediaPlayer != null) {
                    playList.skipNext();
                    playNewTrack();
                }
                break;
            case ACTION_PREV:
                if (mMediaPlayer != null) {
                    playList.skipPrevious();
                    playNewTrack();
                }
                break;
        }
        setNotification();
    }
    private void playNewTrack() {
        largeIcon = null;
        setupService();
        handleAction(ACTION_PLAY);
    }

    private void setupService() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new SpotifyMediaPlayer();
        } else {
            mMediaPlayer.reset();
        }

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(playList.getCurrentTrack().previewURL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        WifiManager.WifiLock wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_TAG);

        wifiLock.acquire();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // could not get audio focus.
        }


    }
    private void setNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showNotificationControls = prefs.getBoolean(SettingsFragment.PREF_NOTIFICATION, true);


        // assign the song name to songName
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), getClass()),
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        int playDrawable = mMediaPlayer.isPlaying() ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_36dp;

        if (showNotificationControls) {
            builder.addAction(R.drawable.ic_skip_previous_black_36dp, null, getPendingIntent(ACTION_PREV));
            builder.addAction(playDrawable, null, getPendingIntent(ACTION_PLAY));
            builder.addAction(R.drawable.ic_skip_next_black_36dp, null, getPendingIntent(ACTION_NEXT));
        }


        if (largeIcon != null) {
            builder.setLargeIcon(largeIcon);
        } else {
            Glide.with(this)
                    .load(playList.getCurrentTrack().albumURL)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(
                            R.dimen.notification_large_icon_width, R.dimen.notification_large_icon_height) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            largeIcon = bitmap;
                            setNotification();
                        }
                    });
        }


        MediaSessionCompat compat = new MediaSessionCompat(this, MEDIA_SESSION_TAG, null, null); // need tag

        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        style.setMediaSession(compat.getSessionToken())
                .setShowCancelButton(true)
        ;

        builder.setStyle(style);


        CustomTrack track = playList.getCurrentTrack();
        String songDescription = track.trackName + " by " + track.artistName;

        builder.setContentTitle(songDescription);
        Notification notification = builder.build();

        notification.icon = R.mipmap.ic_launcher;


        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(),
                track.trackName,
                "by " + track.artistName, pi);

        startForeground(NOTIFICATION_ID, notification); // TODO Make notifaction cancelable somehow

    }

    private PendingIntent getPendingIntent(String action) {
        Intent i = new Intent(getApplicationContext(), getClass());
        i.setAction(action);
        return PendingIntent.getService(getApplicationContext(), 0,
                i, 0);
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
            setNotification();
            EventBus.getDefault().post(new MusicStatusEvent(true, playList)); // TODO only necessary if first song?
        }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        Log.d("Spotify", "MediaPlayer Error, resetting");
        mp.reset();
        EventBus.getDefault().post(new MusicStatusEvent(false, playList));
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


    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(getClass().getSimpleName(), "music stopped");
        EventBus.getDefault().post(new MusicStatusEvent(false, playList));
    }
}
