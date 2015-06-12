package com.companionfree.nanodegree.project1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.TopSongsFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Kyle on 6/5/2015
 */
public class ArtistActivity extends AppCompatActivity{

    @InjectView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.inject(this);

        Intent i = getIntent();
        String artistName = i.getStringExtra(TopSongsFragment.ARTIST_NAME);
        toolbar.setSubtitle(artistName);
        toolbar.setTitle(getString(R.string.top_tracks));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new TopSongsFragment())
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
