package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MPAMapper implements RowMapper<MPA> {
    @Override
    public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MPA.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .minAgeForWatching(rs.getInt("min_age_for_watching"))
                .build();
    }
}
