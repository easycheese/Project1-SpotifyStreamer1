package com.companionfree.nanodegree.project1.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.companionfree.nanodegree.project1.BuildConfig;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.SingleSongFragment;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.google.gson.Gson;

/**
 * Created by Kyle on 6/6/2015
 */
public class SongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String trackJson = i.getStringExtra(SingleSongFragment.TRACK);
        CustomTrack track = new Gson().fromJson(trackJson, CustomTrack.class);

        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(track.getPaletteColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(track.getPaletteColorDark());
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SingleSongFragment())
                    .commit();
        }

    }

}
