package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Film {
    @NonNull
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    @JsonIgnore
    Set<Long> usersWhoLiked;

    public long getCountLikes() {
        if (usersWhoLiked == null) {
            return 0;
        } else {
            return usersWhoLiked.size();
        }
    }
}
