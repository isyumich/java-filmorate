package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.user.UserBirthdayValidationException;
import ru.yandex.practicum.filmorate.exception.user.UserEmailValidationException;
import ru.yandex.practicum.filmorate.exception.user.IsAlreadyUserExistException;
import ru.yandex.practicum.filmorate.exception.user.UserLoginValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int nextId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findUsers() {
        log.info("Метод GET, количество пользователей: " + users.size());
        return users.values();
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (validateUserFields(user)) {
            if (!users.containsKey(user.getId())) {
                log.debug("Метод PUT: Пользователя с таким id не существует");
                throw new IsAlreadyUserExistException("Пользователя с таким id не существует");
            } else {
                users.put(user.getId(), user);
                log.info("Метод PUT: пользователь обновлён");
            }
        }
        return user;
    }

    @PostMapping
    public User addNewUser(@RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (validateUserFields(user)) {
                user.setId(nextId++);
                users.put(user.getId(), user);
                log.info("Метод POST: пользователь добавлен");
            }
        return user;
    }

    private boolean validateEmail(User user) {
        if (user.getEmail() == null || user.getEmail().length() == 0) {
            throw new UserEmailValidationException("Поле Email не может быть пустым");
        } else if (!user.getEmail().contains("@")) {
            throw new UserEmailValidationException("Поле Email должно содержать символ @");
        } else {
            return true;
        }
    }

    private boolean validateLogin(User user) {
        if (user.getLogin() == null || user.getLogin().length() == 0) {
            throw new UserLoginValidationException("Поле Login не может быть пустым");
        } else if (user.getLogin().contains(" ")) {
            throw new UserLoginValidationException("Поле Login не может содержать пробелов");
        } else {
            return true;
        }
    }

    private boolean validateBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new UserBirthdayValidationException("Дата рождения не может быть больше текущей даты");
        } else {
            return true;
        }
    }

    private boolean validateUserFields(User user) {
        return validateEmail(user) && validateLogin(user) && validateBirthday(user);
    }
}
