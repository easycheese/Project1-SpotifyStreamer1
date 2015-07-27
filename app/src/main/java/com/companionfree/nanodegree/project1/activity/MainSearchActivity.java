package com.companionfree.nanodegree.project1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.ArtistSearchFragment;
import com.companionfree.nanodegree.project1.fragment.PlayerFragment;
import com.companionfree.nanodegree.project1.fragment.TopSongsFragment;
import com.companionfree.nanodegree.project1.model.ArtistClickEvent;
import com.companionfree.nanodegree.project1.model.SongClickEvent;

import de.greenrobot.event.EventBus;

public class MainSearchActivity extends AppCompatActivity  {

    private boolean mTwoPane;
    private String TAG_PLAYER = "player";

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
        songFragment.show(getSupportFragmentManager(), TAG_PLAYER);

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
