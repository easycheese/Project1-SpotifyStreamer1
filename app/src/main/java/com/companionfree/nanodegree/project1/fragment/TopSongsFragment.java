package com.companionfree.nanodegree.project1.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.adapter.TrackAdapter;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopSongsFragment extends BaseFragment{

    private List<CustomTrack> tracks;
    private TrackAdapter trackAdapter;

    private String artistId;

    public static final String ARTIST_ID = "artist_id";
    public static final String ARTIST_NAME = "artist_name";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        tracks = new ArrayList<>();
        trackAdapter = new TrackAdapter(tracks);
        recyclerView.setAdapter(trackAdapter);

        Intent i = getActivity().getIntent();
        artistId = i.getStringExtra(ARTIST_ID);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        killRunningTaskIfExists();
        saveError(outState);
        String json = new Gson().toJson(tracks);
        outState.putString(resultsSave, json);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            displaySavedError(savedInstanceState);
            String results = savedInstanceState.getString(resultsSave);
            Type collectionType = new TypeToken<Collection<Track>>(){}.getType();
            List<Track> trackResults = new Gson().fromJson(results, collectionType);
            ArrayList<CustomTrack> customTracks = new ArrayList<>();
            for (Track track : trackResults) {
                customTracks.add(new CustomTrack(track));
            }
            tracks.addAll(customTracks);
        } else {
            executeSearch();
        }
    }

    private void executeSearch() {
        boolean isConnected = getConnectivityStatus();
        killRunningTaskIfExists();

        if (!isConnected) {
            displayError(R.string.error_network_availability, false);
        } else {
            removeError();
            loadingBar.setVisibility(View.VISIBLE);
            searchTask = getSearchTask().execute();
        }

    }
    private AsyncTask<Void, Void, Void> getSearchTask() {
        return new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Map<String, Object> options = new HashMap<>();
                options.put("country", "US");
                Tracks results = spotifyService.getArtistTopTrack(artistId, options);

                List<Track> resultTracks = results.tracks;
                tracks.clear();

                List<CustomTrack> customTracks = new ArrayList<>();
                for (Track track : resultTracks) {
                    customTracks.add(new CustomTrack(track));
                }

                tracks.addAll(customTracks);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                trackAdapter.notifyDataSetChanged();
                loadingBar.setVisibility(View.GONE);

                if (tracks != null && tracks.isEmpty()) {
                    displayError(R.string.error_no_results_tracks, false);
                }
            }
        };
    }
}
