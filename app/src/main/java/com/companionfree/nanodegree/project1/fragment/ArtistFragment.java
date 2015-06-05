package com.companionfree.nanodegree.project1.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.adapter.ArtistAdapter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFragment extends Fragment{

    private SpotifyService spotifyService;
    private AsyncTask searchTask;
    private List<Artist> artists;
    private ArtistAdapter artistAdapter;
    private ProgressBar loadingBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_searchresults);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        artists = new ArrayList<>();
        artistAdapter = new ArtistAdapter(artists);
        recyclerView.setAdapter(artistAdapter);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();

        loadingBar = (ProgressBar) rootView.findViewById(R.id.artist_loading_bar);


        return rootView;
    }

    private void executeSearch(final String searchString) {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
            if (searchString.equals("")) {
                loadingBar.setVisibility(View.GONE);
            }
        }

        if (searchString.equals("")) {
            artists.clear();
            artistAdapter.notifyDataSetChanged();
        } else {
            loadingBar.setVisibility(View.VISIBLE);
            searchTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                        ArtistsPager results = spotifyService.searchArtists(searchString);
                        artists.clear();
                        artists.addAll(results.artists.items);

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    artistAdapter.notifyDataSetChanged();
                    loadingBar.setVisibility(View.GONE);
                }
            }.execute();
        }

    }

}
