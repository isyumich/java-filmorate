package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    List<Genre> genres;
    MPA mpa;
    List<Director> directors;
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
