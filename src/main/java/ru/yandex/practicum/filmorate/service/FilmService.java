package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmService {
    final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addNewFilm(Film film) {
        log.info("Запрос на добавление нового фильма");
        return filmStorage.addNewFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Запрос на обновление данных фильма");
        return filmStorage.updateFilm(film);
    }

    public Film addOrDeleteLikeToFilm(long filmId, long userId, String typeOperation) {
        log.info("Запрос добавление/удаление лайка к фильму " + filmId);
        log.info("Тип операции: " + typeOperation);
        return filmStorage.addOrDeleteLikeToFilm(filmId, userId, typeOperation);
    }

    public Collection<Film> findFilms() {
        log.info("Запрос на получение списка фильмов");
        return filmStorage.findFilms();
    }

    public Film findFilm(long filmId) {
        log.info("Запрос на получение списка фильмов на получения фильма по id" + filmId);
        return filmStorage.findFilm(filmId);
    }

    public List<Film> findMostPopularFilms(String countFilms) {
        log.info("Запрос на получение списка из " + countFilms + " самых популярных фильмов");
        return filmStorage.findMostPopularFilms(countFilms);
    }

    public List<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }

    public Genre findGenre(int genreId) {
        return filmStorage.findGenre(genreId);
    }

    public List<MPA> findAllMPA() {
        return filmStorage.findAllMPA();
    }

    public MPA findMPA(int mpaId) {
        return filmStorage.findMPA(mpaId);
    }
}
