package dao;

import models.Artist;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

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

}