package com.companionfree.nanodegree.project1.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.PlayerFragment;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.companionfree.nanodegree.project1.model.Playlist;

/**
 * Created by Kyle on 6/6/2015
 */
public class PlayerActivity extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Playlist playlist = getIntent().getExtras().getParcelable(PlayerFragment.PLAYLIST);
        CustomTrack track = playlist.getCurrentTrack();

        setContentView(R.layout.activity_player);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(track.getPaletteColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(track.getPaletteColorDark());
        }

        toolbar.setTitle(track.artistName);
        toolbar.setSubtitle(track.albumName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PlayerFragment newFragment = PlayerFragment.newInstance();
        ft.add(R.id.embedded, newFragment);
        ft.commit();
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.top_songs_list_container, new PlayerFragment())
//                    .commit();
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();// app icon in action bar clicked; go home
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Somewhere in the application.
    public void doShare(Intent shareIntent) { // TODO implement share
        // When you want to share set the share intent.
        mShareActionProvider.setShareIntent(shareIntent);
    }
}
