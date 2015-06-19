package com.companionfree.nanodegree.project1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.ArtistSearchFragment;
import com.companionfree.nanodegree.project1.fragment.TopSongsFragment;
import com.companionfree.nanodegree.project1.model.ArtistClickEvent;

import de.greenrobot.event.EventBus;

public class MainSearchActivity extends AppCompatActivity  {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        if (findViewById(R.id.top_songs_list_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            ArtistSearchFragment fragment = new ArtistSearchFragment();
//            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_songs_list_container, fragment)
                    .commit();


            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
//            ((TopSongsFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.artist_search_fragment))
//            .setActivateOnItemClick(true);
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

    // This method will be called when a ArtistClickEvent is posted
    public void onEvent(ArtistClickEvent event){
        Bundle bundle = new Bundle();
        bundle.putString(TopSongsFragment.ARTIST_NAME, event.artistName);
        bundle.putString(TopSongsFragment.ARTIST_ID, event.artistId);

        TopSongsFragment fragment = new TopSongsFragment();

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.top_songs_list_container, fragment)
                .commit();

    }

}
