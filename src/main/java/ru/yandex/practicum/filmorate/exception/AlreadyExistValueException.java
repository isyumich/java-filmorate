package ru.yandex.practicum.filmorate.exception;

public class AlreadyExistValueException extends RuntimeException {
    public AlreadyExistValueException(String s) {
        super(s);
    }
}
