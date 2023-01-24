package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.experimental.PackagePrivate;

@Data
@PackagePrivate
public class Review {
    int reviewId;
    String content;
    boolean isPositive;
    int userId;
    int filmId;
    int useful;

    public boolean getIsPositive() {
        return isPositive;
    }
}
