package models;

public class User {

    int id;
    private String spotifyClientId;
    private String spotifyClientSecret;
    private String spotifyUserId;
    private String spotifyToken;

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

    public static class Builder {
        String spotifyClientId;
        String spotifyClientSecret;
        String spotifyUserId;
        String spotifyToken;

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

        public User Build() {
            return new User(this);
        }
    }

    private User(Builder builder) {
        this.spotifyClientId = builder.spotifyClientId;
        this.spotifyClientSecret = builder.spotifyClientSecret;
//        this.spotifyUserId = builder.spotifyUserId;
//        this.spotifyToken = builder.spotifyToken;
    }
}
// user =  new User.Builder
//  .spotifyToken(stuff)
//  .spotify

//user.getToken()