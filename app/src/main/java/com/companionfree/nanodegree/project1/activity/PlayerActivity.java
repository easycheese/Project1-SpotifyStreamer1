package com.companionfree.nanodegree.project1.activity;

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
import com.companionfree.nanodegree.project1.service.PlaybackService;
import com.companionfree.nanodegree.project1.util.ShareManager;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Kyle on 6/6/2015
 */
public class PlayerActivity extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;

    @InjectView(R.id.toolbar) Toolbar toolbar;

    private CustomTrack track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.inject(this);



        Bundle bundle = getIntent().getExtras();
        boolean isResuming = bundle.getBoolean(PlayerFragment.RESUMING_PLAYER);
        Playlist playlist;
        if (isResuming) {
            playlist = PlaybackService.getCurrentPlaylist();
        } else {
            playlist = bundle.getParcelable(PlayerFragment.PLAYLIST);
        }

        if (playlist != null) {
            track = playlist.getCurrentTrack();
            setThemeColors(track);
            toolbar.setTitle(track.artistName);
            toolbar.setSubtitle(track.albumName);
        }

        toolbar.setVisibility(View.VISIBLE);


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
    }



    public void setThemeColors(CustomTrack currentTrack) {
        toolbar.setBackgroundColor(currentTrack.getPaletteColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(currentTrack.getPaletteColorDark());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            setShareIntent(track);
        }

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


    public void setShareIntent(CustomTrack currentTrack) {
        if (currentTrack != null) {
            mShareActionProvider.setShareIntent(ShareManager.getShareDetails(currentTrack, this));
        }
    }
}
