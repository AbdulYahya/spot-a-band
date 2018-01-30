package dao;

import com.wrapper.spotify.methods.*;
import com.wrapper.spotify.models.*;
import com.google.gson.*;
import models.Artist;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Sql2oSpotifyDao implements SpotifyDao {

    private final Sql2o sql2o;
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
