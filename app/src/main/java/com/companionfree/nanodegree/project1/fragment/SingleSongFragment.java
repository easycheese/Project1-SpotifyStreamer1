package com.companionfree.nanodegree.project1.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.google.gson.Gson;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Kyle on 6/6/2015
 */
public class SingleSongFragment extends Fragment {

    public static final String TRACK = "track_info";

    protected String resultsSave = "track";

    private CustomTrack track;
    private SpotifyService spotifyService;
    private AsyncTask searchTask;
    private ProgressBar loadingBar;
    private ImageView albumImage;
    private SeekBar progressBar;

    private ImageButton previous;
    private ImageButton play;
    private ImageButton next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song, container, false);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);
        albumImage = (ImageView) rootView.findViewById(R.id.song_image);
        progressBar = (SeekBar) rootView.findViewById(R.id.song_progress);
        TextView title2 = (TextView) rootView.findViewById(R.id.song_title2);

        previous = (ImageButton) rootView.findViewById(R.id.media_previous);
        play = (ImageButton) rootView.findViewById(R.id.media_play);
        next = (ImageButton) rootView.findViewById(R.id.media_next);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();

        Intent i = getActivity().getIntent();
        String trackJson = i.getStringExtra(TRACK);
        track = new Gson().fromJson(trackJson, CustomTrack.class);

//        title.setText(track.name);
        title2.setText(track.name);
        int alpha = 256;//TODO need alpha of text larger than background
        title2.setTextColor(Color.argb(alpha, 255, 0, 0));
        title2.setTextColor(track.getPaletteColor());
        progressBar.setMax((int)track.duration_ms);

        return rootView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {

        String json = new Gson().toJson(track);
        outState.putString(resultsSave, json);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!track.album.images.isEmpty()) {
            Image image = track.album.images.get(0);
            Glide.with(getActivity()).load(image.url)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            Drawable d  = resource.getCurrent();
//
//                            int width = d.getIntrinsicWidth();
//                            width = width > 0 ? width : 1;
//                            int height = d.getIntrinsicHeight();
//                            height = height > 0 ? height : 1;
//
//                            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                            Canvas canvas = new Canvas(bitmap);
//                            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//                            d.draw(canvas);
//
//
//                            Palette.Builder palette = new Palette.Builder(bitmap);
//                            Palette p = palette.generate();
//
//                            next.setBackgroundColor(p.getMutedColor(0));
//                            play.setBackgroundColor(p.getVibrantColor(0));
//                            previous.setBackgroundColor(p.getDarkVibrantColor(0));
                            return false;
                        }
                    })
                    .into(albumImage);
        }

        if (savedInstanceState != null) {
            String results = savedInstanceState.getString(resultsSave);
            track = new Gson().fromJson(results, CustomTrack.class);
        } else {
            executeSearch();
        }
    }
    private void executeSearch() {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        killRunningTaskIfExists();

        if (!isConnected) {
//            displayError(R.string.error_network_availability); TODO
        } else {
//            removeError(); TODO
            loadingBar.setVisibility(View.VISIBLE);
            searchTask = getSearchTask().execute();
        }


    }
    private AsyncTask<Void, Void, Void> getSearchTask() {
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {



                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loadingBar.setVisibility(View.GONE);
            }
        };
    }
    private void killRunningTaskIfExists() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }
}
