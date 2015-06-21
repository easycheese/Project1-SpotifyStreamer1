package com.companionfree.nanodegree.project1.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.model.CustomTrack;
import com.companionfree.nanodegree.project1.model.GenericViewHolder;
import com.companionfree.nanodegree.project1.model.Playlist;
import com.companionfree.nanodegree.project1.model.SongClickEvent;

import java.util.List;

import de.greenrobot.event.EventBus;


public class TopSongsAdapter extends RecyclerView.Adapter<GenericViewHolder> implements View.OnClickListener{

    private List<CustomTrack> trackList;

    public TopSongsAdapter(List<CustomTrack> trngRecords) {
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
        final CustomTrack track = trackList.get(i);

        viewHolder.line1.setText(track.trackName);
        viewHolder.line2.setText(track.albumName);


        if (!track.albumURL.equals(CustomTrack.ALBUM_EMPTY)) {
            Glide.with(viewHolder.albumImage.getContext()).load(track.albumURL)
                    .centerCrop()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            track.generatePaletteColor(resource);
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.albumImage);
        }

        viewHolder.recyclerViewRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Playlist playlist = new Playlist(trackList, i);
                EventBus.getDefault().post(new SongClickEvent(playlist));

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
