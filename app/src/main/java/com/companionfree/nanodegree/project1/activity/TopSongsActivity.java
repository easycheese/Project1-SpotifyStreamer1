package com.companionfree.nanodegree.project1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.fragment.PlayerFragment;
import com.companionfree.nanodegree.project1.fragment.TopSongsFragment;
import com.companionfree.nanodegree.project1.model.MusicStatusEvent;
import com.companionfree.nanodegree.project1.model.SongClickEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by Kyle on 6/5/2015
 */
public class TopSongsActivity extends AppCompatActivity{

    @InjectView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topsongs);
        ButterKnife.inject(this);

        Intent i = getIntent();
        String artistName = i.getStringExtra(TopSongsFragment.ARTIST_NAME);
        toolbar.setSubtitle(artistName);
        toolbar.setTitle(getString(R.string.top_tracks));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true); //TODO disable menu items
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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

    @SuppressWarnings("unused")
    public void onEvent(SongClickEvent event){ //only received in Single pane flow
        Bundle bundle = new Bundle();
        bundle.putParcelable(PlayerFragment.PLAYLIST, event.playlist);
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @SuppressWarnings("unused") //only received in Single pane flow
    public void onEvent(MusicStatusEvent event){
//        nowPlayingButton.setVisible(event.isPlaying); todo
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
