package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.TypeOperations;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    final UserService userService;
    final String pathForAddOrDeleteFriends = "/{id}/friends/{friendId}";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    User addNewUser(@RequestBody User user) {
        return userService.addNewUser(user);
    }


    @DeleteMapping(pathForAddOrDeleteFriends)
    User deleteFromFriend(@PathVariable("id") long firstUserId, @PathVariable("friendId") long secondUserId) {
        return userService.addOrDeleteToFriends(firstUserId, secondUserId, TypeOperations.DELETE.toString());
    }


    @PutMapping
    User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping(pathForAddOrDeleteFriends)
    User addToFriend(@PathVariable("id") long firstUserId, @PathVariable("friendId") long secondUserId) {
        return userService.addOrDeleteToFriends(firstUserId, secondUserId, TypeOperations.ADD.toString());
    }


    @GetMapping
    List<User> findUsers() {
        return new ArrayList<>(userService.findUsers());
    }

    @GetMapping("/{id}")
    User findUser(@PathVariable("id") long userId) {
        return userService.findUser(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    List<User> getMutualFriends(@PathVariable("id") long firstUserId, @PathVariable("otherId") long secondUserId) {
        return userService.getMutualFriends(firstUserId, secondUserId);
    }

    @GetMapping("/{id}/friends")
    List<User> getFriendsList(@PathVariable("id") long userId) {
        return userService.getFriendsList(userId);
    }
}
