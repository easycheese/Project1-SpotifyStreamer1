package com.companionfree.nanodegree.project1.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.activity.MainSearchActivity;
import com.companionfree.nanodegree.project1.activity.SettingsActivity;
import com.companionfree.nanodegree.project1.adapter.ArtistAdapter;
import com.companionfree.nanodegree.project1.model.CustomArtist;
import com.companionfree.nanodegree.project1.model.MusicStatusEvent;
import com.companionfree.nanodegree.project1.service.PlaybackService;
import com.companionfree.nanodegree.project1.util.ConnectionManager;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view
 */
public class ArtistSearchFragment extends BaseFragment implements SearchView.OnQueryTextListener,
        MenuItemCompat.OnActionExpandListener, Toolbar.OnMenuItemClickListener {
    private ArrayList<CustomArtist> artists;
    private ArtistAdapter artistAdapter;

    private static final long SEARCH_DELAY_MILLIS = 500;
    private boolean searchEnabled = false;
    private String currentSearchText = "";

    private MenuItem nowPlayingButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setupToolbar();

        artists = new ArrayList<>();
        artistAdapter = new ArtistAdapter(artists);
        recyclerView.setAdapter(artistAdapter);
        recyclerView.addOnScrollListener(new MyScrollListener());

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        // TODO remove
        Button b = (Button) rootView.findViewById(R.id.kill);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PlaybackService.class);
                getActivity().stopService(i);
            }
        });


        return rootView;
    }

    private void setupToolbar() {
        Menu existingMenu = toolbar.getMenu();
        Boolean visibleItems = existingMenu.hasVisibleItems();

        if (!visibleItems) {
            toolbar.inflateMenu(R.menu.menu_main);
        }

        toolbar.setTitle(getString(R.string.app_name));

        Menu menu = toolbar.getMenu();
        nowPlayingButton = menu.findItem(R.id.menu_now_playing);
        nowPlayingButton.setVisible(false);

        MenuItem searchButton = menu.findItem(R.id.menu_search);
        MenuItemCompat.setOnActionExpandListener(searchButton, this);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchButton.getActionView();
        searchView.setFocusable(true);

        if (searchEnabled) {
            searchButton.expandActionView();
            searchView.setQuery(currentSearchText, false);
        }

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(this);


        toolbar.setOnMenuItemClickListener(this);
    }


    protected void executeSearch() {
        boolean isConnected = ConnectionManager.hasNetworkConnection(getActivity());
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
            artists.clear();
        } else if (!searchIsEmpty){
            removeError();
            loadingBar.setVisibility(View.VISIBLE);
            searchTask = getSearchTask().execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        nowPlayingButton.setVisible(PlaybackService.isPlaying());
    }

    @SuppressWarnings("unused")
    public void onEvent(MusicStatusEvent event){
        nowPlayingButton.setVisible(event.isPlaying);
    }


    private AsyncTask<Void, Void, Void> getSearchTask() {
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(SEARCH_DELAY_MILLIS);

                    ArtistsPager results = spotifyService.searchArtists(currentSearchText);

                    artists.clear();

                    for (Artist artist : results.artists.items) {
                        artists.add(new CustomArtist(artist));
                    }

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

                if (artists != null && artists.isEmpty()) {
                    displayError(R.string.error_no_results, false);
                }
            }
        };
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        killRunningTaskIfExists();
        saveError(outState);
        outState.putParcelableArrayList(resultsSave, artists);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            displaySavedError(savedInstanceState);
            ArrayList<CustomArtist> artistResults = savedInstanceState.getParcelableArrayList(resultsSave);
            if (artistResults!= null) {
                artists.addAll(artistResults);
            }

        } else {
            displayError(R.string.error_no_search_text, true);
        }
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
        searchEnabled = true;
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        searchEnabled = false;
        currentSearchText = "";
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_now_playing:
                Bundle bundle = new Bundle();
                bundle.putBoolean(PlayerFragment.RESUMING_PLAYER, true);

                ((MainSearchActivity) getActivity()).launchPlayer(bundle);




                return true;
        }
        return false;
    }


    private class MyScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            getInputMethodManager().hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
        }
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getActivity().getSystemService(Activity
                .INPUT_METHOD_SERVICE);
    }
}
