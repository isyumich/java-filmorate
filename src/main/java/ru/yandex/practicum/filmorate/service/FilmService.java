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
        log.info(String.format("%s %d", "Запрос на добавление/удаление лайка к фильму", filmId));
        log.info(String.format("%s %s", "Тип операции:", typeOperation));
        return filmStorage.addOrDeleteLikeToFilm(filmId, userId, typeOperation);
    }

    public Collection<Film> findFilms() {
        log.info("Запрос на получение списка фильмов");
        return filmStorage.findFilms();
    }

    public Film findFilm(long filmId) {
        log.info(String.format("%s %d", "Запрос на получение фильма по id", filmId));
        return filmStorage.findFilm(filmId);
    }

    public List<Film> findMostPopularFilms(String countFilms) {
        log.info(String.format("%s %s %s", "Запрос на получение списка из", countFilms, "самых популярных фильмов"));
        return filmStorage.findMostPopularFilms(countFilms);
    }

    public List<Genre> findAllGenres() {
        log.info("Запрос на получение списка жанров");
        return filmStorage.findAllGenres();
    }

    public Genre findGenre(int genreId) {
        log.info(String.format("%s %d", "Запрос на получение жанра по id", genreId));
        return filmStorage.findGenre(genreId);
    }

    public List<MPA> findAllMPA() {
        log.info("Запрос на получение списка рейтингов");
        return filmStorage.findAllMPA();
    }

    public MPA findMPA(int mpaId) {
        log.info(String.format("%s %d", "Запрос на получение рейтинга по id", mpaId));
        return filmStorage.findMPA(mpaId);
    }
}
