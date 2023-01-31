package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Director;
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

    // Start of %%%%%%%%% %%%%%%%%% %%%%%%%%% Director's services %%%%%%%%% %%%%%%%%% %%%%%%%%%

    public List<Film> getDirectorSortedFilms(int id, String param){
        log.info("Запрос на отсортированный список фильмов");
        return filmStorage.getDirectorSortedFilms(id, param);
    }

    public List<Director> getAllDirectors(){
        log.info("Запрос на список всех режиссеров");
        return filmStorage.getAllDirectors();
    }

    public Director getDirectorById(int id){
        log.info("Запрос на режиссера по id");
        return filmStorage.getDirectorById(id);
    }

    public Director createDirector(Director director){
        log.info("Запрос на добавление нового режиссера");
        return filmStorage.createDirector(director);
    }

    public Director updateDirector(Director director){
        log.info("Запрос на изменение режиссера");
        return filmStorage.updateDirector(director);
    }

    public Director deleteDirector(int id){
        log.info("Запрос на удаление режиссера по id");
        return filmStorage.deleteDirector(id);
    }
    // End of %%%%%%%%% %%%%%%%%% %%%%%%%%% Director's services %%%%%%%%% %%%%%%%%% %%%%%%%%%

    public List<Film> searchFilmByParameters(String fieldValue, String parameters) {
        log.info("Поиск фильмов по параметру " + parameters);
        return filmStorage.searchFilmByParameters(fieldValue, parameters);
    }

}
