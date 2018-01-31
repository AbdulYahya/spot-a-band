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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static spark.Spark.connect;
import static spark.Spark.get;

public class Sql2oSpotifyDao implements SpotifyDao {
    private final Sql2o sql2o;
    private String code;
    private String accessToken;

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
        Api spotifyApi = apiConstructor();
        /*
        *       Containerize entire Spotify oAuth flow in here?
        * */
        final SettableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = spotifyApi.authorizationCodeGrant(code).build().getAsync();

            /* Add callbacks to handle success and failure */
        Futures.addCallback(authorizationCodeCredentialsFuture, new FutureCallback<AuthorizationCodeCredentials>() {
            @Override
            public void onSuccess(AuthorizationCodeCredentials authorizationCodeCredentials) {

                /* Set the access token and refresh token so that they are used whenever needed */
                spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
                spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

                setAccessToken(authorizationCodeCredentials.getAccessToken());
            }

            @Override
            public void onFailure(Throwable throwable) {
                /* Let's say that the client id is invalid, or the code has been used more than once,
                 * the request will fail. Why it fails is written in the throwable's message. */

            }
        });

        return spotifyApi;
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
    public Artist findById(int id) {
        String sql = "SELECT * FROM artists WHERE id = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Artist.class);
        }
    }

    @Override
    public String getTopArtist() {
        String route = "https://api.spotify.com/v1/me/top/artists";
        String accessToken = getAccessToken();

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

            return response.get("name").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String username/login info
        //String[] artists
        /*get(path stuff){
            top artists
        }*/

        return null;
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
