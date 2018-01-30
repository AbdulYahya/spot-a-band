package dao;


import models.Artist;

public interface SpotifyDao {

    void add(Artist artist);
    Artist findById(int id);

}
