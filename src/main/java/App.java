
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dao.Sql2oSpotifyDao;
import dao.Sql2oTicketMasterDao;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");
        Sql2oSpotifyDao sql2oSpotifyDao;
        Connection conn;
        Gson gson = new Gson();
        String connectionString = "jdbc:h2:~/spot-a-band.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        sql2oSpotifyDao = new Sql2oSpotifyDao(sql2o);
        conn = sql2o.open();

        Sql2oTicketMasterDao ticketMasterDao = new Sql2oTicketMasterDao(sql2o);
        Sql2oSpotifyDao spotifyDao = new Sql2oSpotifyDao(sql2o);

        // Root - Index
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            /*
            *   Below auth/user vars are used for testing header-nav only
            * */
            boolean authenticated = true;
            String user = "Abdul";

            if (authenticated) {
                model.put("authenticated", authenticated);
                model.put("user", user);
            }

            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
        });

        //get top artist
        get("https://api.spotify.com/v1/me/top/artists", (request, response) -> {
                    Map<String, Object> model = new HashMap<>();

                    //big JSON goes here
                    //artists = json.queryParams("artists");
                    // a miracle occurs to turn artists into an array
                    //

                    return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
        });

        get("/signin", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "signin.hbs"));
        });

        get("/profile/:user", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String user = request.params("user");
            boolean authenticated = true;
            model.put("authenticated", authenticated);
            model.put("user", user);
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "profile.hbs"));
        });

        // FILTERS
        before((request, response) -> {
            Map<String, Object> model = new HashMap<>();
            boolean authenticated = true;
            /*
            *  Logic to check if user is Authenticated
            * */
            if (!authenticated) {
                halt(401, new HandlebarsTemplateEngine().render(new ModelAndView(model, "signin.hbs")));
            }
        });


        String route = "https://api.spotify.com/v1/me/top/artists";
        String accessToken = "BQDZl9FBQI4DnN8hWp0dQFbqzKzMWFQfsgZEw9WmD747O-Nlyq5K3tshegdS8JwTz8pjep4ukT6VEbbe8D64PXfhGqkVYt5Oi_izAwkDAE90KwDajbX9X-5BuxTQTZAomnJSPE1MRPGaVlsHiIUGtlyy";

        try {
            URL url = new URL(route);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            request.setRequestMethod("GET");
            request.setRequestProperty("Authorization", "Bearer " + accessToken);

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));

            JsonObject response = jsonElement.getAsJsonObject()
                    .getAsJsonArray("items")
                    .get(0).getAsJsonObject();

//            JsonObject response =
            System.out.println(ticketMasterDao.getNextShow("Faye Carol").getLocalDate());
            System.out.println(spotifyDao.getTopArtist());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
