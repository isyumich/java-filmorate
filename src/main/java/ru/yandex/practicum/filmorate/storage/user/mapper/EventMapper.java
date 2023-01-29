package ru.yandex.practicum.filmorate.storage.user.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation_type"))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getTimestamp("date_time"))
                .build();
    }
}
