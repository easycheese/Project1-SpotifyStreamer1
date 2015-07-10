package com.companionfree.nanodegree.project1.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.adapter.TopSongsAdapter;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InterruptedIOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopSongsFragment extends BaseFragment{

    private ArrayList<CustomTrack> tracks;
    private TopSongsAdapter topSongsAdapter;

    private String artistId;

    public static final String ARTIST_ID = "artist_id";
    public static final String ARTIST_NAME = "artist_name";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setupToolbar();

        tracks = new ArrayList<>();
        topSongsAdapter = new TopSongsAdapter(tracks);
        recyclerView.setAdapter(topSongsAdapter);

        Bundle bundle = getArguments();
        if (bundle == null) { // Single pane layout
            bundle = getActivity().getIntent().getExtras();
        }

        artistId = bundle.getString(ARTIST_ID);
        toolbar.setTitle(bundle.getString(ARTIST_NAME));

        return rootView;
    }
    private void setupToolbar() {


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        killRunningTaskIfExists();
        saveError(outState);
//        String json = new Gson().toJson(tracks);
//        outState.putString(resultsSave, json);
        outState.putParcelableArrayList(resultsSave, tracks);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            displaySavedError(savedInstanceState);
            ArrayList<CustomTrack> customTracks = savedInstanceState.getParcelableArrayList(resultsSave);
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
                try {
                    Tracks results = spotifyService.getArtistTopTrack(artistId, options);
                    List<Track> resultTracks = results.tracks;
                    tracks.clear();

                    List<CustomTrack> customTracks = new ArrayList<>();
                    for (Track track : resultTracks) {
                        customTracks.add(new CustomTrack(track));
                    }

                    tracks.addAll(customTracks);
                } catch (RetrofitError error) { // timeout errors

                    // TODO (and in other fragment)
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                topSongsAdapter.notifyDataSetChanged();
                loadingBar.setVisibility(View.GONE);

                if (tracks != null && tracks.isEmpty()) {
                    displayError(R.string.error_no_results_tracks, false);
                }
            }
        };
    }



}
