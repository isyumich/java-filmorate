package ru.yandex.practicum.filmorate.exception.user;

public class UserEmailValidationException extends RuntimeException{
    public UserEmailValidationException(String s) {
        super(s);
    }
}
