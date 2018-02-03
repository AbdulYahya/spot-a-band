package dao;

import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.*;
import com.wrapper.spotify.models.*;
import com.google.gson.*;
import models.Artist;
import org.eclipse.jetty.client.HttpConnection;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static spark.Spark.connect;
import static spark.Spark.get;

public class Sql2oSpotifyDao implements SpotifyDao {
    private final Sql2o sql2o;
    private User currentUser;
    private String code;
    private String accessToken;
    private Api spotifyApi;

    public Sql2oSpotifyDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Artist artist) {
        String sql = "INSERT INTO artists (artistName, spotifyId) VALUES (:artistName, :spotifyId)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql)
                    .bind(artist)
                    .executeUpdate()
                    .getKey();
            artist.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public Api apiConstructor() {
        return Api.builder()
                .clientId(loadProperties().getProperty("SpotifyClientId"))
                .clientSecret(loadProperties().getProperty("SpotifyClientSecret"))
                .redirectURI(loadProperties().getProperty("SpotifyRedirectURI"))
                .build();
    }

    @Override
    public Api oAuth(String code) {
        this.code = code;
        Api api = apiConstructor();

        setSpotifyApi(apiConstructor());

        final SettableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = api.authorizationCodeGrant(code).build().getAsync();

            /* Add callbacks to handle success and failure */
        Futures.addCallback(authorizationCodeCredentialsFuture, new FutureCallback<AuthorizationCodeCredentials>() {
            @Override
            public void onSuccess(AuthorizationCodeCredentials authorizationCodeCredentials) {

                /* Set the access token and refresh token so that they are used whenever needed */
                api.setAccessToken(authorizationCodeCredentials.getAccessToken());
                api.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

                setAccessToken(authorizationCodeCredentials.getAccessToken());
            }

            @Override
            public void onFailure(Throwable throwable) {
                /* Let's say that the client id is invalid, or the code has been used more than once,
                 * the request will fail. Why it fails is written in the throwable's message. */

            }
        });

        setSpotifyApi(api);

        return api;
    }

    @Override
    public User setCurrentUser(CurrentUserRequest currentUserRequest) {
        try {
            this.currentUser = currentUserRequest.get();
        } catch (Exception e) {
            System.out.println("currentUser Method Failed!");
            e.printStackTrace();
        }
        return this.currentUser;
    }

    @Override
    public User getCurrentUser() {
//        CurrentUserRequest currentUserRequest = oAuth(getCode()).getMe().build();
//        try {
//            this.currentUser = currentUserRequest.get();
//        } catch (Exception e) {
//            System.out.println("currentUser Method Failed!");
//            e.printStackTrace();
//        }
        return this.currentUser;
    }

    @Override
    public Api getSpotifyApi() {
        return this.spotifyApi;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void setSpotifyApi(Api spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Artist findById(int id) {
        String sql = "SELECT * FROM artists WHERE id = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Artist.class);
        }
    }

    @Override
    public List<String> getTopArtist() {
        String route = "https://api.spotify.com/v1/me/top/artists";
        String accessToken = getAccessToken();
        List<String> artists = new ArrayList<>();
        try {
            URL url = new URL(route);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setRequestProperty("Authorization", "Bearer " + accessToken);
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray itemsArray = jsonElement.getAsJsonObject().getAsJsonArray("items");
            for (int i = 0; i < itemsArray.size(); i ++) {
                String artistsNames = jsonElement.getAsJsonObject()
                        .getAsJsonArray("items")
                        .get(i).getAsJsonObject()
                        .get("name")
                        .getAsString();
                artists.add(artistsNames);
            }
            // Adding Test Artists
            artists.add("Katy Perry");
            artists.add("Eagles");
            artists.add("Rod Stewart");
            artists.add("Faye Carol");
            for (String artist: artists){
                System.out.println(artist);
            }
            return artists;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String createPlaylist(String name, String description) {
        String userIdroute = "https://api.spotify.com/v1/me";
        String accessToken = getAccessToken();
        String userId = new String();
        try {
            URL url = new URL(userIdroute);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setRequestProperty("Authorization", "Bearer " + accessToken);
            JsonParser jsonParser = new JsonParser();
            JsonElement json = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
            userId = json.getAsJsonObject().get("id").getAsString();
            System.out.println("user id =" + userId);

        } catch ( IOException e){
            e.printStackTrace();
            System.out.println("failed to get userId");
        }

        String route = "https://api.spotify.com/v1/users/" + userId + "/playlists";
        String playlistId = new String();
        try {
            URL url = new URL(route);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("POST");
            request.setRequestProperty("Authorization", "Bearer " + accessToken);
            request.setRequestProperty("Content-Type", "application/json");
            request.setRequestProperty("name", name);
            request.setRequestProperty("public", "true");
            request.setRequestProperty("description", description
            );
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
            playlistId = jsonElement
                    .getAsJsonObject()
                    .get("id")
                    .getAsString();
            System.out.println(playlistId);
            return playlistId;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("no playlist, dude");
        }
        return null;
    }

    @Override
    public StringBuilder formatUserId(String userId) {
        StringBuilder formattedUserId = new StringBuilder();
        String[] userIdArray = userId.split("\\.");

        for (String name : userIdArray) {
             formattedUserId
                    .append(Character.toUpperCase(name.charAt(0)))
                    .append(name.substring(1))
                    .append(" ");
        }
        return formattedUserId;
    }

    @Override
    public StringBuilder formatUserEmail(String userEmail) {
        StringBuilder buildUserEmail = new StringBuilder();
        String formattedUserEmail = userEmail.substring(0, userEmail.indexOf("@"));

        if (formattedUserEmail.contains(".")) {
            String[] splitUserEmail = formattedUserEmail.split("\\.");

            for (String name : splitUserEmail) {
                buildUserEmail
                        .append(Character.toUpperCase(name.charAt(0)))
                        .append(name.substring(1))
                        .append(" ");
            }
        } else {
            buildUserEmail
                    .append(formattedUserEmail);
        }

        return buildUserEmail;
    }

    @Override
    public Properties loadProperties() {
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getResourceAsStream("/config.properties"));
        } catch (Exception e) {

        }
        return prop;
    }

}
