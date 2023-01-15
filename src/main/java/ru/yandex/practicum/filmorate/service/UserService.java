package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addNewUser(User user) {
        log.info("Запрос на добавление пользователя");
        return userStorage.addNewUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Collection<User> findUsers() {
        log.info("Запрос на получение информации обо всех пользователях");
        return userStorage.findUsers();
    }

    public User findUser(long userId) {
        log.info("Запрос на получение информации о пользователе " + userId);
        return userStorage.findUser(userId);
    }

    public User addOrDeleteToFriends(long firstUserId, long secondUserId, String typeOperation) {
        log.info("Запрос на добавление/удаление из друзей от пользователя " + firstUserId + " пользователя " + secondUserId);
        log.info("Тип операции " + typeOperation);
        return userStorage.addOrDeleteToFriends(firstUserId, secondUserId, typeOperation);
    }

    public List<User> getMutualFriends(long firstUserId, long secondUserId) {
        log.info("Запрос на получение списка общих друзей у пользователей " + firstUserId + " и " + secondUserId);
        return userStorage.getMutualFriends(firstUserId, secondUserId);
    }

    public List<User> getFriendsList(long userId) {
        log.info("Запрос на получение списка друзей у пользователя " + userId);
        return userStorage.getFriendsList(userId);
    }
}
