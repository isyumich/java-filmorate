package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;
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


    List<Genre> findAllGenres();

    Genre findGenre(int genreId);

    List<MPA> findAllMPA();

    MPA findMPA(int mpaId);
    List <Film> findMostPopularFilms(String limit, String genreId, String year);

    // Start of %%%%%%%%% %%%%%%%%% %%%%%%%%% Director's funcs %%%%%%%%% %%%%%%%%% %%%%%%%%%
    List<Film> getDirectorSortedFilms(int id, String param);

    List<Director> getAllDirectors();

    Director getDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    Director deleteDirector(int id);

    // End of %%%%%%%%% %%%%%%%%% %%%%%%%%% Director's services %%%%%%%%% %%%%%%%%% %%%%%%%%%


    void deleteFilm (long id);
}
