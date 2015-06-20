package com.companionfree.nanodegree.project1.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.SingleSongFragment;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.companionfree.nanodegree.project1.model.Playlist;

import java.util.List;

/**
 * Created by Kyle on 6/6/2015
 */
public class SongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Playlist playlist = getIntent().getExtras().getParcelable(SingleSongFragment.PLAYLIST);
        CustomTrack track = playlist.getCurrentTrack();

        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_songs_list_container, new SingleSongFragment())
                    .commit();
        }

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
}
