package dao;

import models.*;
import org.sql2o.Sql2o;

import java.util.List;

/**
 * Created by Guest on 1/29/18.
 */
public class Sql2oTicketMasterDAO implements TicketMasterDao {

    private final Sql2o sql2o;

    public Sql2oTicketMasterDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }


    @Override
    public Event getNextShow(String artistName) {
        return null;
    }

    @Override
    public void addEvent(Event event, int userId) {
    }

    @Override
    public Event findById(int eventId) {
        return null;
    }

    @Override
    public List<Event> getAllEvents(int UserId) {
        return null;
    }

    @Override
    public void deleteById(int eventId) {

    }

    @Override
    public void deleteAllEvents(int userId) {

    }
}
