package com.companionfree.nanodegree.project1.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.adapter.TrackAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopSongsFragment extends Fragment{

    private SpotifyService spotifyService;
    private AsyncTask searchTask;
    private List<Track> tracks;
    private TrackAdapter trackAdapter;
    private ProgressBar loadingBar;

    private String artistId;

    public static final String ARTIST_ID = "artist_id";
    public static final String ARTIST_NAME = "artist_name";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artistsearch, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_searchresults);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        tracks = new ArrayList<>();
        trackAdapter = new TrackAdapter(tracks);
        recyclerView.setAdapter(trackAdapter);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();

        Intent i = getActivity().getIntent();
        artistId = i.getStringExtra(ARTIST_ID);


        loadingBar = (ProgressBar) rootView.findViewById(R.id.artist_loading_bar);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        executeSearch();
    }

    private void executeSearch() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }

        loadingBar.setVisibility(View.VISIBLE);

        searchTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Map<String, Object> options = new HashMap<>();
                options.put("country", "US");
                Tracks results = spotifyService.getArtistTopTrack(artistId, options);

                tracks.clear();
                tracks.addAll(results.tracks);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                trackAdapter.notifyDataSetChanged();
                loadingBar.setVisibility(View.GONE);
            }
        }.execute();


    }

}
