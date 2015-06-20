package com.companionfree.nanodegree.project1.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.companionfree.nanodegree.project1.R;


import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Kyle on 6/6/2015
 */
public class CustomTrack implements Parcelable {

//    public static final String SONG_URL = "song_uri";
    private int paletteColor = R.color.color_primary;

    public String albumName;
    public String albumURL;
    public String artistName;
    public long duration;
    public String id;
    public String trackName;
    public String previewURL;

    public static final String ALBUM_EMPTY = "empty";

    public CustomTrack(Track track) {
        this.albumName = track.album.name;
        this.artistName = track.artists.get(0).name;
        this.duration = track.duration_ms;
        this.id = track.id;
        this.trackName = track.name;
        this.previewURL = track.preview_url;

        if (!track.album.images.isEmpty()) {
            albumURL = track.album.images.get(0).url;
        } else {
            albumURL = ALBUM_EMPTY;
        }


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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.paletteColor);
        dest.writeString(this.albumName);
        dest.writeString(this.albumURL);
        dest.writeString(this.artistName);
        dest.writeLong(this.duration);
        dest.writeString(this.id);
        dest.writeString(this.trackName);
        dest.writeString(this.previewURL);
    }

    protected CustomTrack(Parcel in) {
        this.paletteColor = in.readInt();
        this.albumName = in.readString();
        this.albumURL = in.readString();
        this.artistName = in.readString();
        this.duration = in.readLong();
        this.id = in.readString();
        this.trackName = in.readString();
        this.previewURL = in.readString();
    }

    public static final Parcelable.Creator<CustomTrack> CREATOR = new Parcelable.Creator<CustomTrack>() {
        public CustomTrack createFromParcel(Parcel source) {
            return new CustomTrack(source);
        }

        public CustomTrack[] newArray(int size) {
            return new CustomTrack[size];
        }
    };
}
