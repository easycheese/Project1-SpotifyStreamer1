package com.companionfree.nanodegree.project1.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.adapter.TrackAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Kyle on 6/6/2015
 */
public class SingleSongFragment extends Fragment {

    public static final String TRACK = "track_info";

    protected String resultsSave = "track";

    private Track track;
    private SpotifyService spotifyService;
    protected AsyncTask searchTask;
    protected ProgressBar loadingBar;
    protected ImageView albumImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song, container, false);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);
        TextView artistText = (TextView) rootView.findViewById(R.id.song_artist);
        TextView albumText = (TextView) rootView.findViewById(R.id.song_album);
        TextView songText = (TextView) rootView.findViewById(R.id.song_name);
        albumImage = (ImageView) rootView.findViewById(R.id.song_image);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();

        Intent i = getActivity().getIntent();
        String trackJson = i.getStringExtra(TRACK);
        track = new Gson().fromJson(trackJson, Track.class);

        artistText.setText(track.artists.get(0).name);
        albumText.setText(track.album.name);
        songText.setText(track.name);

        return rootView;
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
                    .centerCrop()
                    .into(albumImage);
        }

        if (savedInstanceState != null) {
            String results = savedInstanceState.getString(resultsSave);
            track = new Gson().fromJson(results, Track.class);
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

                track = spotifyService.getTrack(track.id);

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
