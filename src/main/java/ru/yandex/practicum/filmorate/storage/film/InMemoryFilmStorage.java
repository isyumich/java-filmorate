package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    final FilmValidation filmValidation = new FilmValidation();

    long nextId = 1;
    final Map<Long, Film> films = new HashMap<>();
    final List<Genre> genres = List.of(
            Genre.builder().id(1).name("Комедия").build(),
            Genre.builder().id(2).name("Триллер").build(),
            Genre.builder().id(3).name("Боевик").build(),
            Genre.builder().id(4).name("Драма").build(),
            Genre.builder().id(5).name("Мелодрама").build(),
            Genre.builder().id(6).name("Ужасы").build());

    final List<MPA> MPAList = List.of(
            MPA.builder().id(1).name("G").build(),
            MPA.builder().id(2).name("PG").build(),
            MPA.builder().id(3).name("PG-13").build(),
            MPA.builder().id(4).name("R").build(),
            MPA.builder().id(5).name("NC-17").build());

    @Override
    public Film addNewFilm(Film film) {
        if (filmValidation.validateFilmFields(film)) {
            film.setId(nextId++);
            setEmptyLikesSet(film);
            setEmptyGenresList(film);
            films.put(film.getId(), film);
            log.info(String.format("%s %d", "Добавлен новый фильм с id", film.getId()));
        } else {
            log.info("Поля заполнены неверно");
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmValidation.validateFilmFields(film)) {
            if (!films.containsKey(film.getId())) {
                log.debug(String.format("%s %d %s", "Фильм с id", film.getId(), "не найден"));
                throw new NotFoundException(String.format("%s %d %s", "Фильм с id", film.getId(), "не найден"));
            }
            films.put(film.getId(), film);
            log.info(String.format("%s %d %s", "Фильм с id", film.getId(), "успешно обновлён"));
        } else {
            log.info("Поля заполнены неверно");
        }
        setEmptyLikesSet(film);
        setEmptyGenresList(film);
        return film;
    }

    @Override
    public List<Film> findFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilm(long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new NotFoundException(String.format("%s %d %s", "Фильм с id", filmId, "не найден"));
        }
    }

    @Override
    public Film addOrDeleteLikeToFilm(long filmId, long userId, String typeOperation) {
        if (films.containsKey(filmId)) {
            Film film = films.get(filmId);
            Set<Long> newSetWithLikes = film.getUsersWhoLiked();
            switch (typeOperation) {
                case ("DELETE"):
                    if (newSetWithLikes.contains(userId)) {
                        newSetWithLikes.remove(userId);
                    } else {
                        throw new NotFoundException(String.format("%s %d %s %d", "Пользователь с id", userId, "не ставил лайк фильму с id", filmId));
                    }
                    log.info(String.format("%s %d %s %d", "У фильма с id", filmId, "удалён лайк от пользователя", userId));
                    break;
                case ("ADD"):
                    newSetWithLikes.add(userId);
                    log.info(String.format("%s %d %s %d", "У фильма с id", filmId, "добавлен лайк от пользователя", userId));
                    break;
                default:
                    break;
            }
            film.setUsersWhoLiked(newSetWithLikes);
            return film;
        } else {
            throw new NotFoundException(String.format("%s %d %s", "Фильм с id", userId, "не найден"));
        }

    }

    @Override
    public List<Film> findMostPopularFilms(String countFilms) {
        return findFilms().stream()
                .sorted((o1, o2) -> (int) (o2.getCountLikes() - o1.getCountLikes()))
                .limit(Long.parseLong(countFilms))
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> findAllGenres() {
        return genres;
    }

    @Override
    public Genre findGenre(int genreId) {
        return genres.get(genreId);
    }

    @Override
    public List<MPA> findAllMPA() {
        return MPAList;
    }

    @Override
    public MPA findMPA(int mpaId) {
        return MPAList.get(mpaId);
    }

    private void setEmptyLikesSet(Film film) {
        if (film.getUsersWhoLiked() == null) {
            film.setUsersWhoLiked(new HashSet<>());
        }
    }

    private void setEmptyGenresList(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
    }

    // Start of %%%%%%%%% %%%%%%%%% %%%%%%%%% Director's funcs %%%%%%%%% %%%%%%%%% %%%%%%%%%
    @Override
    public List<Film> getDirectorSortedFilms(int id, String param) {
        return null;
    }

    @Override
    public List<Director> getAllDirectors() {
        return null;
    }

    @Override
    public Director getDirectorById(int id) {
        return null;
    }

    @Override
    public Director createDirector(Director director) {
        return null;
    }

    @Override
    public Director updateDirector(Director director) {
        return null;
    }

    @Override
    public Director deleteDirector(int id) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return null;
    }
    // End of %%%%%%%%% %%%%%%%%% %%%%%%%%% Director's funcs %%%%%%%%% %%%%%%%%% %%%%%%%%%

}
