package com.companionfree.nanodegree.project1.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.activity.SongActivity;
import com.companionfree.nanodegree.project1.fragment.SingleSongFragment;
import com.companionfree.nanodegree.project1.model.GenericViewHolder;
import com.google.gson.Gson;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;


public class TrackAdapter extends RecyclerView.Adapter<GenericViewHolder> implements View.OnClickListener{

    private List<Track> trackList;

    public TrackAdapter(List<Track> trngRecords) {
        this.trackList = trngRecords;

    }

    @Override
    public GenericViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recyclerview_row, viewGroup, false);

        return new GenericViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final GenericViewHolder viewHolder, final int i) {
        final Track track = trackList.get(i);

        viewHolder.line1.setText(track.name);
        viewHolder.line2.setText(track.album.name);


        if (!track.album.images.isEmpty()) {
            Image image = track.album.images.get(0);
            Glide.with(viewHolder.albumImage.getContext()).load(image.url)
                    .centerCrop()
                    .into(viewHolder.albumImage);
        }

        viewHolder.recyclerViewRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = viewHolder.recyclerViewRow.getContext();
                Intent i = new Intent(ctx, SongActivity.class);
                i.putExtra(SingleSongFragment.TRACK, new Gson().toJson(track));
                ctx.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }


    @Override
    public void onClick(View v) {

    }
}
