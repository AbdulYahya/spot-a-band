package dao;

import models.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

/**
 * Created by Guest on 1/29/18.
 */
public class Sql2oTicketMasterDao implements TicketMasterDao {

    private final Sql2o sql2o;

    public Sql2oTicketMasterDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }


    @Override
    public Event getNextShow(String artistName) {
        return null;
    }

    @Override
    public void addEvent(Event event) {
        String sql = "INSERT INTO events (name, ticketMasterId, url, localDate, localTime, priceRange) VALUES (:name, :ticketMasterId, :url, :localDate, :localTime, :priceRange)";
        try (Connection con = sql2o.open()){
            int id = (int) con.createQuery(sql)
                    .bind(event)
                    .executeUpdate()
                    .getKey();
            event.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public Event findById(int eventId) {
        String sql = "SELECT * FROM events WHERE id = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", eventId)
                    .executeAndFetchFirst(Event.class);
        }
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
