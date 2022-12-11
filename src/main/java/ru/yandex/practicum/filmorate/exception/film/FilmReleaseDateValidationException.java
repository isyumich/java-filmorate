package ru.yandex.practicum.filmorate.exception.film;

public class FilmReleaseDateValidationException extends RuntimeException{
    public FilmReleaseDateValidationException(String s) {
        super(s);
    }
}
