package dao;


import models.Artist;

public interface SpotifyDao {
    //CREATE
    void add(Artist artist);

//    Artist getArtistFromSpotify();

    //READ
    Artist findById(int id);

    String getTopArtist();

}
