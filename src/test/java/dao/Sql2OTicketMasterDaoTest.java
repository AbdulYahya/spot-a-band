package dao;

import models.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.*;


public class Sql2OTicketMasterDaoTest {

    private Connection con;
    private Sql2oTicketMasterDao ticketMasterDao;

    public Event setupEvent() {
        return new Event("name","ticketMasterId", "url", "localDate","localTime");
    }

    public Event setupEvent2() {
        return new Event("name2","ticketMasterId2", "url2", "localDate2","localTime");
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

    @Test
    public void getNextShow() throws Exception {
        Event event = ticketMasterDao.getNextShow("weird al");
        assertEquals("Weird Al Yankovic", event.getName());
    }

    @Test
    public void getTonightsShows() throws Exception {
        List<Event> test = ticketMasterDao.getTonightsShows();
        assertEquals(3, test.size());
        assertEquals("Bruce Cockburn", test.get(0).getName());
        assertEquals("Iration - Meet & Greet Packages", test.get(1).getName());
        assertEquals("David Barber W/ Radio Phoenix", test .get(2).getName());
    }

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

    @Test
    public void getAllEvents() throws Exception {
        Event testEvent = setupEvent();
        Event testEvent2 = setupEvent2();
        Event controlEvent = setupEvent();
        controlEvent.setName("control");
        ticketMasterDao.addEvent(testEvent);
        ticketMasterDao.addEvent(testEvent2);
        assertEquals(2, ticketMasterDao.getAllEvents(1).size());
        assertFalse(ticketMasterDao.getAllEvents(1).contains(controlEvent));
    }

    @Test
    public void deleteById() throws Exception {
        Event testEvent = setupEvent();
        Event controlEvent = setupEvent2();
        ticketMasterDao.addEvent(testEvent);
        int testId = testEvent.getId();
        ticketMasterDao.addEvent(controlEvent);
        int controlId = controlEvent.getId();
        assertEquals(2, ticketMasterDao.getAllEvents(1).size());
        ticketMasterDao.deleteById(testId);
        assertEquals(1, ticketMasterDao.getAllEvents(1).size());
        assertFalse(ticketMasterDao.getAllEvents(1).contains(testEvent));
        assertTrue(ticketMasterDao.getAllEvents(1).contains(controlEvent));
    }

    @Test
    public void deleteAllEvents() throws Exception {
        Event testEvent =setupEvent();
        Event secondEvent = setupEvent2();
        ticketMasterDao.addEvent(testEvent);
        ticketMasterDao.addEvent(secondEvent);
        assertEquals(2, ticketMasterDao.getAllEvents(1).size());
        ticketMasterDao.deleteAllEvents(1);
        assertEquals(0, ticketMasterDao.getAllEvents(1).size());
    }
}