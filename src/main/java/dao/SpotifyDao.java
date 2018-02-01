package dao;


import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.CurrentUserRequest;
import com.wrapper.spotify.models.User;
import models.Artist;

import java.util.List;
import java.util.Properties;

public interface SpotifyDao {
    //CREATE
    void add(Artist artist);

    Api apiConstructor();
    Api oAuth(String code); // Move authorization methods from App.java to here

    User setCurrentUser(CurrentUserRequest currentUserRequest);
    User getCurrentUser();

    String getCode();
    String getAccessToken();

    void setAccessToken(String accessToken);

//    Artist getArtistFromSpotify();

    //READ
    Artist findById(int id);

    List<String> getTopArtist();

    StringBuilder formatUserId(String userId);
    StringBuilder formatUserEmail(String userEmail);

    Properties loadProperties();

}
