package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@FieldDefaults(level= AccessLevel.PRIVATE)
@Component
public class InMemoryFilmStorage implements FilmStorage {
    static final int MAX_LENGTH_DESCRIPTION = 200;
    static final int MIN_DURATION = 0;
    static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    long nextId = 1;
    final Map<Long, Film> films = new HashMap<>();

    public Map<Long, Film> getFilms() {
        return films;
    }

    public Film addNewFilm(Film film) {
        if (validateFilmFields(film)) {
            film.setId(nextId++);
            film.setUsersWhoLiked(new HashSet<>());
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм с id: " + film.getId());
        } else {
            log.info("Поля заполнены неверно");
        }
        return film;
    }

    public Film updateFilm(Film film) {
        if (validateFilmFields(film)) {
            if (!films.containsKey(film.getId())) {
                log.debug("Фильма с таким id не существует");
                throw new NotFoundException("фильма с таким id не существует");
            }
            films.put(film.getId(), film);
            log.info("Фильм с id: " + film.getId() + " успешно обновлён");
        } else {
            log.info("Поля заполнены неверно");
        }
        if (film.getUsersWhoLiked() == null) {
            film.setUsersWhoLiked(new HashSet<>());
        }
        return film;
    }

    public Collection<Film> findFilms() {
        return films.values();
    }

    public Film findFilm(long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new NotFoundException("фильма с таким id не существует");
        }
    }


    boolean validateName(Film film) {
        if (film.getName() != null && film.getName().length() > 0) {
            return  true;
        } else {
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    boolean validateDescription(Film film) {
        if (film.getDescription().length() <= MAX_LENGTH_DESCRIPTION) {
            return true;
        } else {
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
    }

    boolean validateReleaseDate(Film film) {
        if (!film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            return true;
        } else {
            throw new ValidationException("Дата выхода фильма не может быть раньше 28.12.1895");
        }
    }

    boolean validateDuration(Film film) {
        if (film.getDuration() > MIN_DURATION) {
            return true;
        } else {
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
    }

    boolean validateFilmFields(Film film) {
        return validateName(film) && validateDescription(film) && validateReleaseDate(film) && validateDuration(film);
    }
}
