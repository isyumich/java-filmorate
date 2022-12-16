package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@FieldDefaults(level= AccessLevel.PRIVATE)
@Component
public class InMemoryUserStorage implements UserStorage {
    long nextId = 1;
    final Map<Long, User> users = new HashMap<>();

    public Map<Long, User> getUsers() {
        return users;
    }
    public User addNewUser(User user) {
        if (user.getName() == null || Objects.equals(user.getName(), "") || Objects.equals(user.getName(), " ")) {
            user.setName(user.getLogin());
        }
        if (validateUserFields(user)) {
            user.setId(nextId++);
            user.setFriendsIdsSet(new HashSet<>());
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь с id: " + user.getId());
        } else {
            log.info("Поля заполнены неверно");
        }
        return user;
    }

    public User updateUser(User user) {
        if (validateUserFields(user)) {
            if (!users.containsKey(user.getId())) {
                log.debug("Пользователя с таким id не существует");
                throw new NotFoundException("Пользователя с таким id не существует");
            } else {
                users.put(user.getId(), user);
                log.info("Пользователь с id: " + user.getId() + " успешно обновлён");
            }
        } else {
            log.info("Поля заполнены неверно");
        }
        if (user.getFriendsIdsSet() == null) {
            user.setFriendsIdsSet(new HashSet<>());
        }
        return user;
    }

    public Collection<User> findUsers() {
        log.info("Количество пользователей: " + users.size());
        return users.values();
    }

    public User findUser(long userId) {
        if (users.get(userId) != null) {
            log.info("Пользователь найден");
            return users.get(userId);
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }


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

    boolean validateUserFields(User user) {
        return validateEmail(user) && validateLogin(user) && validateBirthday(user);
    }
}
