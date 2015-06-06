package com.companionfree.nanodegree.project1.fragment;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.adapter.ArtistAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view
 */
public class ArtistSearchFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener{

    private SpotifyService spotifyService;
    private AsyncTask searchTask;
    private List<Artist> artists;
    private ArtistAdapter artistAdapter;
    private ProgressBar loadingBar;
    private RecyclerView recyclerView;
    private TextView errorText;

    private static final long SEARCH_DELAY_MILLIS = 500;
    private boolean SEARCHING = false;
    private String currentSearchText = "";
    private String searchState_Save = "search_state";
    private String searchText_Save = "search_text";
    private String searchResults_Save = "search_results";
    private MenuItem searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_artistsearch, container, false);

        errorText = (TextView) rootView.findViewById(R.id.error_text);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_searchresults);
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

    private void executeSearch() {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        boolean searchIsEmpty = currentSearchText.equals("");

        killRunningTaskIfExists();

        if (searchIsEmpty) {
            artists.clear();
            artistAdapter.notifyDataSetChanged();
            loadingBar.setVisibility(View.GONE);
            displayError(R.string.error_no_search_text);
        }

        if (!isConnected) {
            displayError(R.string.error_network_availability);
        } else if (!searchIsEmpty){
            removeError();
            loadingBar.setVisibility(View.VISIBLE);
            searchTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(SEARCH_DELAY_MILLIS);

                        ArtistsPager results = spotifyService.searchArtists(currentSearchText);

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

                    if (artists.isEmpty()) {
                        displayError(R.string.error_no_results);
                    }
                }
            }.execute();
        }

    }
    private void killRunningTaskIfExists() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }
    private void displayError(int stringId) {
        recyclerView.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(getActivity().getString(stringId));
    }
    private void removeError() {
        recyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        killRunningTaskIfExists();
        outState.putString(searchText_Save, currentSearchText);
        outState.putBoolean(searchState_Save, SEARCHING);

        String json = new Gson().toJson(artists);
        outState.putString(searchResults_Save, json);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            SEARCHING = savedInstanceState.getBoolean(searchState_Save);
            currentSearchText = savedInstanceState.getString(searchText_Save);

            String results = savedInstanceState.getString(searchResults_Save);
            Type collectionType = new TypeToken<Collection<Artist>>(){}.getType();
            List<Artist> artistResults = new Gson().fromJson(results, collectionType);
            artists.addAll(artistResults);
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchButton = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchButton,this);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView)searchButton.getActionView();

        if (searchView != null) {

            if (SEARCHING) {
                searchButton.expandActionView();
                searchView.setQuery(currentSearchText, false);

            }

            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);

            searchView.setOnQueryTextListener(this);

        }

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        currentSearchText = newText;
        executeSearch();
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        SEARCHING = true;
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        SEARCHING = false;
        currentSearchText = "";
        return true;
    }

    private class MyScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged (RecyclerView recyclerView, int newState) {
            InputMethodManager inputMethodManger = (InputMethodManager)getActivity().getSystemService(Activity
                    .INPUT_METHOD_SERVICE);
            inputMethodManger.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
        }

    }
}
