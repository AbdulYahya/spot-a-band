package dao;


import models.Artist;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

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
}
