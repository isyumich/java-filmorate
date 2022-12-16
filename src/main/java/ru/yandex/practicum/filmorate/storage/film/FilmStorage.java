package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Film addNewFilm(Film film);
    Film updateFilm(Film film);
    Collection<Film> findFilms();
    Film findFilm(long filmId);
    Map<Long, Film> getFilms();

}
