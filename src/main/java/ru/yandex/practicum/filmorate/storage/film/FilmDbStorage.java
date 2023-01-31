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
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.MPAMapper;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.service.Constants.*;

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
            log.info(String.format("%s %d", "Добавлен новый фильм с id", film.getId()));
        } else {
            log.info("Поля заполнены неверно");
        }
        checkLikesSet(film);
        checkGenresList(film);
        checkDirectorList(film);
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
                log.debug(String.format("%s %d %s", "Фильм с id", film.getId(), "не найден"));
                throw new NotFoundException(String.format("%s %d %s", "Фильм с id", film.getId(), "не найден"));
            }
            log.info(String.format("%s %d %s", "Фильм с id", film.getId(), "успешно обновлён"));
        } else {
            log.info("Поля заполнены неверно");
        }
        checkLikesSet(film);
        checkGenresList(film);
        checkDirectorList(film);
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
            throw new NotFoundException(String.format("%s %d %s", "Фильм с id", filmId, "не найден"));
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
            throw new NotFoundException(String.format("%s %d %s %d %s", "Пользователь с id", userId, "или фильм с id", filmId, "не найден"));
        }
        switch (typeOperation) {
            case ("DELETE"):
                int countLinesDelete = jdbcTemplate.update("DELETE FROM film_likes_by_user WHERE film_id = ? and user_id = ?;", filmId, userId);
                if (countLinesDelete == 0) {
                    throw new NotFoundException(String.format("%s %d %s %d", "Пользователь с id", userId, "не ставил лайк фильму с id", filmId));
                }
                log.info(String.format("%s %d %s %d", "У фильма с id", filmId, "удалён лайк от пользователя", userId));
                addToFeedDeleteLike(userId, filmId);
                break;
            case ("ADD"):
                int countLinesSelect = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM film_likes_by_user where film_id = ? and user_id = ?;", Integer.class, filmId, userId);
                addToFeedAddLike(userId, filmId);
                if (countLinesSelect == 0) {
                    jdbcTemplate.update("INSERT INTO film_likes_by_user VALUES (?, ?);", filmId, userId);
                    log.info(String.format("%s %d %s %d", "У фильма с id", filmId, "добавлен лайк от пользователя", userId));
                    break;
               }
            default:
                break;
        }
        return film;
    }

    private void addToFeedDeleteLike(long userId, long filmId) {
        String query = "INSERT INTO events_history (user_id, event_type_id, operations_type_id, entity_id, date_time) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(query, userId, EVENT_TYPE_LIKE, OPERATION_TYPE_DELETE, filmId, Date.from(Instant.now()));
    }

    private void addToFeedAddLike(long userId, long filmId) {
        String query = "INSERT INTO events_history (user_id, event_type_id, operations_type_id, entity_id, date_time) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(query, userId, EVENT_TYPE_LIKE, OPERATION_TYPE_ADD,filmId, Date.from(Instant.now()));
    }

    @Override
    public List<Film> findMostPopularFilms(String countFilms) {
        String query = "SELECT t1.*, t3.name as mpa_name FROM films t1 " +
                "LEFT JOIN film_likes_by_user t2 ON t1.id = t2.film_id " +
                "INNER JOIN MPA t3 ON t1.mpa_id = t3.id " +
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
            throw new NotFoundException(String.format("%s %d %s", "Фильм с id", genreId, "не найден"));
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
            throw new NotFoundException(String.format("%s %d %s", "Фильм с id", mpaId, "не найден"));
        }
    }

    @Override
    public List<Film> getDirectorSortedFilms(int id, String param) {
        directorExistCheckUp(id);
        if (Objects.equals(param, "year")){
            String sql = "SELECT t1.*, t2.name AS mpa_name FROM films t1 INNER JOIN MPA t2 ON t1.mpa_id = t2.id WHERE t1.id IN (SELECT film_id FROM directors_films WHERE director_id = ?) ORDER BY EXTRACT(YEAR FROM CAST(release_date AS date));";
            return jdbcTemplate.query(sql, new FilmMapper(jdbcTemplate), id);
        } else if (Objects.equals(param, "likes")) {
            String sql = "SELECT t1.*, t3.name AS mpa_name FROM films t1 LEFT JOIN film_likes_by_user t2 ON t1.id = t2.film_id INNER JOIN MPA t3 ON t1.mpa_id = t3.id WHERE t1.id IN (SELECT film_id FROM directors_films WHERE director_id = ?) GROUP BY t1.id ORDER BY COUNT(user_id) DESC";
            return jdbcTemplate.query(sql, new FilmMapper(jdbcTemplate), id);
        } else {
            throw new ValidationException("Неверный формат переданного параметра");
        }
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors;", new DirectorMapper());
    }

    @Override
    public Director getDirectorById(int id) {
        Director director;
        try {
            director = jdbcTemplate.queryForObject("SELECT * FROM directors WHERE id = ?", new DirectorMapper(),  id);
            return director;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("%s %d %s", "Director с id", id, "не найден"));
        }
    }

    @Override
    public Director createDirector(Director director) {
        if (director.getName().isEmpty() || director.getName() == null || Objects.equals(director.getName(), " ")){
            throw new ValidationException("Bad name format");
        }
        String sql = "INSERT INTO directors (name) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        director.setId(id);
        log.info(String.format("%s %d", "Добавлен новый режиссер с id", director.getId()));
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        int countLines = jdbcTemplate.update("UPDATE directors SET name = ? WHERE id = ?", director.getName(), director.getId());
        if (countLines == 0) {
            log.debug(String.format("%s %d %s", "Режиссер с id", director.getId(), "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Режиссер с id", director.getId(), "не найден"));
        }
        log.info(String.format("%s %d %s", "Режиссер с id", director.getId(), "успешно обновлён"));
        return director;
    }

    @Override
    public Director deleteDirector(int id) {
        Director director = getDirectorById(id);
        jdbcTemplate.update("DELETE FROM directors_films WHERE director_id = ?", id);
        jdbcTemplate.update("DELETE FROM directors WHERE id = ?", id);
        log.info(String.format("%s %d %s", "Режиссер с id ", id, " успешно удален"));
        return director;
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

    private void checkDirectorList(Film film) {
        if (film.getDirectors() == null) {
            film.setDirectors(new ArrayList<>());
            jdbcTemplate.update("DELETE FROM directors_films WHERE film_id = ?;", film.getId());
        } else {
            List<Director> listDirector = film.getDirectors();
            List<Director> newList = new ArrayList<>();
            jdbcTemplate.update("DELETE FROM directors_films WHERE film_id = ?;", film.getId());
            for (Director director : listDirector) {
                if (!newList.contains(director)) {
                    newList.add(director);
                    jdbcTemplate.update("INSERT INTO directors_films VALUES (?, ?);", film.getId(), director.getId());
                }
            }
            film.setDirectors(newList);
        }
    }

    void directorExistCheckUp(int id){
        if(!jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE id = ?", id).next()) {
            throw new NotFoundException("Director not found");
        }
    }

    @Override
    public void deleteFilm(long id) {
        filmExistCheckUp(id);
//        jdbcTemplate.update("delete   from DIRECTORS_FILMS where FILM_ID = ? ", id);
//        jdbcTemplate.update("delete   from FILM_GENRE where FILM_ID = ? ", id);
//        jdbcTemplate.update("delete   from FILM_LIKES_BY_USER where FILM_ID = ? ", id);
//        jdbcTemplate.update("delete   from FILM_REVIEWS where FILM_ID = ? ", id);
        jdbcTemplate.update("delete   from FILMS where ID = ? ", id);
        log.info("Удалён фильм с id : {} ", id);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String query = ("SELECT t1.*, t3.name as mpa_name FROM films t1 " +
                "LEFT JOIN film_likes_by_user t2 ON t1.id = t2.film_id " +
                "INNER JOIN MPA t3 ON t1.mpa_id = t3.id" +
                " WHERE t1.ID IN " +
                "(SELECT t4.FILM_ID  FROM FILM_LIKES_BY_USER AS t4 " +
                "LEFT JOIN film_likes_by_user t2 ON t4.FILM_ID =t2.FILM_ID" +
                " WHERE (t4.USER_id=? AND t2.user_id = ?))" +
                "group by t1.id order by count(user_id) desc");

        return jdbcTemplate.query(query, new FilmMapper(jdbcTemplate), userId, friendId);
    }

    @Override
    public List<Film> searchFilmByParameters(String fieldValue, String parameters) {
        FilmMapper mapper = new FilmMapper(jdbcTemplate);
        String causeSearch = getCauseForSearch(fieldValue, parameters);
        String sql = "SELECT f.*, mpa.name as mpa_name\n" +
                "FROM films f\n" +
                "LEFT JOIN MPA mpa ON f.mpa_id = mpa.id\n" +
                "LEFT JOIN film_likes_by_user flu ON f.id = flu.film_id\n" +
                "LEFT join directors_films df ON f.id = df.film_id\n" +
                "LEFT join directors d ON df.director_id = d.id\n" +
                "WHERE " + causeSearch + "\n" +
                "GROUP BY f.id\n" +
                "ORDER BY COUNT(flu.user_id) DESC";
        System.out.println(jdbcTemplate.query(sql, (rs, rowNum) -> mapper.mapRow(rs, rowNum)));
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapper.mapRow(rs, rowNum));
    }

    private String getCauseForSearch (String fieldValue, String parameters) {
        switch (parameters) {
            case ("director"):
                System.out.println("Поиск по директору");
                System.out.println("LOWER(f.name) LIKE LOWER('%" + fieldValue + "%')");
                return "LOWER(d.name) LIKE LOWER('%" + fieldValue + "%')";
            case ("title"):
                return "LOWER(f.name) LIKE LOWER('%" + fieldValue + "%')";
            case ("director,title"):
            case ("title,director"):
                return "LOWER(f.name) LIKE LOWER('%" + fieldValue + "%') OR " + "LOWER(d.name) LIKE LOWER('%" + fieldValue + "%')";
            default:
                return "true";
        }
    }

    void filmExistCheckUp (long id){
        if(!jdbcTemplate.queryForRowSet("SELECT * FROM FILMS WHERE id = ?", id).next()) {
            throw new NotFoundException("Film not found");
        }
    }
}
