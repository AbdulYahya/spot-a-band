

import com.google.gson.*;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.*;
import com.wrapper.spotify.models.*;
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

import java.util.*;

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
        String authdCode = "";
        Sql2oTicketMasterDao ticketMasterDao = new Sql2oTicketMasterDao(sql2o);
        Sql2oSpotifyDao spotifyDao = new Sql2oSpotifyDao(sql2o);
        Sql2oMerge sql2oMerge = new Sql2oMerge(sql2o);

        final List<String> scopes = Arrays.asList("user-top-read, user-read-private, user-read-email, playlist-modify-public");
        final String state = RandomStringUtils.random(34, true, true);

        // Root - Index
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            // Authenticate user by checking if session attribute 'user' holds any data
            if (request.session().attribute("user") != null) {
                //FIND SHOWS BY USER TOP ARTISTS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
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

//        get("/playlist/new", (request, response) -> {
//            Map<String, Object> model = new HashMap<>();
//
////            String inputCity = request.queryParams("inputCity");
////            System.out.println("Input City: "+ inputCity);
////            String inputDate = request.queryParams("inputDate");
////            System.out.println("Input Date: "+ inputDate);
////            sql2oMerge.eventsPlaylist("Portland", "2018-02-01", spotifyDao.getCurrentUser().getDisplayName());
//
//            model.put("user", request.session().attribute("user"));
//            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "embed.hbs"));
//        });

        post("/playlist/new", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            System.out.println("Playlist Route code: " + spotifyDao.getCode());
            Api req = spotifyDao.getSpotifyApi();
            req.setAccessToken(spotifyDao.getAccessToken());
            CurrentUserRequest currentUserRequest = req.getMe().build();
            com.wrapper.spotify.models.User user = spotifyDao.setCurrentUser(currentUserRequest);

//            com.wrapper.spotify.models.User user = spotifyDao.o();
//            final String
//            CurrentUserRequest currentUserRequest = spotifyDao.oAuth(code).getMe().build();
//            com.wrapper.spotify.models.User user = spotifyDao.setCurrentUser(currentUserRequest);
            //BUILD SPOTIFY PLAYLIST BASED ON SUBMITTED CITY AND DATE
            String inputCity = request.queryParams("inputCity");
            System.out.println("Input City: "+ inputCity);
            String inputDate = request.queryParams("inputDate");
            System.out.println("Input Date: "+ inputDate);

            String userId = user.getId();
            String playlistID = "";
//        final PlaylistCreationRequest request = spotifyDao.getSpotifyApi().createPlaylist(userID, "Test 2")
            final PlaylistCreationRequest listRequest = spotifyDao.apiConstructor().createPlaylist(userId, "Test 2")
                    .publicAccess(true)
                    .build();

            System.out.println("creation request: " + listRequest);
            System.out.println(spotifyDao.oAuth(spotifyDao.getCode()));
//        try {
//            Playlist playlist = request.get();
//            playlistID = playlist.getId();
//        } catch (Exception e) {
//            System.out.println("no playlist was made");
//            System.out.println(spotifyDao.oAuth(spotifyDao.getAccessToken()));
//        }

            List<Event> events = ticketMasterDao.getShowsForCityOnDay(inputCity, inputDate);

            try {
                final Playlist playlist = listRequest.get(); //token is likely missing here.

                System.out.println("You just created this playlist!");
                System.out.println("Its title is " + playlist.getName());
            } catch (Exception e) {
                System.out.println("no playlist was made");
            }
            List<String> artistList = new ArrayList<>();
            for (Event event:events) {
                String artist = event.getName();
                ArtistSearchRequest artistSearchRequest = spotifyDao.apiConstructor().searchArtists(artist).market("US").limit(1).build();

                try {
                    final Page<Artist> artistSearchResult = artistSearchRequest.get();
                    final List<com.wrapper.spotify.models.Artist> artists = artistSearchResult.getItems();

                    for (com.wrapper.spotify.models.Artist eachArtist : artists) {
                        artistList.add(eachArtist.getId());
                    }

                } catch (Exception e) {

                }
            }
            for(String artist:artistList){
                try{
                    List<Track> topTracks = TopTracksRequest.builder().id(artist).countryCode("US").build().get();
                    List<String> topTrackIDs = new ArrayList<>();
                    for(Track track: topTracks){
                        topTrackIDs.add(track.getId());
                    }
                    final AddTrackToPlaylistRequest playlistAdd = spotifyDao.apiConstructor().addTracksToPlaylist(user.getId(), playlistID, topTrackIDs).build();

                }catch (Exception e) {
                }
            }

//            sql2oMerge.eventsPlaylist("Portland", "2018-02-01");
//                Playlist playlist = new Playlist();


            model.put("user", request.session().attribute("user"));
//            model.put("playlistId", get()));

            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "embed.hbs"));
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
            spotifyDao.setCode(code);
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
