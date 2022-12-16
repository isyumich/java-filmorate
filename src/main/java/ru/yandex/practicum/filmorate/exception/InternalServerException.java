package ru.yandex.practicum.filmorate.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String s) {
        super(s);
    }
}
