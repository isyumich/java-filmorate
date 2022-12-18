package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Film addNewFilm(Film film);
    Film updateFilm(Film film);
    Collection<Film> findFilms();
    Film findFilm(long filmId);
    Map<Long, Film> getFilms();

}
