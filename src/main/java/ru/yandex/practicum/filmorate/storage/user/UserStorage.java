package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addNewUser(User user);

    List<User> findUsers();

    User findUser(long userId);

    User updateUser(User user);

    User addOrDeleteToFriends(long firstUserId, long secondUserId, String typeOperation);

    List<User> getMutualFriends(long firstUserId, long secondUserId);

    List<User> getFriendsList(long userId);

    List<Event> getFeed(long userId);
}
