package dao;


import models.Artist;

public interface SpotifyDao {
    //CREATE
    void add(Artist artist);

    String oAuth(String code); // Move authorization methods from App.java to here
    String getCode();

//    Artist getArtistFromSpotify();

    //READ
    Artist findById(int id);

    String getTopArtist();

}
