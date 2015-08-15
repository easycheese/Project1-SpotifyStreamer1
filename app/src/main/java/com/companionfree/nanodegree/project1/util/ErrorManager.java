package com.companionfree.nanodegree.project1.util;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.companionfree.nanodegree.project1.R;

/**
 * Created by Kyle on 8/15/2015
 */
public class ErrorManager {

    public static void displayError(View infoView, View errorBlock, TextView errorText, ImageView errorImage, Context ctx, int errorStringId, boolean statusOnly) {
        infoView.setVisibility(View.GONE);
        errorBlock.setVisibility(View.VISIBLE);
        errorText.setText(ctx.getString(errorStringId));

        int vis = (statusOnly) ? View.GONE : View.VISIBLE;
        errorImage.setVisibility(vis);
    }

    public static void displayNetworkPlayerErrorToast(Context ctx) {
        Toast.makeText(ctx, ctx.getString(R.string.error_network_availability), Toast.LENGTH_SHORT).show();
    }

}
