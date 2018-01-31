package models;

import java.util.List;

public class User {

    int id;
    private String spotifyClientId;
    private String spotifyClientSecret;
    private String spotifyUserId;
    private String spotifyToken;
    private List<String> spotifyScopes;

    public String getSpotifyClientId(){
        return this.spotifyClientId;
    }

    public String getSpotifyClientSecret(){
        return this.spotifyClientSecret;
    }

    public String getSpotifyUserId(){
        return this.spotifyUserId;
    }

    public String getSpotifyToken(){
        return this.spotifyToken;
    }

    public List<String> getSpotifyScopes(){
        return this.spotifyScopes;
    }

    public static class Builder {
        String spotifyClientId;
        String spotifyClientSecret;
        String spotifyUserId;
        String spotifyToken;
        List<String> spotifyScopes;

        public Builder spotifyClientId(String clientId){
            this.spotifyClientId = clientId;
            return this;
        }

        public Builder spotifyClientSecret(String secret){
            this.spotifyClientSecret = secret;
            return this;
        }

        public Builder spotifyUserId(String user){
            this.spotifyUserId = user;
            return this;
        }

        public Builder spotifyToken(String token){
            this.spotifyToken = token;
            return this;
        }

        public Builder spotifyScopes(List<String> scopes){
            this.spotifyScopes = scopes;
            return this;
        }

        public User Build() {
            return new User(this);
        }
    }

    private User(Builder builder) {
        this.spotifyClientId = builder.spotifyClientId;
        this.spotifyClientSecret = builder.spotifyClientSecret;

        this.spotifyUserId = builder.spotifyUserId;
        this.spotifyToken = builder.spotifyToken;
        this.spotifyScopes = builder.spotifyScopes;
    }
}
// user =  new User.Builder
//  .spotifyToken(stuff)
//  .spotify

//user.getToken()