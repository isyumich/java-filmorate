package ru.yandex.practicum.filmorate.storage.user.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UserMapper implements RowMapper<User> {
    final JdbcTemplate jdbcTemplate;

    public UserMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        setFriendsList(user);
        return user;

    }

    private void setFriendsList(User user) {
        Set<Long> friendsSet = new HashSet<>(jdbcTemplate.queryForList("SELECT friend_id FROM friends_list WHERE user_id = ?;", Long.class, user.getId()));
        user.setFriendsIdsSet(friendsSet);
    }
}
