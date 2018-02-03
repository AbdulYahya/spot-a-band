package dao;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.*;
import com.wrapper.spotify.models.*;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.User;
import models.*;
import org.apache.commons.lang.RandomStringUtils;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Sql2oMerge implements Merge{
    String connectionString = "jdbc:h2:~/spot-a-band.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
    private Sql2o sql2o = new Sql2o(connectionString, "", "");

    public Sql2oMerge(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    Sql2oSpotifyDao spotifyDao = new Sql2oSpotifyDao(sql2o);
    Sql2oTicketMasterDao ticketMasterDao = new Sql2oTicketMasterDao(sql2o);

//    CurrentUserRequest currentUserRequest = spotifyDao.oAuth(spotifyDao.getCode()).getMe().build();
//    User user = spotifyDao.getCurrentUser(currentUserRequest);
//    User user = spotifyDao.getCurrentUser();

    @Override
    public void eventsPlaylist(String city, String date, String userID){
        String playlistID = "";
//        System.out.println(user.getId());
//        System.out.println(playlistID);
//        spotifyDao.
//        final List<String> scopes = Arrays.asList("user-top-read, user-read-private, user-read-email, playlist-modify-public");
//        System.out.println(spotifyDao.apiConstructor().createAuthorizeURL(scopes, RandomStringUtils.random(64, true, true)));
        PlaylistCreationRequest request = spotifyDao.getSpotifyApi().createPlaylist(userID,"test2")
                .publicAccess(true)
                .build();
        try {
            Playlist playlist = request.get();
            playlistID = playlist.getId();
        } catch (Exception e) {
            System.out.println("no playlist was made");
            System.out.println(spotifyDao.oAuth(spotifyDao.getAccessToken()));
        }

        List<Event> events = ticketMasterDao.getShowsForCityOnDay(city, date);

        try {
            final Playlist playlist = request.get(); //token is likely missing here.

            System.out.println("You just created this playlist!");
            System.out.println("Its title is " + playlist.getName());
        } catch (Exception e) {
            System.out.println("no playlist was made");
        }
        List<String> artistList = new ArrayList<>();
        for (Event event:events) {
            String artist = event.getName();
            ArtistSearchRequest artistSearchRequest = spotifyDao.getSpotifyApi().searchArtists(artist).market("US").limit(1).build();

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
            try{
                List<Track> topTracks = TopTracksRequest.builder().id(artist).countryCode("US").build().get();
                List<String> topTrackIDs = new ArrayList<>();
                for(Track track: topTracks){
                    topTrackIDs.add(track.getId());
                }
                final AddTrackToPlaylistRequest playlistAdd = spotifyDao.getSpotifyApi().addTracksToPlaylist(userID, playlistID, topTrackIDs).build();

            }catch (Exception e) {
            }
        }
    }


    @Override
    public List<String> getSongsToAdd(String city, String date) {
        List<String> spotifySongIds = new ArrayList<>();
        List<Event> events = ticketMasterDao.getShowsForCityOnDay(city, date);
        List<String> artistIds = new ArrayList<>();
        for (Event event:events) {
            System.out.println(event.getName());
            final ArtistSearchRequest request = spotifyDao.getSpotifyApi().searchArtists(event.getName()).market("US").limit(10).build();

            try {
                final Page<Artist> artistSearchResult = request.get();
                final List<Artist> artists = artistSearchResult.getItems();

                System.out.println("I've found " + artistSearchResult.getTotal() + " artists!");
                System.out.println(artists.get(0).getName());
                artistIds.add(artists.get(0).getId());
                System.out.println(artists.get(0).getId());

            } catch (Exception e) {
                System.out.println("Something went wrong!" + e.getMessage());
            }
            for(String id : artistIds){
                try{
                    List<Track> topTracks = TopTracksRequest.builder().id(id).countryCode("US").build().get();
                    for(Track track: topTracks){
                        spotifySongIds.add(track.getId());
                    }
                }catch (Exception e) {
                    System.out.println("no tracks");
                }
            }
        }
        return spotifySongIds;
    }



    @Override
    public List<Event> whenInTown(){
        List<String> artists = spotifyDao.getTopArtist();
        List<Event> events = ticketMasterDao.getNextPortlandShow(artists);
        return events;
    }
}
