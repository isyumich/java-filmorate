package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistValueException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.MPAMapper;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    final FilmValidation filmValidation = new FilmValidation();
    final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addNewFilm(Film film) {
        if (filmValidation.validateFilmFields(film)) {
            long id;
            String query = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?);";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(query, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);
            id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            film.setId(id);
        } else {
            log.info("Поля заполнены неверно");
        }
        checkLikesSet(film);
        checkGenresList(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmValidation.validateFilmFields(film)) {
            int countLines = jdbcTemplate.update("UPDATE films SET name = ?, " +
                            "description = ?, " +
                            "release_date = ?, " +
                            "duration = ?, " +
                            "mpa_id = ? WHERE id = ?;",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            if (countLines == 0) {
                throw new NotFoundException("Фильм с таким id не найден");
            }
        } else {
            log.info("Поля заполнены неверно");
        }
        checkLikesSet(film);
        checkGenresList(film);
        return film;
    }

    @Override
    public List<Film> findFilms() {
        String query = "SELECT t1.*, t2.name AS mpa_name FROM films t1 INNER JOIN MPA t2  ON t1.mpa_id = t2.id;";
        return jdbcTemplate.query(query, new FilmMapper(jdbcTemplate));
    }

    @Override
    public Film findFilm(long filmId) {
        Film film;
        try {
            film = jdbcTemplate.queryForObject("SELECT t1.*, t2.name AS mpa_name FROM films t1 INNER JOIN MPA t2  ON t1.mpa_id = t2.id WHERE t1.id = ?;", new FilmMapper(jdbcTemplate), filmId);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
    }

    @Override
    public Film addOrDeleteLikeToFilm(long filmId, long userId, String typeOperation) {
        Film film;
        User user;
        try {
            film = jdbcTemplate.queryForObject("SELECT t1.*, t2.name AS mpa_name FROM films t1 INNER JOIN MPA t2  ON t1.mpa_id = t2.id WHERE t1.id = ?", new FilmMapper(jdbcTemplate), filmId);
            user = jdbcTemplate.queryForObject("SELECT * from users WHERE id = ?;", new UserMapper(jdbcTemplate), userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь или фильм с таким id не найден");
        }
        switch (typeOperation) {
            case ("DELETE"):
                int countLinesDelete = jdbcTemplate.update("DELETE FROM film_likes_by_user WHERE film_id = ? and user_id = ?;", filmId, userId);
                if (countLinesDelete == 0) {
                    throw new NotFoundException("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
                }
                log.info("Удален лайк от пользователя");
                break;
            case ("ADD"):
                int countLinesSelect = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM film_likes_by_user where film_id = ? and user_id = ?;", Integer.class, filmId, userId);
                if (countLinesSelect > 0) {
                    throw new AlreadyExistValueException("Пользователь с id " + userId + " уже поставил лайк фильму с id " + filmId);
                }
                jdbcTemplate.update("INSERT INTO film_likes_by_user VALUES (?, ?);", filmId, userId);
                log.info("Добавлен лайк от пользователя");
                break;
            default:
                break;
        }
        return film;
    }

    @Override
    public List<Film> findMostPopularFilms(String countFilms) {
        String query = "SELECT t1.*, t3.name as mpa_name FROM films t1 " +
                "LEFT JOIN film_likes_by_user t2 ON t1.id = t2.film_id " +
                "INNER JOIN MPA t3  ON t1.mpa_id = t3.id " +
                "group by t1.id " +
                "order by count(user_id) desc " +
                "limit ?;";
        return jdbcTemplate.query(query, new FilmMapper(jdbcTemplate), countFilms);
    }

    @Override
    public List<Genre> findAllGenres() {
        String query = "SELECT * FROM genres;";
        return jdbcTemplate.query(query, new GenreMapper());
    }

    @Override
    public Genre findGenre(int genreId) {
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject("SELECT * FROM genres where id = ?;", new GenreMapper(), genreId);
            return genre;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с таким id не найден");
        }
    }

    @Override
    public List<MPA> findAllMPA() {
        String query = "SELECT * FROM MPA;";
        return jdbcTemplate.query(query, new MPAMapper());
    }

    @Override
    public MPA findMPA(int mpaId) {
        MPA mpa;
        try {
            mpa = jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE id = ?;", new MPAMapper(), mpaId);
            return mpa;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг с таким id не найден");
        }
    }

    private void checkLikesSet(Film film) {
        if (film.getUsersWhoLiked() == null) {
            film.setUsersWhoLiked(new HashSet<>());
        }
    }

    private void checkGenresList(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        } else {
            List<Genre> listGenre = film.getGenres();
            List<Genre> newList = new ArrayList<>();
            jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?;", film.getId());
            for (Genre genre : listGenre) {
                if (!newList.contains(genre)) {
                    newList.add(genre);
                    jdbcTemplate.update("INSERT INTO film_genre VALUES (?, ?);", film.getId(), genre.getId());
                }
            }
            film.setGenres(newList);
        }
    }
}
