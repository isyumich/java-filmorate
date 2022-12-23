package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.TypeOperations;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmController {
    final FilmService filmService;
    final String pathForFilmLike = "/{id}/like/{userId}";

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @PostMapping
    Film addNewFilm(@RequestBody Film film) {
        log.info("Получен запрос POST на добавление фильма");
        return filmService.filmStorage.addNewFilm(film);
    }


    @DeleteMapping(pathForFilmLike)
    Film deleteLikeFromFilm(@PathVariable("id") long filmId, @PathVariable("userId") Long userId) {
        log.info("Получен запрос DELETE на удаление лайка у фильма " + filmId);
        return filmService.addOrDeleteLikeToFilm(filmId, userId, TypeOperations.DELETE.toString());
    }


    @PutMapping
    Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос PUT на обновление фильма");
        return filmService.filmStorage.updateFilm(film);
    }

    @PutMapping(pathForFilmLike)
    Film addLikeToFilm(@PathVariable("id") long filmId, @PathVariable("userId") Long userId) {
        log.info("Получен запрос PUT добавление лайка фильму " + filmId);
        return filmService.addOrDeleteLikeToFilm(filmId, userId, TypeOperations.ADD.toString());
    }


    @GetMapping
    Collection<Film> findFilms() {
        log.info("Получен запрос GET на получение списка фильмов");
        return filmService.filmStorage.findFilms();
    }
    @GetMapping("/{id}")
    Film findFilm(@PathVariable("id") long filmId) {
        return filmService.filmStorage.findFilm(filmId);
    }
    @GetMapping("/popular")
    List<Film> findMostPopularFilms(@RequestParam(defaultValue = "10", name = "count") String countFilms) {
        log.info("Получен запрос GET на получение списка из " + countFilms + " самых популярных фильмов");
        return filmService.findMostPopularFilms(countFilms);
    }
}
