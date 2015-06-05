package com.companionfree.nanodegree.project1.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
public class MainActivityFragment extends Fragment implements TextWatcher{

    private EditText searchText;
    private SpotifyService spotifyService;
    private AsyncTask searchTask;
    private List<Artist> artists;
    private ArtistAdapter artistAdapter;
    private ProgressBar loadingBar;

    private static final long SEARCH_DELAY_MILLIS = 500;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        searchText = (EditText) rootView.findViewById(
                R.id.search_text_input);
        searchText.addTextChangedListener(this);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_searchresults);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        artists = new ArrayList<>();
        artistAdapter = new ArtistAdapter(artists);
        recyclerView.setAdapter(artistAdapter);
        recyclerView.addOnScrollListener(new MyScrollListener());

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
                    try {
                        Thread.sleep(SEARCH_DELAY_MILLIS);
                        ArtistsPager results = spotifyService.searchArtists(searchString);
                        artists.clear();
                        artists.addAll(results.artists.items);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        executeSearch(s.toString());
    }

    private class MyScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged (RecyclerView recyclerView, int newState) {
            InputMethodManager inputMethodManger = (InputMethodManager)getActivity().getSystemService(Activity
                    .INPUT_METHOD_SERVICE);
            inputMethodManger.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
            searchText.clearFocus();
        }

    }
}
