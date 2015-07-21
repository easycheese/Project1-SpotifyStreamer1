package com.companionfree.nanodegree.project1.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.companionfree.nanodegree.project1.model.MusicStatusEvent;
import com.companionfree.nanodegree.project1.model.MusicStatusTimeEvent;
import com.companionfree.nanodegree.project1.model.Playlist;
import com.companionfree.nanodegree.project1.service.PlaybackService;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by Kyle on 6/6/2015
 */
public class PlayerFragment extends DialogFragment implements View.OnClickListener{

    public static final String PLAYLIST = "playlist";

    protected String resultsSave = "currentTrack";

    private Playlist playList;
    private CustomTrack currentTrack;
    private SpotifyService spotifyService;
    private AsyncTask searchTask;

    @InjectView(R.id.loading_bar) ProgressBar loadingBar;
    @InjectView(R.id.song_image) ImageView albumImage;
    @InjectView(R.id.song_progress) SeekBar progressBar;
    @InjectView(R.id.song_title2) TextView songTitle;

    @InjectView(R.id.media_previous) ImageButton previous;
    @InjectView(R.id.media_play) FloatingActionButton play;
    @InjectView(R.id.media_next) ImageButton next;

    @InjectView(R.id.time_end) TextView endTime;
    @InjectView(R.id.time_start) TextView currentSongTime;


    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, rootView);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();

        Bundle bundle = getArguments();
        if (bundle == null) { // Single pane layout
            bundle = getActivity().getIntent().getExtras();
        }
        playList = bundle.getParcelable(PLAYLIST);

        currentTrack = playList.getCurrentTrack();

        songTitle.setText(currentTrack.trackName);
        endTime.setText(getTimeString(currentTrack.duration));
        progressBar.setMax((int) currentTrack.duration);

        progressBar.getProgressDrawable().setColorFilter(currentTrack.getPaletteColor(), PorterDuff.Mode.MULTIPLY);

        play.setRippleColor(currentTrack.getPaletteColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            int[][] states = new int[][] {
                    new int[]{}
            };

            int[] colors = new int[] {
                    currentTrack.getPaletteColor()
            };

            ColorStateList myList = new ColorStateList(states, colors);
            progressBar.setThumbTintList(myList);

        } else {
            Drawable d = DrawableCompat.wrap(progressBar.getThumb());
            d.setColorFilter(currentTrack.getPaletteColor(), PorterDuff.Mode.MULTIPLY);
            DrawableCompat.setTint(d, currentTrack.getPaletteColor());
        }
        play.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);

        return rootView;
    }
    private String getTimeString(long millis) {
        long secondsLong = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        String secondsString = "" + secondsLong;
        if (secondsLong < 10) {
            secondsString = "0" + secondsLong;
        }
        return String.format("%d:%s",
                TimeUnit.MILLISECONDS.toMinutes(millis), secondsString);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(resultsSave, currentTrack);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!currentTrack.albumName.equals(CustomTrack.ALBUM_EMPTY)) {
            Glide.with(getActivity()).load(currentTrack.albumURL)
                    .fitCenter()
                    .into(albumImage);
        }

        if (savedInstanceState != null) {
            currentTrack = savedInstanceState.getParcelable(resultsSave);
        } else {
            executeSearch();
        }
    }
    private void executeSearch() {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        killRunningTaskIfExists();

        if (!isConnected) {
//            displayError(R.string.error_network_availability); TODO
        } else {
//            removeError(); TODO
            loadingBar.setVisibility(View.VISIBLE);
            searchTask = getSearchTask().execute();
        }


    }
    private AsyncTask<Void, Void, Void> getSearchTask() {
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loadingBar.setVisibility(View.GONE);
            }
        };
    }
    private void killRunningTaskIfExists() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // This method will be called when a MusicStatusEvent is posted
    public void onEvent(MusicStatusEvent event){

        int id = (event.isPlaying) ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_24dp;
        play.setImageResource(id);

    }
    public void onEventMainThread(MusicStatusTimeEvent event) {
        progressBar.setProgress(event.progress);
        currentSongTime.setText(getTimeString(event.progress));
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        String action = null;
        if (id == play.getId()) {
            action = PlaybackService.ACTION_PLAY;
        } else if (id == previous.getId()) {
            action = PlaybackService.ACTION_PREV;
        } else if (id == next.getId()) {
            action = PlaybackService.ACTION_NEXT;
        }
        Intent i = new Intent(getActivity(), PlaybackService.class);
        i.setAction(action);
        i.putExtra(PlayerFragment.PLAYLIST, playList);
        getActivity().startService(i);
    }
}
