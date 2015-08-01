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

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Kyle on 6/6/2015
 */
public class PlayerActivity extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;
    @InjectView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.inject(this);

        Playlist playlist = getIntent().getExtras().getParcelable(PlayerFragment.PLAYLIST);
        CustomTrack track = playlist.getCurrentTrack();

        toolbar.setVisibility(View.VISIBLE);

        setThemeColors(track);

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
            setShareIntent(null);
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

    // Somewhere in the application.
    public void setShareIntent(CustomTrack currentTrack) { // Broken on phone for first track
        if (currentTrack != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_url));
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "" + currentTrack.previewURL);
            mShareActionProvider.setShareIntent(shareIntent); // This is null on first runthrough
        }
    }
}
