package ru.yandex.practicum.filmorate.exception.user;

public class UserLoginValidationException extends RuntimeException{
    public UserLoginValidationException(String s) {
        super(s);
    }
}
