package ru.yandex.practicum.filmorate.exception.film;

public class FilmNameValidationException extends RuntimeException{
    public FilmNameValidationException(String s) {
        super(s);
    }
}
