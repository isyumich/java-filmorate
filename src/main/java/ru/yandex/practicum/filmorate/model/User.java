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
public class User {
    @NonNull
    long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    @JsonIgnore
    Set<Long> friendsIdsSet;
}
