package com.companionfree.nanodegree.project1.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.activity.TopSongsActivity;
import com.companionfree.nanodegree.project1.fragment.TopSongsFragment;
import com.companionfree.nanodegree.project1.model.GenericViewHolder;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;


public class ArtistAdapter extends RecyclerView.Adapter<GenericViewHolder> implements View.OnClickListener{

    private List<Artist> artistList;

    public ArtistAdapter(List<Artist> trngRecords) {
        this.artistList = trngRecords;

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
        final Artist artist = artistList.get(i);

        viewHolder.line2.setVisibility(View.GONE);
        viewHolder.line1.setText(artist.name);

        if (!artist.images.isEmpty()) {
            Image image = artist.images.get(0);
            Glide.with(viewHolder.albumImage.getContext()).load(image.url)
                    .centerCrop()
                    .into(viewHolder.albumImage);
        }

        viewHolder.recyclerViewRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = viewHolder.recyclerViewRow.getContext();
                Intent i = new Intent(ctx, TopSongsActivity.class);
                i.putExtra(TopSongsFragment.ARTIST_NAME, artist.name);
                i.putExtra(TopSongsFragment.ARTIST_ID, artist.id);
                ctx.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }


    @Override
    public void onClick(View v) {

    }
}
