package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.TypeOperations;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    User addNewUser(@RequestBody User user) {
        log.info("Получен запрос POST на добавление пользователя");
        return userService.userStorage.addNewUser(user);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    User deleteFromFriend(@PathVariable("id") long firstUserId, @PathVariable("friendId") long secondUserId) {
        log.info("Получен запрос DELETE на удаление из друзей у пользователя " + firstUserId + " пользователя " + secondUserId);
        return userService.addOrDeleteToFriends(firstUserId, secondUserId, TypeOperations.DELETE.toString());
    }


    @PutMapping
    User updateUser(@RequestBody User user) {
        return userService.userStorage.updateUser(user);
    }
    @PutMapping("/{id}/friends/{friendId}")
    User addToFriend(@PathVariable("id") long firstUserId, @PathVariable("friendId") long secondUserId) {
        log.info("Получен запрос PUT на добавление в друзья от пользователя " + firstUserId + " пользователя " + secondUserId);
        return userService.addOrDeleteToFriends(firstUserId, secondUserId, TypeOperations.ADD.toString());
    }


    @GetMapping
    Collection<User> findUsers() {
        log.info("Получен запрос GET на получение информации обо всех пользователях");
        return userService.userStorage.findUsers();
    }
    @GetMapping("/{id}")
    User findUser(@PathVariable("id") long userId) {
        log.info("Получен запрос GET на получение информации о пользователе " + userId);
        return userService.userStorage.findUser(userId);
    }
    @GetMapping("/{id}/friends/common/{otherId}")
    List<User> getMutualFriends(@PathVariable("id") long firstUserId, @PathVariable("otherId") long secondUserId) {
        log.info("Получен запрос GET на получение списка общих друзей у пользователей " + firstUserId + " и " + secondUserId);
        return userService.getMutualFriends(firstUserId, secondUserId);
    }
    @GetMapping("/{id}/friends")
    List<User> getFriendsSet(@PathVariable("id") long userId) {
        log.info("Получен запрос GET на получение списка друзей у пользователя " + userId);
        return userService.getFriendsSet(userId);
    }
}
