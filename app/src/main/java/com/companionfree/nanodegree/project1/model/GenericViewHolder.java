package com.companionfree.nanodegree.project1.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.companionfree.nanodegree.project1.R;


/**
 * Created by Kyle on 6/3/2015
 */
public class GenericViewHolder extends RecyclerView.ViewHolder {

    public View recyclerViewRow;
    public ImageView albumImage;
    public TextView line1;
    public TextView line2;

    public GenericViewHolder(View itemView) {
        super(itemView);

        recyclerViewRow = itemView;
        albumImage = (ImageView) itemView.findViewById(R.id.record_row_image);
        line1 = (TextView) itemView.findViewById(R.id.record_row_holder_line1);
        line2 = (TextView) itemView.findViewById(R.id.record_row_holder_line2);

    }



}
