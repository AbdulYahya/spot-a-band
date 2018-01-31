package dao;

import com.wrapper.spotify.models.Playlist;
import models.Artist;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.*;


public class Sql2oSpotifyDaoTest {
    private Connection conn;
    private Sql2oSpotifyDao spotifyDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        spotifyDao = new Sql2oSpotifyDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addArtist() throws Exception {
        Artist artist = new Artist("ABC", "asdasdafawe2132");
        spotifyDao.add(artist);
        assertEquals(1, artist.getId());
    }

    @Test
    public void findById() throws Exception {
        Artist artist = new Artist("ABC", "asdasdafawe2132");
        Artist otherArtist = new Artist("DEF", "dadaaw132");
        spotifyDao.add(artist);
        spotifyDao.add(otherArtist);
        assertEquals(2, spotifyDao.findById(2).getId());
    }

    @Test
    public void getTopArtist_returnsListOfStrings() throws Exception{
        assert(spotifyDao.getTopArtist() != null);
    }
}