package ru.yandex.practicum.filmorate.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmValidation {
    static final int MAX_LENGTH_DESCRIPTION = 200;
    static final int MIN_DURATION = 0;
    static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    boolean validateName(Film film) {
        if (film.getName() != null && film.getName().length() > 0) {
            return true;
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

    public boolean validateFilmFields(Film film) {
        return validateName(film) && validateDescription(film) && validateReleaseDate(film) && validateDuration(film);
    }
}
