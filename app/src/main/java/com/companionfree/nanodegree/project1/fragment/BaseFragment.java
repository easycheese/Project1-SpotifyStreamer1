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

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by Kyle on 6/6/2015
 */
public class BaseFragment extends Fragment {

    protected SpotifyService spotifyService;
    protected AsyncTask searchTask;

    @InjectView(R.id.loading_bar) protected ProgressBar loadingBar;
    @InjectView(R.id.recyclerview_searchresults) protected RecyclerView recyclerView;
    @InjectView(R.id.error_text) protected TextView errorText;
    @InjectView(R.id.error_block) protected LinearLayout errorBlock;
    @InjectView(R.id.error_image) protected ImageView errorImage;

    protected View rootView;

    protected String resultsSave = "results";
    protected String errorVisibilitySave = "errorVis";
    protected String errorImageVisibilitySave = "errorImageVis";
    protected String errorTextSave = "errorText";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        rootView = inflater.inflate(R.layout.fragment_base, container, false);
        ButterKnife.inject(this, rootView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void killRunningTaskIfExists() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }
    protected void displayError(int stringId, boolean statusOnly) {
        recyclerView.setVisibility(View.GONE);
        errorBlock.setVisibility(View.VISIBLE);
        errorText.setText(getActivity().getString(stringId));

        int vis = (statusOnly) ? View.GONE : View.VISIBLE;
        errorImage.setVisibility(vis);
    }

    protected void removeError() {
        recyclerView.setVisibility(View.VISIBLE);
        errorBlock.setVisibility(View.GONE);
    }

    protected void saveError(Bundle outState) {
        outState.putInt(errorVisibilitySave, errorBlock.getVisibility());
        outState.putInt(errorImageVisibilitySave, errorImage.getVisibility());
        outState.putString(errorTextSave, errorText.getText().toString());
    }
    protected void displaySavedError(Bundle savedInstanceState) {
        //noinspection ResourceType
        errorBlock.setVisibility(savedInstanceState.getInt(errorVisibilitySave, View.GONE));
        //noinspection ResourceType
        errorImage.setVisibility(savedInstanceState.getInt(errorImageVisibilitySave, View.GONE));
        errorText.setText(savedInstanceState.getString(errorTextSave));
    }
    protected Boolean getConnectivityStatus() {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
