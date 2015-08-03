package com.companionfree.nanodegree.project1.model;

/**
 * Created by Kyle on 6/17/2015
 */
public class ArtistClickEvent {
    public final String artistName;
    public final String artistId;

    public ArtistClickEvent(String artistName, String id) {
        this.artistName = artistName;
        this.artistId = id;
    }
}
