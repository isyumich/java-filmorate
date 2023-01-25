package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface FilmStorage {
    Film addNewFilm(Film film);

    Film updateFilm(Film film);

    List<Film> findFilms();

    Film findFilm(long filmId);

    Film addOrDeleteLikeToFilm(long filmId, long userId, String typeOperation);

    List<Film> findMostPopularFilms(String countFilms);

    List<Genre> findAllGenres();

    Genre findGenre(int genreId);

    List<MPA> findAllMPA();

    MPA findMPA(int mpaId);

    List<Film> searchFilmByDirector(String query, List<String> values);
    List<Film> searchFilmByTitle(String query, List<String> values);
    List<Film> searchFilmByTitleAndDirector(String query, List<String> values);
}
