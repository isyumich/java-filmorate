package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    User addNewUser(User user);
    Collection<User> findUsers();
    User findUser(long userId);
    User updateUser(User user);
    Map<Long, User> getUsers();
}
