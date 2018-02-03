

import com.google.gson.*;
import com.wrapper.spotify.methods.CurrentUserRequest;
import com.wrapper.spotify.methods.PlaylistCreationRequest;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.Image;

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
        Sql2oTicketMasterDao ticketMasterDao = new Sql2oTicketMasterDao(sql2o);
        Sql2oSpotifyDao spotifyDao = new Sql2oSpotifyDao(sql2o);
        Sql2oMerge sql2oMerge = new Sql2oMerge(sql2o);

        final List<String> scopes = Arrays.asList("user-top-read, user-read-private, user-read-email, playlist-modify-public");
        final String state = RandomStringUtils.random(34, true, true);

        // Root - Index
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String authorizeURL = spotifyDao.apiConstructor().createAuthorizeURL(scopes, state);
            // Authenticate user by checking if session attribute 'user' holds any data
            if (request.session().attribute("user") != null) {
                //FIND SHOWS BY USER TOP ARTISTS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
                List<String> artists = spotifyDao.getTopArtist();
                List<Event> eventList = ticketMasterDao.getNextPortlandShow(artists);
                request.session().attribute("eventList", eventList);

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
                model.put("playlistId", request.session().attribute("playlistId"));

                return new HandlebarsTemplateEngine().render(new ModelAndView(model, "index.hbs"));
            } else {
                response.redirect("/signin"); // User auth failed - send user back to signin page

            }

            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "signin.hbs"));
        });


        post("/playlist/new", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String authorizeURL = spotifyDao.apiConstructor().createAuthorizeURL(scopes, state);

            //GET PARAMETERS FROM HTML FORM
            String inputCity = request.queryParams("inputCity");
            System.out.println("Input City: "+ inputCity);
            String inputDate = request.queryParams("inputDate");
            System.out.println("Input Date: "+ inputDate);
            String playlistId = new String();

            // BUILD LIST OF SONG IDS
            List<String> tracksToAdd = new ArrayList<>();
            List<Event> events = ticketMasterDao.getShowsForCityOnDay(inputCity, inputDate);
            System.out.println(events.size());
            for (Event event : events) {
                System.out.println(event.getName());
                List<String> artistIds = new ArrayList<>();
                final ArtistSearchRequest artistSearchRequest = spotifyDao.getSpotifyApi().searchArtists(event.getName()).market("US").limit(1).build();
                try {
                    final Page<Artist> artistSearchResult = artistSearchRequest.get();
                    final List<Artist> artists = artistSearchResult.getItems();
                    artistIds.add(artists.get(0).getId());
                } catch (Exception e) {
                    System.out.println("No artist found ::" + e);
                }
                System.out.println(artistIds.size());
                for(String id : artistIds){
                    final TopTracksRequest topTracksRequest = spotifyDao.getSpotifyApi().getTopTracksForArtist(id, "US").build();
                    try{
                        final List<Track> topTracks = topTracksRequest.get();
                        for(int i = 0; i < 6; i++){
                            tracksToAdd.add("spotify:track:"+topTracks.get(i).getId());
                        }
                    }catch (Exception e) {
                        System.out.println("no tracks");
                    }
                }
                System.out.println(tracksToAdd);
            }


            //CREATE A PLAAYLIST FOR INPUTCITY ON INPUTDATE
            final PlaylistCreationRequest creationRequest = spotifyDao.getSpotifyApi().createPlaylist(request.session().attribute("id"), "Bands playing in " + inputCity + " on " + inputDate).publicAccess(true).build();
            try {
                final Playlist playlist = creationRequest.get();
                playlistId = playlist.getId();
            } catch (Exception e) {
                System.out.println("playlist generation failed");
            }
            request.session().attribute("playlistId", playlistId);



            //ADD TRACKS TO PLAYLIST
            final int insertIndex = 0;   // Index starts at 0
            final AddTrackToPlaylistRequest addTrackToPlaylistRequest = spotifyDao.getSpotifyApi().addTracksToPlaylist(request.session().attribute("id"), playlistId, tracksToAdd)
                    .position(insertIndex)
                    .build();
            try {
                addTrackToPlaylistRequest.get();
            } catch (Exception e) {
                System.out.println("add tracks failed" + e.getMessage());
            }


            model.put("authorizeURL", authorizeURL);
            model.put("user", request.session().attribute("user"));
            model.put("profileImage", request.session().attribute("profileImage"));
            model.put("playlistId", playlistId);
            model.put("eventList", request.session().attribute("eventList"));
            model.put("id", request.session().attribute("id"));
            model.put("city", inputCity);
            model.put("date", inputDate);
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
                request.session().attribute("id", user.getId());
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

        // BEFORE FILTER
        before((request, response) -> {
           if (spotifyDao.getAccessToken() != null) {
               response.header("access_token", spotifyDao.getAccessToken());
               response.header("token_type", "Bearer");
//               request.headers()
           }
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
