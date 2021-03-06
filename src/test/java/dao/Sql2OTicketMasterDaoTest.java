package dao;

import models.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class Sql2OTicketMasterDaoTest {

    private Connection con;
    private Sql2oTicketMasterDao ticketMasterDao;

    public Event setupEvent() {
        return new Event("name","ticketMasterId", "url", "localDate","localTime", "priceRange", "venue", "venueUrl", "image");
    }

    public Event setupEvent2() {
        return new Event("name2","ticketMasterId2", "url2", "localDate2","localTime", "priceRange2", "venue2", "venueUrl2", "image2");
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
    public void getNextPortlandShow() throws Exception {
        List<String> artists = new ArrayList<>();
        artists.add("weird al");
        artists.add("faye carol");
        artists.add("eagles");
        artists.add("muse");
        List<Event> nextShows = ticketMasterDao.getNextPortlandShow(artists);
        assertEquals("Weird Al Yankovic", nextShows.get(0).getName());
        assertEquals("2018-05-25", nextShows.get(0).getLocalDate());
        assertEquals("Faye Carol", nextShows.get(1).getName());
        assertEquals("2018-02-17", nextShows.get(1).getLocalDate());
        assertEquals("Eagles", nextShows.get(2).getName());
        assertEquals("2018-05-05", nextShows.get(2).getLocalDate());
        assertEquals("muse", nextShows.get(3).getName());
        assertEquals("no upcoming shows", nextShows.get(3).getLocalDate());
    }

    @Test
    public void getTonightsPortlandShows() throws Exception {
        List<Event> test = ticketMasterDao.getTonightsPortlandShows();
        assertEquals(6, test.size());
        assertEquals("Drive By Truckers", test.get(0).getName());
        assertEquals("Elephant Revival", test.get(1).getName());
        assertEquals("Eric Johnson", test .get(2).getName());
        assertEquals("Roseland Theater", test.get(2).getVenue());
        assertEquals("Jessica Lea Mayfield", test .get(3).getName());
        assertEquals("Mel Brown B-3 Organ Group", test .get(4).getName());
        assertEquals("Rebecca Kilgore", test .get(5).getName());
    }

    @Test
    public void getShowsForCityOnDay() throws Exception {
        List<Event> test = ticketMasterDao.getShowsForCityOnDay("portland", "2018-3-25");
        assertEquals(4, test.size());
        assertEquals("Lindi Ortega", test.get(0).getName());
        assertEquals("Milk & Bone", test.get(1).getName());
        assertEquals("Orchestral Manoeuvers in the Dark", test.get(2).getName());
        assertEquals("Hamilton", test.get(3).getName());
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