package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.PackagePrivate;

@Data
@PackagePrivate
public class Review {
    int reviewId;
    String content;
    Boolean isPositive;
    Integer userId;
    Integer filmId;
    int useful;

    public Boolean getIsPositive() {
        return isPositive;
    }

    public void setPositive(boolean is_positive) {
        isPositive = is_positive;
    }
}
