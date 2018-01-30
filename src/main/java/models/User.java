package models;

public class User {

    int id;
    private String spotifyClientId;
    private String spotifyClientSecret;
    private String spotifyUserId;

    public String getSpotifyClientId(){
        return this.spotifyClientId;
    }

    public String getSpotifyClientSecret(){
        return this.spotifyClientSecret;
    }

    public String getSpotifyUserId(){
        return this.spotifyUserId;
    }

    public static class Builder {
        private String spotifyClientId;
        String spotifyClientSecret;
        String spotifyUserId;

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

        public User Build() {
            return new User(this);
        }
    }

    private User(Builder builder){
        this.spotifyClientId = builder.spotifyClientId;
        this.spotifyClientSecret = builder.spotifyClientSecret;
        this.spotifyUserId = builder.spotifyUserId;
    }
}
