package ru.yandex.practicum.filmorate.exception.film;

public class IsAlreadyFilmExistException extends RuntimeException{
    public IsAlreadyFilmExistException (String s) {
        super(s);
    }
}
