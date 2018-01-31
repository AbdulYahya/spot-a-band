package dao;

import com.wrapper.spotify.methods.AddTrackToPlaylistRequest;
import com.wrapper.spotify.methods.ArtistSearchRequest;
import com.wrapper.spotify.methods.PlaylistCreationRequest;
import com.wrapper.spotify.methods.TopTracksRequest;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;
import models.Artist;
import models.Event;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;


public class Sql2oMerge implements Merge{
    String connectionString = "jdbc:h2:~/spot-a-band.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
    private Sql2o sql2o = new Sql2o(connectionString, "", "");

    public Sql2oMerge(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    Sql2oSpotifyDao spotifyDao = new Sql2oSpotifyDao(sql2o);
    Sql2oTicketMasterDao ticketMasterDao = new Sql2oTicketMasterDao(sql2o);

    @Override
    public void eventsPlaylist(String city, String date){
        //create a playlist of name "playing in [CITY] on [DATE]"
        String playlistID = "";
        final PlaylistCreationRequest request = spotifyDao.apiConstructor().createPlaylist("","playing in" +city +" on " + date)
                .publicAccess(true)
                .build();

        try {
            Playlist playlist = request.get();

            playlistID = playlist.getId();
        } catch (Exception e) {

        }



        List<Event> events = ticketMasterDao.getShowsForCityOnDay(city, date);

        try {
            final Playlist playlist = request.get();

            System.out.println("You just created this playlist!");
            System.out.println("Its title is " + playlist.getName());
        } catch (Exception e) {
            System.out.println("Something went wrong!" + e.getMessage());
        }
        List<String> artistList = new ArrayList<>();
        for (Event event:events) {
            String artist = event.getName();
            ArtistSearchRequest artistSearchRequest = spotifyDao.apiConstructor().searchArtists(artist).market("US").limit(1).build();

            try {
                final Page<com.wrapper.spotify.models.Artist> artistSearchResult = artistSearchRequest.get();
                final List<com.wrapper.spotify.models.Artist> artists = artistSearchResult.getItems();

                for (com.wrapper.spotify.models.Artist eachArtist : artists) {
                   artistList.add(eachArtist.getId());
                }

            } catch (Exception e) {

            }
        }

        for(String artist:artistList){
            int i = 0;
            try{
                List<Track> topTracks = TopTracksRequest.builder().id(artist).countryCode("US").build().get();
                List<String> topTrackIDs = new ArrayList<>();
                for(Track track: topTracks){
                    topTrackIDs.add(track.getId());
                }

                final AddTrackToPlaylistRequest playlistAdd = spotifyDao.apiConstructor().addTracksToPlaylist("", playlistID, topTrackIDs).build();

            }catch (Exception e) {

            }
        }
    }
}
