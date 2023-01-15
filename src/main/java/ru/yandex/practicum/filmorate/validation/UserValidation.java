package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidation {

    boolean validateEmail(User user) {
        if (user.getEmail() == null || user.getEmail().length() == 0) {
            throw new ValidationException("Поле Email не может быть пустым");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException("Поле Email должно содержать символ @");
        } else {
            return true;
        }
    }

    boolean validateLogin(User user) {
        if (user.getLogin() == null || user.getLogin().length() == 0) {
            throw new ValidationException("Поле Login не может быть пустым");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidationException("Поле Login не может содержать пробелов");
        } else {
            return true;
        }
    }

    boolean validateBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть больше текущей даты");
        } else {
            return true;
        }
    }

    public boolean validateUserFields(User user) {
        return validateEmail(user) && validateLogin(user) && validateBirthday(user);
    }
}
