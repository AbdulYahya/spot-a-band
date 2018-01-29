package models;

public class Artist {
    private String artistName;
    private String spotifyId;
    private int id;

    public Artist(String artistName, String spotifyId) {
        this.artistName = artistName;
        this.spotifyId = spotifyId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (id != artist.id) return false;
        if (!artistName.equals(artist.artistName)) return false;
        return spotifyId.equals(artist.spotifyId);
    }

    @Override
    public int hashCode() {
        int result = artistName.hashCode();
        result = 31 * result + spotifyId.hashCode();
        result = 31 * result + id;
        return result;
    }
}
