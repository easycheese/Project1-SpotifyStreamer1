package com.companionfree.nanodegree.project1.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.companionfree.nanodegree.project1.R;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by Kyle on 6/6/2015
 */
public class BaseFragment extends Fragment {

    protected SpotifyService spotifyService;
    protected AsyncTask searchTask;
    protected ProgressBar loadingBar;
    protected RecyclerView recyclerView;
    protected TextView errorText;
    protected LinearLayout errorBlock;
    protected ImageView errorImage;
    protected View rootView;


    protected String resultsSave = "results";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        rootView = inflater.inflate(R.layout.fragment_base, container, false);

        errorText = (TextView) rootView.findViewById(R.id.error_text);
        errorImage = (ImageView) rootView.findViewById(R.id.error_image);
        errorBlock = (LinearLayout) rootView.findViewById(R.id.error_block);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_searchresults);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();

        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void killRunningTaskIfExists() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }
    protected void displayError(int stringId, boolean statusOnly) { // TODO this isn't working right
        recyclerView.setVisibility(View.GONE);
        errorBlock.setVisibility(View.VISIBLE);
        errorText.setText(getActivity().getString(stringId));

        int vis = (statusOnly) ? View.GONE : View.VISIBLE;
        errorImage.setVisibility(vis);
    }

    protected void removeError() {
        recyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
    }
    protected Boolean getConnectivityStatus() {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
