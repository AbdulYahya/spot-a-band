SET MODE PostgreSQL;

CREATE TABLE IF NOT EXISTS artists (
 id int PRIMARY KEY auto_increment,
 artistName VARCHAR,
 spotifyId VARCHAR
);