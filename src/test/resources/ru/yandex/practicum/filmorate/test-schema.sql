DROP TABLE IF EXISTS friends_list;
DROP TABLE IF EXISTS film_likes_by_user;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS MPA;
DROP TABLE IF EXISTS genres;

CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS MPA
(
    id                   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name                 VARCHAR(20) NOT NULL,
    min_age_for_watching INTEGER     NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(50)  NOT NULL,
    login    VARCHAR(100) NOT NULL,
    name     VARCHAR(100),
    birthday DATE
        CONSTRAINT check_user_birthday CHECK (birthday < CURRENT_DATE())
);

CREATE TABLE IF NOT EXISTS films
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    description  VARCHAR(200),
    release_date DATE         NOT NULL
        CONSTRAINT check_film_release_date CHECK (release_date > '1895-12-28'),
    duration     INTEGER      NOT NULL
        CONSTRAINT check_film_duration CHECK (duration > 0),
    mpa_id       INTEGER REFERENCES MPA (id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER REFERENCES films (id),
    genre_id INTEGER REFERENCES genres (id)
);

CREATE TABLE IF NOT EXISTS film_likes_by_user
(
    film_id INTEGER REFERENCES films (id),
    user_id INTEGER REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS friends_list
(
    user_id   INTEGER REFERENCES users (id),
    friend_id INTEGER REFERENCES users (id)
);