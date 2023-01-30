package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistValueException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    final UserValidation userValidation = new UserValidation();

    final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User addNewUser(User user) {
        changeEmptyName(user);
        if (userValidation.validateUserFields(user)) {
            long id;
            String query = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?);";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(query, new String[]{"id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);
            id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            user.setId(id);
            log.info(String.format("%s %d", "Добавлен новый пользователь с id:", user.getId()));
        } else {
            log.info("Поля заполнены неверно");
        }
        setEmptyFriendsSet(user);
        return user;
    }

    public User updateUser(User user) {
        changeEmptyName(user);

        if (userValidation.validateUserFields(user)) {
            int countLines = jdbcTemplate.update("UPDATE users SET email = ?, " +
                            "login = ?, " +
                            "name = ?, " +
                            "birthday = ? WHERE id = ?;",
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            if (countLines == 0) {
                log.debug(String.format("%s %d %s", "Пользователь с id:", user.getId(), "не найден"));
                throw new NotFoundException(String.format("%s %d %s", "Пользователь с id:", user.getId(), "не найден"));
            }
            log.info(String.format("%s %d %s", "Пользователь с id:", user.getId(), "успешно обновлён"));
        } else {
            log.info("Поля заполнены неверно");
        }
        setEmptyFriendsSet(user);
        return user;
    }

    public List<User> findUsers() {
        String query = "SELECT * FROM users;";
        return jdbcTemplate.query(query, new UserMapper(jdbcTemplate));
    }

    public User findUser(long userId) {
        User user;
        try {
            user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?;", new UserMapper(jdbcTemplate), userId);
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id:", userId, "не найден"));
        }
    }

    public User addOrDeleteToFriends(long firstUserId, long secondUserId, String typeOperation) {
        User firstUser;
        User secondUser;
        try {
            firstUser = jdbcTemplate.queryForObject("SELECT * from users WHERE id = ?;", new UserMapper(jdbcTemplate), firstUserId);
            secondUser = jdbcTemplate.queryForObject("SELECT * from users WHERE id = ?;", new UserMapper(jdbcTemplate), secondUserId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("%s %d %s %d %s", "Пользователь с id", firstUserId, "или друг с id", secondUserId, "не найден"));
        }
        switch (typeOperation) {
            case "DELETE":
                int countLinesDelete = jdbcTemplate.update("DELETE FROM friends_list WHERE user_id = ? and friend_id = ?;", firstUserId, secondUserId);
                if (countLinesDelete == 0) {
                    throw new NotFoundException(String.format("%s %d %s %d", "У пользователя с id", firstUserId, "в друзьях нет пользователя с id", secondUserId));
                }
                log.info(String.format("%s %d %s %d", "Пользователь с id", firstUserId, "удалил из друзей пользователя с id", secondUserId));
                break;
            case "ADD":
                int countLinesSelect = jdbcTemplate.queryForObject("SELECT COUNT(user_id) FROM friends_list WHERE user_id = ? and friend_id = ?;", Integer.class, firstUserId, secondUserId);
                if (countLinesSelect > 0) {
                    throw new AlreadyExistValueException(String.format("%s %d %s %d", "Пользователь с id", firstUserId, "уже находится в друзьях пользователь с id", secondUserId));
                }
                jdbcTemplate.update("INSERT INTO friends_list (user_id, friend_id) VALUES (?, ?);", firstUserId, secondUserId);
                log.info(String.format("%s %d %s %d", "Пользователь с id", firstUserId, "добавил в друзья пользователя с id", secondUserId));
                break;
            default:
                break;
        }
        return firstUser;
    }

    public List<User> getMutualFriends(long firstUserId, long secondUserId) {
        String subQuery = "(SELECT friend_id FROM friends_list WHERE user_id = ? " +
                "INTERSECT " +
                "SELECT friend_id FROM friends_list WHERE user_id = ?);";
        String query = "SELECT * FROM users WHERE id IN " + subQuery;
        return jdbcTemplate.query(query, new UserMapper(jdbcTemplate), firstUserId, secondUserId);
    }

    public List<User> getFriendsList(long userId) {
        userExistCheckUp(userId);
        String query = "SELECT * FROM users WHERE id IN (SELECT friend_id FROM friends_list WHERE user_id = ?);";
        return jdbcTemplate.query(query, new UserMapper(jdbcTemplate), userId);
    }

    @Override
    public void deleteUser(long id) {
        userExistCheckUp(id);
        jdbcTemplate.update("delete  from FILM_LIKES_BY_USER where USER_ID = ? ", id);
        jdbcTemplate.update("delete  from FRIENDS_LIST where USER_ID = ? ", id);
        jdbcTemplate.update("delete  from FRIENDS_LIST where FRIEND_ID = ? ", id);
        jdbcTemplate.update("delete  from USERS where ID = ? ", id);
        log.info("Удалён пользователь с id : {} ", id);
    }

    void userExistCheckUp(long id) {
        if (!jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE id = ?", id).next()) {
            throw new NotFoundException("User not found");
        }
    }

    private void changeEmptyName(User user) {
        if (user.getName() == null || Objects.equals(user.getName(), "") || Objects.equals(user.getName(), " ")) {
            user.setName(user.getLogin());
        }
    }

    private void setEmptyFriendsSet(User user) {
        if (user.getFriendsIdsSet() == null) {
            user.setFriendsIdsSet(new HashSet<>());
        }
    }
}
