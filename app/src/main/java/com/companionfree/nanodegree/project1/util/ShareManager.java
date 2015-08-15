package com.companionfree.nanodegree.project1.util;

import android.content.Context;
import android.content.Intent;

import com.companionfree.nanodegree.project1.R;
import com.companionfree.nanodegree.project1.model.CustomTrack;

/**
 * Created by Kyle on 8/15/2015.
 */
public class ShareManager {

    public static Intent getShareDetails(CustomTrack track, Context ctx) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, ctx.getString(R.string.share_url));
        i.putExtra(android.content.Intent.EXTRA_TEXT, "" + track.previewURL);
        return i;
    }
}
