package com.companionfree.nanodegree.project1.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Laptop on 7/9/2015
 */
public class CustomArtist implements Parcelable {

    public String name;
    public String imageURL;
    public String id;

    public static final String IMAGES_EMPTY = "empty";

    public CustomArtist(Artist artist) {
        this.name = artist.name;
        this.id = artist.id;

        if (artist.images.isEmpty()) {
            imageURL = IMAGES_EMPTY;
        } else {
            this.imageURL = artist.images.get(0).url;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeString(this.imageURL);

    }

    protected CustomArtist(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.imageURL = in.readString();

    }

    public static final Parcelable.Creator<CustomArtist> CREATOR = new Parcelable.Creator<CustomArtist>() {
        public CustomArtist createFromParcel(Parcel source) {
            return new CustomArtist(source);
        }

        public CustomArtist[] newArray(int size) {
            return new CustomArtist[size];
        }
    };
}
