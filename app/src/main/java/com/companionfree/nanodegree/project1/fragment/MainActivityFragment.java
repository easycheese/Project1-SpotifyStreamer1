package com.companionfree.nanodegree.project1.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.adapter.ArtistAdapter;
import com.companionfree.nanodegree.project1.model.ClickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.greenrobot.event.EventBus;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements TextWatcher {

    private EditText searchText;
    private SpotifyService spotifyService;
    private AsyncTask searchTask;
    private List<Artist> artists;
    private ArtistAdapter artistAdapter;

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
        artistAdapter = new ArtistAdapter(artists, getActivity().getApplicationContext());
        recyclerView.setAdapter(artistAdapter);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();


        return rootView;
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
    @SuppressWarnings("unused")
    public void onEventMainThread(ClickEvent event) {
        Artist artistSelection = artists.get(event.getPosition());

    }

    private void executeSearch(final String searchString) {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }

        if (searchString.equals("")) {
            artists.clear();
            artistAdapter.notifyDataSetChanged();
        } else {
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
}
