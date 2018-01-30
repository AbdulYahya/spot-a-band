
import com.google.gson.Gson;
import dao.Sql2oSpotifyDao;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

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

        // Root - Index
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
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

        // FILTERS
        before((request, response) -> {
            Map<String, Object> model = new HashMap<>();
           boolean authenticated = false;

            /*
            *  Check if user is Authenticated
            * */

            if (!authenticated) {
                halt(401, new HandlebarsTemplateEngine().render(new ModelAndView(model, "signin.hbs")));
            }
        });
    }
}
