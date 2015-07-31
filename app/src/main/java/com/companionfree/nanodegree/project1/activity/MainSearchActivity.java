package com.companionfree.nanodegree.project1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.ArtistSearchFragment;
import com.companionfree.nanodegree.project1.fragment.PlayerFragment;
import com.companionfree.nanodegree.project1.fragment.TopSongsFragment;
import com.companionfree.nanodegree.project1.model.ArtistClickEvent;
import com.companionfree.nanodegree.project1.model.SongClickEvent;

import de.greenrobot.event.EventBus;

public class MainSearchActivity extends AppCompatActivity  {

    private boolean mTwoPane;

    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        if (findViewById(R.id.top_songs_list_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            mainToolbar = (Toolbar) findViewById(R.id.maintoolbar);

            ArtistSearchFragment fragment = new ArtistSearchFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.artist_search_fragment, fragment)
                    .commit();

            TopSongsFragment topSongsFragment = new TopSongsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_songs_list_container, topSongsFragment)
                    .commit();

            // In two-pane mode, list items should be given the TODO
            // 'activated' state when touched.
//            ((TopSongsFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.artist_search_fragment))
//            .setActivateOnItemClick(true);
        }
    }
    public Toolbar getMainToolbar() {
        return mainToolbar;
    }
    public boolean isTwoPane() {
        return mTwoPane;
    }


    public void onEvent(ArtistClickEvent event){
        Bundle bundle = new Bundle();
        bundle.putString(TopSongsFragment.ARTIST_NAME, event.artistName);
        bundle.putString(TopSongsFragment.ARTIST_ID, event.artistId);

        TopSongsFragment fragment = new TopSongsFragment();

        if (mTwoPane) {
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_songs_list_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopSongsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }



    public void onEvent(SongClickEvent event){ // only received in Master-Detail flow
        Bundle bundle = new Bundle();
        bundle.putParcelable(PlayerFragment.PLAYLIST, event.playlist);
        PlayerFragment songFragment = PlayerFragment.newInstance();
        songFragment.setArguments(bundle);
        songFragment.show(getSupportFragmentManager(), getClass().getSimpleName());
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
}
