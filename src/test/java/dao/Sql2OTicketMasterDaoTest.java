package dao;

import models.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;


public class Sql2OTicketMasterDaoTest {

    private Connection con;
    private Sql2oTicketMasterDao ticketMasterDao;

    public Event setupEvent() {
        return new Event("name","ticketMasterId", "url", "localDate", "localTime", "priceRange");
    }

    public Event setupEvent2() {
        return new Event("name2","ticketMasterId2", "url2", "localDate2", "localTime2", "priceRange2");
    }


    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        ticketMasterDao = new Sql2oTicketMasterDao(sql2o);
        con = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        con.close();
    }

//    @Test
//    public void getNextShow() throws Exception {
//    }

    @Test
    public void addEvent_SetsUniqueIdToEvent_True() throws Exception {
        Event testEvent = setupEvent();
        int originalId = testEvent.getId();
        ticketMasterDao.addEvent(testEvent);
        assertNotEquals(originalId, testEvent.getId());
    }

    @Test
    public void findByIdFindsById() throws Exception {
        Event testEvent = setupEvent();
        Event testEvent2 = setupEvent2();
        ticketMasterDao.addEvent(testEvent);
        ticketMasterDao.addEvent(testEvent2);
        int eventId = testEvent.getId();
        int eventId2 = testEvent2.getId();
        assertEquals(testEvent, ticketMasterDao.findById(eventId));
        assertEquals(testEvent2, ticketMasterDao.findById(eventId2));

    }

//    @Test
//    public void getAllEvents() throws Exception {
//    }
//
//    @Test
//    public void deleteById() throws Exception {
//    }
//
//    @Test
//    public void deleteAllEvents() throws Exception {
//    }

}