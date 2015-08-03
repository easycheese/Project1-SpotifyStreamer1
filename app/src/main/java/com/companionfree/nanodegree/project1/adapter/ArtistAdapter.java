package com.companionfree.nanodegree.project1.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.model.ArtistClickEvent;
import com.companionfree.nanodegree.project1.model.CustomArtist;
import com.companionfree.nanodegree.project1.model.GenericViewHolder;

import java.util.List;

import de.greenrobot.event.EventBus;


public class ArtistAdapter extends RecyclerView.Adapter<GenericViewHolder> implements View.OnClickListener{

    private List<CustomArtist> artistList;

    public ArtistAdapter(List<CustomArtist> trngRecords) {
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
        final CustomArtist artist = artistList.get(i);

        viewHolder.line2.setVisibility(View.GONE);
        viewHolder.line1.setText(artist.name);

        String url = artist.imageURL;
        if (!url.equals(CustomArtist.IMAGES_EMPTY)) {
            Glide.with(viewHolder.albumImage.getContext()).load(url)
                    .centerCrop()
                    .into(viewHolder.albumImage);
        }

        viewHolder.recyclerViewRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ArtistClickEvent(artist.name, artist.id));

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
