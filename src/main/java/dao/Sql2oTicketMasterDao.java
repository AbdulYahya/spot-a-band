package dao;

import com.google.gson.*;
import models.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Guest on 1/29/18.
 */
public class Sql2oTicketMasterDao implements TicketMasterDao {

    private final Sql2o sql2o;
    Gson gson = new Gson();

    public Sql2oTicketMasterDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public String getTomorrow(){
        String datePatternToUse = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(datePatternToUse);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        Date tomorrow = calendar.getTime();
        return  sdf.format(tomorrow).replaceAll("/", "-");
    }


    @Override
    public Event getNextShow(String artistName) {
        //event to be returned

        Event event = new Event("", "", "", "","");

        //API call url split into parameters:git
        String route = "https://app.ticketmaster.com/discovery/v2/events.json?";
        String classificationName = "&classificationName=music";
        String artist = String.format("&keyword=%s", artistName);
        String marketId = "&dmaId=362";
        String apiKey = "&apikey=UVOeCoYG9hwSCSiAfubUzl9vGGM1dXTx";

        //assembled url:
        String apiRequest = (route + classificationName + artist + marketId + apiKey).replaceAll(" ", "+");

        //connect to ticketmaster api
        try {
            URL url = new URL(apiRequest);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(new InputStreamReader((InputStream) request.getContent()));
            //get most data from first object in "events" array in json response
            JsonObject apiResponse = json.getAsJsonObject()
                    .getAsJsonObject("_embedded")
                    .getAsJsonArray("events")
                    .get(0)
                    .getAsJsonObject();
            //date and time are objects inside the events array and require reaching down further into Json data
            JsonObject date = json.getAsJsonObject()
                    .getAsJsonObject("_embedded")
                    .getAsJsonArray("events")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("dates")
                    .getAsJsonObject("start");
            JsonPrimitive time = json.getAsJsonObject()
                    .getAsJsonObject("_embedded")
                    .getAsJsonArray("events")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("dates")
                    .getAsJsonObject("start")
                    .getAsJsonPrimitive("localTime");

            //this is getting null pointer exception
//            if (json.getAsJsonObject().getAsJsonObject("_embedded").getAsJsonArray("events").get(0).getAsJsonObject().getAsJsonArray("priceRanges").get(0).getAsJsonObject() != null) {
//                priceRange = json.getAsJsonObject().getAsJsonObject("_embedded").getAsJsonArray("events").get(0).getAsJsonObject().getAsJsonArray("priceRanges").get(0).getAsJsonObject();
//            }

            //set event parameters
            event.setName(apiResponse.get("name").getAsString());
            event.setTicketMasterId(apiResponse.get("id").getAsString());
            event.setUrl(apiResponse.get("url").getAsString());
            event.setLocalDate(date.get("localDate").getAsString());
            event.setLocalTime(time.toString());

            //getting null pointer exception
//            if (priceRange != null) {
//                event.setPriceRange(priceRange.get("min").getAsString() + "to" + priceRange.get("max").getAsString());
//            }


        }catch (IOException e) {
            e.printStackTrace();
        }
        return event;
    }

    @Override
    public List<Event> getTonightsShows() {
        //list of events to be returned
        List<Event> tonightsShows = new ArrayList<>();


        //build apiRequest url
        //API call url split into parameters:git
        String route = "https://app.ticketmaster.com/discovery/v2/events.json?";
        String classificationName = "&classificationName=music";
        String endDateTime = String.format("&endDateTime=%s%s", getTomorrow(), "T00:00:00Z");
        String marketId = "&dmaId=362";
        String apiKey = "&apikey=UVOeCoYG9hwSCSiAfubUzl9vGGM1dXTx";

        //assembled url:
        String apiRequest = (route + classificationName + endDateTime + marketId + apiKey).replaceAll(" ", "+");
        try {
            URL url = new URL(apiRequest);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(new InputStreamReader((InputStream) request.getContent()));
            //get array of tonight's events in json format
            JsonArray eventsArray = json.getAsJsonObject()
                    .getAsJsonObject("_embedded")
                    .getAsJsonArray("events");

            //loop through events array and add each event to list
            for (int i = 0; i < eventsArray.size(); i++) {
                Event event = new Event("", "", "", "", "");
                JsonObject apiResponse = json.getAsJsonObject()
                        .getAsJsonObject("_embedded")
                        .getAsJsonArray("events")
                        .get(i)
                        .getAsJsonObject();
                JsonObject date = json.getAsJsonObject()
                        .getAsJsonObject("_embedded")
                        .getAsJsonArray("events")
                        .get(i).getAsJsonObject()
                        .getAsJsonObject("dates")
                        .getAsJsonObject("start");
                JsonPrimitive time = json.getAsJsonObject()
                        .getAsJsonObject("_embedded")
                        .getAsJsonArray("events")
                        .get(i).getAsJsonObject()
                        .getAsJsonObject("dates")
                        .getAsJsonObject("start")
                        .getAsJsonPrimitive("localTime");
                event.setName(apiResponse.get("name").getAsString());
                event.setTicketMasterId(apiResponse.get("id").getAsString());
                event.setUrl(apiResponse.get("url").getAsString());
                event.setLocalDate(date.get("localDate").getAsString());
                event.setLocalTime(time.toString());
                tonightsShows.add(event);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return tonightsShows;
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
        String sql = "SELECT * FROM events";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch(Event.class);
        }
    }

    @Override
    public void deleteById(int eventId) {
        String sql = "DELETE FROM events where id = :id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", eventId)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void deleteAllEvents(int userId) {
            String sql = "DELETE FROM events";
            try (Connection con = sql2o.open()){
                con.createQuery(sql)
                        .executeUpdate();
            } catch (Sql2oException ex){
                System.out.println(ex);
            }
        }

    }

