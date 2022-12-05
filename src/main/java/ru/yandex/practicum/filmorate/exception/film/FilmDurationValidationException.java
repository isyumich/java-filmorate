package ru.yandex.practicum.filmorate.exception.film;

public class FilmDurationValidationException extends RuntimeException{
    public FilmDurationValidationException(String s) {
        super(s);
    }
}
