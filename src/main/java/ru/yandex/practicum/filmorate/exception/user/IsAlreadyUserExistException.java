package ru.yandex.practicum.filmorate.exception.user;

public class IsAlreadyUserExistException extends RuntimeException{
    public IsAlreadyUserExistException(String s) {
        super(s);
    }
}
