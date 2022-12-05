package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.film.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int nextId = 1;
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final int MIN_DURATION = 0;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();


    @GetMapping
    public Collection<Film> findFilms() {
        log.info("Метод GET, количество фильмов: " + films.size());
        return films.values();
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (validateFilmFields(film)) {
            if (!films.containsKey(film.getId())) {
                log.debug("Метод PUT: пользователя с таким id не существует");
                throw new IsAlreadyFilmExistException("пользователя с таким id не существует");
            }
            films.put(film.getId(), film);
            log.info("Метод PUT: фильм обновлён");
        }
        return film;
    }

    @PostMapping
    public Film addNewFilm(@RequestBody Film film) {
        if (validateFilmFields(film)) {
                film.setId(nextId++);
                films.put(film.getId(), film);
                log.info("Метод POST: фильм добавлен");
            }
        return film;
    }

    private boolean validateName(Film film) {
        if (film.getName() != null && film.getName().length() > 0) {
            return true;
        } else {
            throw new FilmNameValidationException("Название фильма не может быть пустым");
        }
    }

    private boolean validateDescription(Film film) {
        if (film.getDescription().length() <= MAX_LENGTH_DESCRIPTION) {
            return true;
        } else {
            throw new FilmDescValidationException("Максимальная длина описания - 200 символов");
        }
    }

    private boolean validateReleaseDate(Film film) {
        if (!film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            return true;
        } else {
            throw new FilmReleaseDateValidationException("Дата выхода фильма не может быть раньше 28.12.1895");
        }
    }

    private boolean validateDuration(Film film) {
        if (film.getDuration() > MIN_DURATION) {
            return true;
        } else {
            throw new FilmDurationValidationException("Продолжительность фильма должна быть больше 0");
        }
    }

    private boolean validateFilmFields(Film film) {
        return validateName(film) && validateDescription(film) && validateReleaseDate(film) && validateDuration(film);
    }

}
