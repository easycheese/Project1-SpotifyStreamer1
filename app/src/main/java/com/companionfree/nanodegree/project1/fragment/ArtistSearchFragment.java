package com.companionfree.nanodegree.project1.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.adapter.ArtistAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view
 */
public class ArtistSearchFragment extends BaseFragment implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener{
    private String searchState_Save = "search_state";
    private String searchText_Save = "search_text";
    private String searchKeyboardEnabled_Save = "search_keyboard_enabled";
    private SearchView searchView;

    private List<Artist> artists;
    private ArtistAdapter artistAdapter;

    private static final long SEARCH_DELAY_MILLIS = 500;
    private boolean SEARCH_ENABLED = false;
    private boolean SEARCH_KEYBOARD_ENABLED = false;
    private String currentSearchText = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        artists = new ArrayList<>();
        artistAdapter = new ArtistAdapter(artists);
        recyclerView.setAdapter(artistAdapter);
        recyclerView.addOnScrollListener(new MyScrollListener());

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_SHORT).show();
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });


        return rootView;
    }

    protected void executeSearch() {
        boolean isConnected = getConnectivityStatus();
        boolean searchIsEmpty = currentSearchText.equals("");
        killRunningTaskIfExists();

        if (searchIsEmpty) {
            artists.clear();
            artistAdapter.notifyDataSetChanged();
            loadingBar.setVisibility(View.GONE);
            displayError(R.string.error_no_search_text, true);
        }

        if (!isConnected) {
            displayError(R.string.error_network_availability, false);
        } else if (!searchIsEmpty){
            removeError();
            loadingBar.setVisibility(View.VISIBLE);
            searchTask = getSearchTask().execute();
        }
    }

    private AsyncTask<Void, Void, Void> getSearchTask() {
        return new AsyncTask<Void, Void, Void>() {
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
                    displayError(R.string.error_no_results, false);
                }
            }
        };
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        killRunningTaskIfExists();
        outState.putString(searchText_Save, currentSearchText);
        outState.putBoolean(searchState_Save, SEARCH_ENABLED);
        String json = new Gson().toJson(artists);
        outState.putString(resultsSave, json);

        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        outState.putBoolean(searchKeyboardEnabled_Save, imm.isAcceptingText());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            SEARCH_ENABLED = savedInstanceState.getBoolean(searchState_Save);
            SEARCH_KEYBOARD_ENABLED = savedInstanceState.getBoolean(searchKeyboardEnabled_Save);
            currentSearchText = savedInstanceState.getString(searchText_Save);
            String results = savedInstanceState.getString(resultsSave);
            Type collectionType = new TypeToken<Collection<Artist>>(){}.getType();
            List<Artist> artistResults = new Gson().fromJson(results, collectionType);
            artists.addAll(artistResults);
        } else {
            displayError(R.string.error_no_search_text, true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchButton = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchButton,this);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchButton.getActionView();
        searchView.setFocusable(true);
        if (searchView != null) {

            if (SEARCH_ENABLED) {
                searchButton.expandActionView();
                searchView.setQuery(currentSearchText, false);
                if (!SEARCH_KEYBOARD_ENABLED) {
                    searchView.clearFocus();
                }

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
        SEARCH_ENABLED = true;
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        SEARCH_ENABLED = false;
        currentSearchText = "";
        return true;
    }

    private class MyScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            InputMethodManager inputMethodManger = (InputMethodManager) getActivity().getSystemService(Activity
                    .INPUT_METHOD_SERVICE);
            inputMethodManger.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
        }
    }

}
