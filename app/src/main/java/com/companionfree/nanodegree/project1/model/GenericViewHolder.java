package com.companionfree.nanodegree.project1.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.companionfree.nanodegree.project1.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Kyle on 6/3/2015
 */
public class GenericViewHolder extends RecyclerView.ViewHolder {

    public View recyclerViewRow;

    @InjectView(R.id.record_row_image) public ImageView albumImage;
    @InjectView(R.id.record_row_holder_line1) public TextView line1;
    @InjectView(R.id.record_row_holder_line2) public TextView line2;

    public GenericViewHolder(View itemView) {
        super(itemView);

        recyclerViewRow = itemView;
        ButterKnife.inject(this, itemView);

    }
}
