
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.CurrentUserRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.Image;
import dao.Sql2oSpotifyDao;
import dao.Sql2oTicketMasterDao;
import exceptions.ApiException;
import models.User;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import org.apache.commons.lang3.RandomStringUtils;

import javax.jws.Oneway;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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


        final List<String> scopes = Arrays.asList("user-top-read, user-read-private, user-read-email");
        final String state = RandomStringUtils.random(34, true, true);

        // Root - Index
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();

            String authorizeURL = spotifyDao.apiConstructor().createAuthorizeURL(scopes, state);


            model.put("authorizeURL", authorizeURL);
            model.put("user", request.session().attribute("user"));
            model.put("email", request.session().attribute("email"));
//            model.put("topArtistLink", spotifyDao.getTopArtist());
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
        });

        //get top artist
//        get("https://api.spotify.com/v1/me/top/artists", (request, response) -> {
//                    Map<String, Object> model = new HashMap<>();
//
//                    return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
//        });



        get("/profile/:user", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String user = request.params("user");
            boolean authenticated = true;
            model.put("authenticated", authenticated);
            model.put("user", user);
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "profile.hbs"));
        });


        get("/auth", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            final String code = request.queryParams("code");

            final CurrentUserRequest currentUserRequest = spotifyDao.oAuth(code).getMe().build();

            System.out.println(spotifyDao.getTopArtist());

            try {
                final com.wrapper.spotify.models.User user = currentUserRequest.get();

                request.session().attribute("user", user.getId());
                request.session().attribute("email", user.getEmail());
                model.put("user", user.getId());
                model.put("email", user.getEmail());
            } catch (Exception e) {
                System.out.println("Something went wrong!" + e.getMessage());
            }

            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
        });


        // EXCEPTIONS FILTER
        exception(ApiException.class, (exc, req, res) -> {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", exc.getStatusCode());
            jsonMap.put("errorMessage", exc.getMessage());
            res.type("application/json");
            res.status(exc.getStatusCode());
            res.body(gson.toJson(jsonMap));
        });

    }
}
