package com.companionfree.nanodegree.project1.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Kyle on 6/3/2015.
 */
public class SongViewHolder extends RecyclerView.ViewHolder {

    public ImageView albumImage;
    public TextView songTitle;
    public TextView artistName;

    public SongViewHolder(View itemView) {
        super(itemView);
    }
}
