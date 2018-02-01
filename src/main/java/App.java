

import com.google.gson.*;
import com.wrapper.spotify.methods.CurrentUserRequest;
import com.wrapper.spotify.models.Image;
import dao.Sql2oMerge;
import dao.Sql2oSpotifyDao;
import dao.Sql2oTicketMasterDao;
import exceptions.ApiException;
import models.Event;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");
//        Sql2oSpotifyDao sql2oSpotifyDao;
        Connection conn;
        Gson gson = new Gson();
        String connectionString = "jdbc:h2:~/spot-a-band.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        Sql2oSpotifyDao sql2oSpotifyDao = new Sql2oSpotifyDao(sql2o);
        conn = sql2o.open();

        Sql2oTicketMasterDao ticketMasterDao = new Sql2oTicketMasterDao(sql2o);
        Sql2oSpotifyDao spotifyDao = new Sql2oSpotifyDao(sql2o);
        Sql2oMerge sql2oMerge = new Sql2oMerge(sql2o);

        final List<String> scopes = Arrays.asList("user-top-read, user-read-private, user-read-email");
        final String state = RandomStringUtils.random(34, true, true);

        // Root - Index
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            // Authenticate user by checking if session attribute 'user' holds any data
            if (request.session().attribute("user") != null) {
                List<String> artists = spotifyDao.getTopArtist();
                List<Event> eventList = ticketMasterDao.getNextPortlandShow(artists);

                for (Event event : eventList) {
                    if (event.getLocalDate().equals("no upcoming shows")) {
                        System.out.println("The artist " + event.getName() + "has" + event.getLocalDate() + "|");
                       // model.put("noShow", event.getLocalDate());
                    } else {
                        System.out.println("The artist " + event.getName() + " has a show");
                    }
                }

                model.put("user", request.session().attribute("user"));
                model.put("profileImage", request.session().attribute("profileImage"));
                model.put("eventList", eventList);

                return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
            } else {
                response.redirect("/signin"); // User auth failed - send user back to signin page

            }

            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "signin.hbs"));
        });

        get("/signin", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String authorizeURL = spotifyDao.apiConstructor().createAuthorizeURL(scopes, state);

            model.put("authorizeURL", authorizeURL);
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "signin.hbs"));
        });



        get("/auth", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            final String code = request.queryParams("code");

            CurrentUserRequest currentUserRequest = spotifyDao.oAuth(code).getMe().build();
            com.wrapper.spotify.models.User user = spotifyDao.setCurrentUser(currentUserRequest);

            // If user has a profile image
            if (user.getImages() != null) {
                List<Image> images = user.getImages();

                for (Image image : images) {
                    System.out.println(image.getUrl());

                    model.put("profileImage", image.getUrl());
                    request.session().attribute("profileImage", image.getUrl());
                }
            }

            if (user.getDisplayName() != null) {
                model.put("user", user.getDisplayName());
                request.session().attribute("user", user.getDisplayName());
            } else {
                model.put("user", spotifyDao.formatUserEmail(user.getEmail()));
                request.session().attribute("user", spotifyDao.formatUserEmail(user.getEmail()));
            }

            response.redirect("/");
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
        });

        /*
        *   Dynamic Routes
        * */
        get("/profile/:user", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String user = request.params("user");
            boolean authenticated = true;
            model.put("authenticated", authenticated);
            model.put("user", user);
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "profile.hbs"));
        });

        // BEFORE FILTER
//        before((request, response) -> {
//            boolean isAuthenticated = false;
//            String authorizeURL = spotifyDao.apiConstructor().createAuthorizeURL(scopes, state);
//
//         //   if (spotifyDao.getCode() == null) {
////            if (!isAuthenticated) {
////                try {
////                    URL authRequest = new URL(authorizeURL);
////                    HttpURLConnection getRequest = (HttpURLConnection) authRequest.openConnection();
////
////                    getRequest.setRequestMethod("GET");
////
////                    if (getRequest.getResponseCode() == HttpURLConnection.HTTP_OK) {
////                        isAuthenticated = true;
////                        response.redirect("/auth");
////                    }
////
////                    System.out.println(getRequest.getResponseCode());
////                    System.out.println(getRequest.getResponseMessage());
////                    System.out.println(getRequest.getHeaderFields());
////                    System.out.println(spotifyDao.getCode());
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            } else {
////                isAuthenticated = false;
////            }
//        });
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
