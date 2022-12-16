package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserService {
    final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addOrDeleteToFriends(long firstUserId, long secondUserId, String typeOperation) {
        User firstUser = userStorage.getUsers().get(firstUserId);
        User secondUser =userStorage.getUsers().get(secondUserId);
        if (firstUser != null && secondUser != null) {
            Set<Long> firstUserFriendsSet = firstUser.getFriendsIdsSet();
            Set<Long> secondUserFriendsSet = secondUser.getFriendsIdsSet();
            switch (typeOperation) {
                case "DELETE":
                    firstUserFriendsSet.remove(secondUser.getId());
                    secondUserFriendsSet.remove(firstUser.getId());
                    break;
                case "ADD":
                    firstUserFriendsSet.add(secondUser.getId());
                    secondUserFriendsSet.add(firstUser.getId());
                    break;
                default:
                    break;
            }
            firstUser.setFriendsIdsSet(firstUserFriendsSet);
            secondUser.setFriendsIdsSet(secondUserFriendsSet);
            return firstUser;
        } else {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }

    public List<User> getMutualFriends(long firstUserId, long secondUserId) {
        User firstUser = userStorage.getUsers().get(firstUserId);
        User secondUser = userStorage.getUsers().get(secondUserId);
        if (firstUser != null && secondUser != null) {
            List<Long> firstUserFriendsList = firstUser.getFriendsIdsSet().stream().toList();
            List<Long> secondUserFriendsList = secondUser.getFriendsIdsSet().stream().toList();
            List<Long> mutualFriendsIdsList = new ArrayList<>();
            for (long firstFriendFriendId : firstUserFriendsList) {
                for (int j = 0; j < secondUserFriendsList.size(); j++) {
                    if (secondUserFriendsList.contains(firstFriendFriendId)) {
                        mutualFriendsIdsList.add(firstFriendFriendId);
                    }
                }
            }
            List<User> mutualFriendsList = new ArrayList<>();
            for (User user : userStorage.getUsers().values()) {
                if (mutualFriendsIdsList.contains(user.getId())) {
                    mutualFriendsList.add(user);
                }
            }
            return mutualFriendsList;
        } else {
            throw new NotFoundException("Пользователь с таким id не существует");
        }
    }

    public List<User> getFriendsSet(long userId) {
        List<User> friendsSet = new ArrayList<>();
        Set<Long> friendsIdsSet = userStorage.getUsers().get(userId).getFriendsIdsSet();
        for (long friendId : friendsIdsSet) {
            friendsSet.add(userStorage.getUsers().get(friendId));
        }
        return friendsSet;
    }
}
