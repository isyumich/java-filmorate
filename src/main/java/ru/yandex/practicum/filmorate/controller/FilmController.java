package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.TypeOperations;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmController {
    final FilmService filmService;
    final String pathForFilmLike = "/{id}/like/{userId}";
    final String pathForFilms = "/films";
    final String pathForGenres = "/genres";
    final String pathForMPA = "/mpa";
    final String pathForDirector = "/directors";

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping(pathForFilms)
    Film addNewFilm(@RequestBody Film film) {
        return filmService.addNewFilm(film);
    }

    @DeleteMapping(pathForFilms + pathForFilmLike)
    Film deleteLikeFromFilm(@PathVariable("id") long filmId, @PathVariable("userId") Long userId) {
        return filmService.addOrDeleteLikeToFilm(filmId, userId, TypeOperations.DELETE.toString());
    }


    @PutMapping(pathForFilms)
    Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping(pathForFilms + pathForFilmLike)
    Film addLikeToFilm(@PathVariable("id") long filmId, @PathVariable("userId") Long userId) {
        return filmService.addOrDeleteLikeToFilm(filmId, userId, TypeOperations.ADD.toString());
    }


    @GetMapping(pathForFilms)
    List<Film> findFilms() {
        return new ArrayList<>(filmService.findFilms());
    }

    @GetMapping(pathForFilms + "/{id}")
    Film findFilm(@PathVariable("id") long filmId) {
        return filmService.findFilm(filmId);
    }

    @GetMapping(pathForFilms + "/popular")
    List<Film> findMostPopularFilms(@RequestParam(defaultValue = "10", name = "count") String countFilms) {
        return filmService.findMostPopularFilms(countFilms);
    }

    @GetMapping(pathForGenres)
    List<Genre> findGenres() {
        return filmService.findAllGenres();
    }

    @GetMapping(pathForGenres + "/{id}")
    Genre findGenre(@PathVariable("id") int genreId) {
        return filmService.findGenre(genreId);
    }

    @GetMapping(pathForMPA)
    List<MPA> findAllMPA() {
        return filmService.findAllMPA();
    }

    @GetMapping(pathForMPA + "/{id}")
    MPA findMPA(@PathVariable("id") int MPAId) {
        return filmService.findMPA(MPAId);
    }

    // Start of %%%%%%%%% %%%%%%%%% %%%%%%%%% Director's end points %%%%%%%%% %%%%%%%%% %%%%%%%%%
    @GetMapping(pathForFilms + "/director/{directorId}")
    List<Film> getDirectorSortedFilms(@PathVariable("directorId") int id, @RequestParam(name = "sortBy") String param) {
        System.out.println("FLAG-00- ID-- " + id);
        return filmService.getDirectorSortedFilms(id, param);
    }

    @GetMapping(pathForDirector)
    List<Director> getAllDirectors() {
        return filmService.getAllDirectors();
    }

    @GetMapping(pathForDirector + "/{id}")
    Director getDirectorById(@PathVariable("id") int id) {
        return filmService.getDirectorById(id);
    }

    @PostMapping(pathForDirector)
    Director createDirector(@RequestBody Director director) {
        return filmService.createDirector(director);
    }

    @PutMapping(pathForDirector)
    Director updateDirector(@RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @DeleteMapping(pathForDirector + "/{id}")
    Director deleteDirector(@PathVariable("id") int id) {
        return filmService.deleteDirector(id);
    }

    // End of %%%%%%%%% %%%%%%%%% %%%%%%%%% Director's end points %%%%%%%%% %%%%%%%%% %%%%%%%%%


    @GetMapping(pathForFilms+"/common")
    List <Film> getCommonFilms(@RequestParam (name = "userId") long userId,
                               @RequestParam(name = "friendId") long friendId){
       return filmService.getCommonFilms(userId, friendId);
    }
}
