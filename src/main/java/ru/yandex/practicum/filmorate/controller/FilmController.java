package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.TypeOperations;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @PostMapping
    Film addNewFilm(@RequestBody Film film) {
        log.info("Получен запрос POST на добавление фильма");
        return filmService.filmStorage.addNewFilm(film);
    }


    @DeleteMapping("/{id}/like/{userId}")
    Film deleteLikeFromFilm(@PathVariable("id") long filmId, @PathVariable("userId") Long userId) {
        log.info("Получен запрос DELETE на удаление лайка у фильма " + filmId);
        return filmService.addOrDeleteLikeToFilm(filmId, userId, TypeOperations.DELETE.toString());
    }


    @PutMapping
    Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос PUT на обновление фильма");
        return filmService.filmStorage.updateFilm(film);
    }
    @PutMapping("/{id}/like/{userId}")
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
