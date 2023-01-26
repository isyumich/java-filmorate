package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilmMapper implements RowMapper<Film> {
    final JdbcTemplate jdbcTemplate;

    public FilmMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(MPA.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa_name")).build())
                .build();
        setLikesSet(film);
        setGenresList(film);
        setDirectorList(film);
        return film;
    }

    private void setLikesSet(Film film) {
        Set<Long> usersWhoLiked = new HashSet<>((jdbcTemplate.queryForList("SELECT id FROM users WHERE id IN (SELECT user_id FROM film_likes_by_user WHERE film_id = ?);", Long.class, film.getId())));
        film.setUsersWhoLiked(usersWhoLiked);
    }

    private void setGenresList(Film film) {
        List<Genre> genreList = new ArrayList<>(jdbcTemplate.query("SELECT * FROM genres WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?);", new GenreMapper(), film.getId()));
        film.setGenres(genreList);
    }

    private void setDirectorList(Film film) {
        List<Director> genreList = new ArrayList<>(jdbcTemplate.query("SELECT * FROM directors WHERE id IN (SELECT director_id FROM directors_films WHERE film_id = ?);", new DirectorMapper(), film.getId()));
        film.setDirectors(genreList);
    }

}
