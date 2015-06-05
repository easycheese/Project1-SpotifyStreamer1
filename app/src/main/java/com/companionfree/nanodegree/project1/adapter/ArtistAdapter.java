package com.companionfree.nanodegree.project1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.model.GenericViewHolder;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;


public class ArtistAdapter extends RecyclerView.Adapter<GenericViewHolder> implements View.OnClickListener{

    private List<Artist> artistList;
    private Context ctx;

    public ArtistAdapter(List<Artist> trngRecords, Context ctx) {
        this.artistList = trngRecords;
        this.ctx = ctx;

    }

    @Override
    public GenericViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recyclerview_row, viewGroup, false);

        return new GenericViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GenericViewHolder viewHolder, int i) {
        final Artist artist = artistList.get(i);

        viewHolder.line2.setVisibility(View.GONE);
        viewHolder.line1.setText(artist.name);

        if (!artist.images.isEmpty()) {
            Image image = artist.images.get(0);
            Glide.with(ctx).load(image.url)
//                    .centerCrop()
                    .into(viewHolder.albumImage);
        }

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }


    @Override
    public void onClick(View v) {

    }
}
