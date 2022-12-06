package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.film.*;
import ru.yandex.practicum.filmorate.model.Film;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = createFilm();
    }

    private Film createFilm() {
        return Film.builder()
                .description("DescriptionFilm")
                .name("NameFilm")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(180)
                .build();
    }

    @Test
    void validateDescException() {
        film.setDescription("Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора», каждый из узников" +
                " которого однажды проходит «зеленую милю» по пути к месту казни. Пол повидал много заключённых" +
                " и надзирателей за время работы. Однако гигант Джон Коффи, обвинённый в страшном преступлении, " +
                "стал одним из самых необычных обитателей блока.");
        assertThrows(FilmDescValidationException.class, () -> filmController.addNewFilm(film), "without desc exception");
    }

    @Test
    void validateDurationException() {
        film.setDuration(0);
        assertThrows(FilmDurationValidationException.class, () -> filmController.addNewFilm(film), "without duration exception");
    }

    @Test
    void validateNameException() {
        film.setName("");
        assertThrows(FilmNameValidationException.class, () -> filmController.addNewFilm(film), "without name exception");
    }

    @Test
    void validateReleaseDateException() {
        film.setReleaseDate(LocalDate.of(1800, 1,1));
        assertThrows(FilmReleaseDateValidationException.class, () -> filmController.addNewFilm(film), "without name exception");
    }

    @Test
    void validateUpdateNotExistFilmTest() {
        filmController.addNewFilm(film);
        film.setId(2);
        assertThrows(IsAlreadyFilmExistException.class, () -> filmController.updateFilm(film), "without exception");
    }
}
