package com.companionfree.nanodegree.project1.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.companionfree.nanodegree.project1.R;


import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Kyle on 6/6/2015
 */
public class CustomTrack extends Track {

    private int paletteColor = R.color.color_primary;

    public CustomTrack(Track track) {
        this.album = track.album;
        this.artists = track.artists;
        this.duration_ms = track.duration_ms;
        this.id = track.id;
        this.name = track.name;

    }

    public int getPaletteColor() {
        return paletteColor;
    }

    public int getPaletteColorDark() {
        float[] hsv = new float[3];
        int color = getPaletteColor();
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public void generatePaletteColor(final GlideDrawable resource) {
       new Thread(new Runnable() {
           @Override
           public void run() {
               Drawable d  = resource.getCurrent();

               int width = d.getIntrinsicWidth();
               width = width > 0 ? width : 1;
               int height = d.getIntrinsicHeight();
               height = height > 0 ? height : 1;

               Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
               Canvas canvas = new Canvas(bitmap);
               d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
               d.draw(canvas);


               Palette.Builder palette = new Palette.Builder(bitmap);
               Palette p = palette.generate();


               paletteColor = p.getVibrantColor(R.color.color_primary);
           }
       }).run();

    }
}
