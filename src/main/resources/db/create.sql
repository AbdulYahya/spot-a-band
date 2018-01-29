SET MODE PostgreSQL;


CREATE TABLE IF NOT EXISTS events (
    id int PRIMARY KEY auto_increment,
    name VARCHAR,
    ticketMasterId VARCHAR,
    url VARCHAR,
    localDate VARCHAR,
    localTime VARCHAR,
    priceRange VARCHAR
);

CREATE TABLE IF NOT EXISTS artists (
 id int PRIMARY KEY auto_increment,
 artistName VARCHAR,
 spotifyId VARCHAR
);

