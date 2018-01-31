package dao;


import com.wrapper.spotify.Api;
import models.Artist;

import java.util.List;
import java.util.Properties;

public interface SpotifyDao {
    //CREATE
    void add(Artist artist);

    void createPlaylist(String name);
    void addTracks(List<String> artists, int top);

    Api apiConstructor();
    Api oAuth(String code); // Move authorization methods from App.java to here

    String getCode();
    String getAccessToken();

    void setAccessToken(String accessToken);

//    Artist getArtistFromSpotify();

    //READ
    Artist findById(int id);

    String getTopArtist();

    Properties loadProperties();

}
