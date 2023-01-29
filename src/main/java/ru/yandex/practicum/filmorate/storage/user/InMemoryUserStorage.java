package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    final UserValidation userValidation = new UserValidation();
    long nextId = 1;
    final Map<Long, User> users = new HashMap<>();

    public User addNewUser(User user) {
        if (user.getName() == null || Objects.equals(user.getName(), "") || Objects.equals(user.getName(), " ")) {
            user.setName(user.getLogin());
        }
        if (userValidation.validateUserFields(user)) {
            user.setId(nextId++);
            user.setFriendsIdsSet(new HashSet<>());
            users.put(user.getId(), user);
            log.info(String.format("%s %d", "Добавлен новый пользователь с id:", user.getId()));
        } else {
            log.info("Поля заполнены неверно");
        }
        return user;
    }

    public User updateUser(User user) {
        if (userValidation.validateUserFields(user)) {
            if (!users.containsKey(user.getId())) {
                log.debug(String.format("%s %d %s", "Пользователь с id:", user.getId(), "не найден"));
                throw new NotFoundException(String.format("%s %d %s", "Пользователь с id:", user.getId(), "не найден"));
            } else {
                users.put(user.getId(), user);
                log.info(String.format("%s %d %s", "Пользователь с id:", user.getId(), "успешно обновлён"));
            }
        } else {
            log.info("Поля заполнены неверно");
        }
        if (user.getFriendsIdsSet() == null) {
            user.setFriendsIdsSet(new HashSet<>());
        }
        return user;
    }

    public List<User> findUsers() {
        return new ArrayList<>(users.values());
    }

    public User findUser(long userId) {
        if (users.get(userId) != null) {
            return users.get(userId);
        } else {
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id:", userId, "не найден"));
        }
    }

    public User addOrDeleteToFriends(long firstUserId, long secondUserId, String typeOperation) {
        User firstUser = users.get(firstUserId);
        User secondUser = users.get(secondUserId);
        if (firstUser != null && secondUser != null) {
            Set<Long> firstUserFriendsSet = firstUser.getFriendsIdsSet();
            Set<Long> secondUserFriendsSet = secondUser.getFriendsIdsSet();
            switch (typeOperation) {
                case "DELETE":
                    firstUserFriendsSet.remove(secondUser.getId());
                    secondUserFriendsSet.remove(firstUser.getId());
                    log.info(String.format("%s %d %s %d", "Пользователь с id", firstUserId, "удалил из друзей пользователя с id", secondUserId));
                    break;
                case "ADD":
                    firstUserFriendsSet.add(secondUser.getId());
                    secondUserFriendsSet.add(firstUser.getId());
                    log.info(String.format("%s %d %s %d", "Пользователь с id", firstUserId, "добавил в друзья пользователя с id", secondUserId));
                    break;
                default:
                    break;
            }
            firstUser.setFriendsIdsSet(firstUserFriendsSet);
            secondUser.setFriendsIdsSet(secondUserFriendsSet);
            return firstUser;
        } else {
            throw new NotFoundException(String.format("%s %d %s %d %s", "Пользователь с id", firstUserId, "или друг с id", secondUserId, "не найден"));
        }
    }

    public List<User> getMutualFriends(long firstUserId, long secondUserId) {
        User firstUser = users.get(firstUserId);
        User secondUser = users.get(secondUserId);
        if (firstUser != null && secondUser != null) {
            List<Long> firstUserFriendsList = firstUser.getFriendsIdsSet().stream().collect(Collectors.toList());
            List<Long> secondUserFriendsList = secondUser.getFriendsIdsSet().stream().collect(Collectors.toList());
            List<Long> mutualFriendsIdsList = new ArrayList<>();
            for (long firstFriendFriendId : firstUserFriendsList) {
                for (int j = 0; j < secondUserFriendsList.size(); j++) {
                    if (secondUserFriendsList.contains(firstFriendFriendId)) {
                        mutualFriendsIdsList.add(firstFriendFriendId);
                    }
                }
            }
            List<User> mutualFriendsList = new ArrayList<>();
            for (User user : users.values()) {
                if (mutualFriendsIdsList.contains(user.getId())) {
                    mutualFriendsList.add(user);
                }
            }
            return mutualFriendsList;
        } else {
            throw new NotFoundException(String.format("%s %d %s %d %s", "Пользователь с id", firstUserId, "или друг с id", secondUserId, "не найден"));
        }
    }

    public List<User> getFriendsList(long userId) {
        List<User> friendsSet = new ArrayList<>();
        Set<Long> friendsIdsSet = users.get(userId).getFriendsIdsSet();
        for (long friendId : friendsIdsSet) {
            friendsSet.add(users.get(friendId));
        }
        return friendsSet;
    }

    @Override
    public List<Event> getFeed(long userId) {
        return null;
    }
}
