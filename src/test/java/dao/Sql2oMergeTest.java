package dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

/**
 * Created by Guest on 1/31/18.
 */
public class Sql2oMergeTest {
    private Connection conn;
    private Sql2oMerge mergeDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        mergeDao = new Sql2oMerge(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void eventsPlaylist_CreatesPlaylistBasedOneEventsInGivenCityOneDate(){
        mergeDao.eventsPlaylist("Portland", "2018-02-01");
    }
}