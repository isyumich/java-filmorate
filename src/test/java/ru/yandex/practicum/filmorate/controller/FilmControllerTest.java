package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.film.*;
import ru.yandex.practicum.filmorate.model.Film;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void validateDescException() {
        final Film filmWithEmptyDesc = Film.builder()
                .id(1)
                .description("")
                .name("name1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(180)
                .build();
        assertThrows(FilmDescValidationException.class, () -> filmController.addNewFilm(filmWithEmptyDesc), "without desc exception");
    }

    @Test
    void validateDurationException() {
        final Film filmZeroDuration = Film.builder()
                .id(1)
                .description("desc1")
                .name("name1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(0)
                .build();
        assertThrows(FilmDurationValidationException.class, () -> filmController.addNewFilm(filmZeroDuration), "without duration exception");
    }

    @Test
    void validateNameException() {
        final Film filmWithEmptyName = Film.builder()
                .id(1)
                .description("desc1")
                .name("")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(180)
                .build();
        assertThrows(FilmNameValidationException.class, () -> filmController.addNewFilm(filmWithEmptyName), "without name exception");
    }

    @Test
    void validateReleaseDateException() {
        final Film filmWithTooEarlyReleaseDate = Film.builder()
                .id(1)
                .description("desc1")
                .name("name1")
                .releaseDate(LocalDate.of(1000, 1, 1))
                .duration(180)
                .build();
        assertThrows(FilmReleaseDateValidationException.class, () -> filmController.addNewFilm(filmWithTooEarlyReleaseDate), "without name exception");
    }

    @Test
    void validateAlreadyExistFilmTest() {
        final Film film = Film.builder()
                .id(1)
                .description("desc1")
                .name("name1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(180)
                .build();
        filmController.addNewFilm(film);
        assertThrows(IsAlreadyFilmExistException.class, () -> filmController.addNewFilm(film), "without name exception");
    }
}
