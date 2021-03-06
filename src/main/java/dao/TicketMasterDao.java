package dao;

import models.*;

import java.util.List;

/**
 * Created by Guest on 1/29/18.
 */
public interface TicketMasterDao {

    //============create

    //get next show in town for an artist
    List<Event> getNextPortlandShow(List<String> artists);

    List<Event> getTonightsPortlandShows();

    List<Event> getShowsForCityOnDay(String location, String date);

    //add event to db to easily track later "bookmark"
    void addEvent(Event event);

    //==============read

    //retrieve a saved event
    Event findById(int EventId);

    //get all events for a user
    List<Event> getAllEvents(int userId);

    //update

    //================delete

    void deleteById(int eventId);

    void deleteAllEvents(int userId);

}
