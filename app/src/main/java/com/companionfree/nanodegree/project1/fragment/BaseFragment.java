package com.companionfree.nanodegree.project1.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.activity.MainSearchActivity;
import com.companionfree.nanodegree.project1.util.ErrorManager;

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
    @InjectView(R.id.toolbar) protected Toolbar toolbar;
    protected View rootView;

    protected String resultsSave = "results";
    protected String errorVisibilitySave = "errorVis";
    protected String errorImageVisibilitySave = "errorImageVis";
    protected String errorTextSave = "errorText";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, rootView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        SpotifyApi api = new SpotifyApi();
        spotifyService = api.getService();

        Activity activity = getActivity();
        if (activity instanceof MainSearchActivity && ((MainSearchActivity) activity).isTwoPane()) { // dual pane mode
            toolbar.setVisibility(View.GONE);
            toolbar = ((MainSearchActivity) activity).getMainToolbar();
        }
        setRetainInstance(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    protected void killRunningTaskIfExists() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }
    protected void displayError(int stringId, boolean statusOnly) {
        Activity activity = getActivity();
        if (activity instanceof MainSearchActivity && ((MainSearchActivity)activity).isTwoPane() && this instanceof ArtistSearchFragment) {
            ((MainSearchActivity)activity).displayError(stringId, statusOnly);
        } else {
            ErrorManager.displayError(recyclerView, errorBlock, errorText, errorImage, getActivity(), stringId, statusOnly);
        }
    }

    protected void removeError() {
        Activity activity = getActivity();
        if (activity instanceof MainSearchActivity && ((MainSearchActivity)activity).isTwoPane()) {
            ((MainSearchActivity)activity).removeError();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            errorBlock.setVisibility(View.GONE);
        }
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

}
