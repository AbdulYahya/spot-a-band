package dao;

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

import static spark.Spark.connect;
import static spark.Spark.get;

public class Sql2oSpotifyDao implements SpotifyDao {
    private final Sql2o sql2o;
    private String code;

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
    public String oAuth(String code) {
        this.code = code;

        /*
        *       Containerize entire Spotify oAuth flow in here?
        * */

        return code;
    }

    @Override
    public String getCode() {
        return code;
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
        String accessToken = "";//getCode();

       System.out.println(accessToken);
        //"AQCm7Ky_49WrUdjfvLO_cgri8LmtiQq4Eef9oDNIevDLuXAr8JEum3ZUytDGgZYnSFpo-mpGNHYWOnDddmI4tZ33MVcvzwk5qMvklQpw9-IUKlVu1Z_kd7Ys6tvjr6eN98sI3509BIuBJgnD9EQxzcOFQhZ3AZ0gaWxf9HrpXZ143O84zWnzgcgqKPis30ij3vKAqZunkQvem-hgYdfO-TfEIj6VxmM9hhlA2RVAThLuc5K-WJw";

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


}
