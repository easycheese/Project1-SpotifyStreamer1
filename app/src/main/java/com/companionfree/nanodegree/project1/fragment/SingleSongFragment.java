package com.companionfree.nanodegree.project1.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Kyle on 6/6/2015
 */
public class SingleSongFragment extends Fragment {

    public static final String TRACK = "track_info";

    protected String resultsSave = "track";

    private CustomTrack track;
    private SpotifyService spotifyService;
    private AsyncTask searchTask;

    @InjectView(R.id.loading_bar) ProgressBar loadingBar;
    @InjectView(R.id.song_image) ImageView albumImage;
    @InjectView(R.id.song_progress) SeekBar progressBar;

    @InjectView(R.id.media_previous) ImageButton previous;
    @InjectView(R.id.media_play) FloatingActionButton play;
    @InjectView(R.id.media_next) ImageButton next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song, container, false);
        ButterKnife.inject(this, rootView);

        TextView title = (TextView) rootView.findViewById(R.id.song_title2);
        TextView endTime = (TextView) rootView.findViewById(R.id.time_end);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();

        Intent i = getActivity().getIntent();
        String trackJson = i.getStringExtra(TRACK);
        track = new Gson().fromJson(trackJson, CustomTrack.class);

        title.setText(track.name);
        endTime.setText(getTimeString(track.duration_ms));
        progressBar.setMax((int) track.duration_ms);

        progressBar.getProgressDrawable().setColorFilter(track.getPaletteColor(), PorterDuff.Mode.MULTIPLY);

        play.setRippleColor(track.getPaletteColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            int[][] states = new int[][] {
                    new int[]{}
            };

            int[] colors = new int[] {
                    track.getPaletteColor()
            };

            ColorStateList myList = new ColorStateList(states, colors);
            progressBar.setThumbTintList(myList);

        } else {
            Drawable d = DrawableCompat.wrap(progressBar.getThumb());
            d.setColorFilter(track.getPaletteColor(), PorterDuff.Mode.MULTIPLY);
            DrawableCompat.setTint(d, track.getPaletteColor());
        }

        return rootView;
    }
    private String getTimeString(long millis) {
        return String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        String json = new Gson().toJson(track);
        outState.putString(resultsSave, json);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!track.album.images.isEmpty()) {
            Image image = track.album.images.get(0);
            Glide.with(getActivity()).load(image.url)
                    .fitCenter()
                    .into(albumImage);
        }

        if (savedInstanceState != null) {
            String results = savedInstanceState.getString(resultsSave);
            track = new Gson().fromJson(results, CustomTrack.class);
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
                //TODO dl music/start stream
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
}
