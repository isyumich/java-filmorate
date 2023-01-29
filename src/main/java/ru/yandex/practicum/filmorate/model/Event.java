package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Event {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    Date timestamp;
    @NotNull
    int userId;
    String eventType;
    String operation;
    @NotNull
    int eventId;
    @NotNull
    int entityId;
}
