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
import com.companionfree.nanodegree.project1.service.PlaybackService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by Kyle on 6/5/2015
 */
public class TopSongsActivity extends AppCompatActivity{

    @InjectView(R.id.toolbar) Toolbar toolbar;
    private MenuItem nowPlayingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topsongs);
        ButterKnife.inject(this);

        Intent i = getIntent();
        String artistName = i.getStringExtra(TopSongsFragment.ARTIST_NAME);

        setupToolbar(artistName);
    }

    private void setupToolbar(String artistName) {
        toolbar.setSubtitle(artistName);
        toolbar.setTitle(getString(R.string.top_tracks));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.menu_search);
        search.setVisible(false);

        MenuItem settings = menu.findItem(R.id.menu_settings);
        settings.setVisible(false);

        nowPlayingButton = menu.findItem(R.id.menu_now_playing);
        nowPlayingButton.setVisible(false);

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
        nowPlayingButton.setVisible(event.isPlaying);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nowPlayingButton != null) {
            nowPlayingButton.setVisible(PlaybackService.isPlaying());
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
}
