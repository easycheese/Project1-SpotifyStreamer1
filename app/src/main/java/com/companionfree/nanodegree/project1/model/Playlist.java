package com.companionfree.nanodegree.project1.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 6/19/2015.
 */
public class Playlist implements Parcelable {

    private List<CustomTrack> playlist;
    private int position;

    public Playlist(List<CustomTrack> tracks, int position) {
        this.playlist = new ArrayList<>(tracks);
        this.position = position;
    }

    public List<CustomTrack> getPlaylist() {
        return playlist;
    }

    public CustomTrack getCurrentTrack() {
        return playlist.get(position);
    }
    public CustomTrack skipNext() {
        if (position == playlist.size()) {
            position = 0;
        } else {
            position++;
        }
        return getCurrentTrack();
    }

    public CustomTrack skipPrevious() {
        if (position == 0) {
            position = playlist.size();
        } else {
            position--;
        }
        return getCurrentTrack();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(playlist);
        dest.writeInt(this.position);
    }

    protected Playlist(Parcel in) {
        this.playlist = in.createTypedArrayList(CustomTrack.CREATOR);
        this.position = in.readInt();
    }

    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}
