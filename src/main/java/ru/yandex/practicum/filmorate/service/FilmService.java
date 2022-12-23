package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmService {
    public final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addOrDeleteLikeToFilm(long filmId, long userId, String typeOperation) {
        if (filmStorage.getFilms().containsKey(filmId)) {
            Film film = filmStorage.getFilms().get(filmId);
            Set<Long> newSetWithLikes = film.getUsersWhoLiked();
            switch (typeOperation) {
                case ("DELETE"):
                    if (newSetWithLikes.contains(userId)) {
                        newSetWithLikes.remove(userId);
                    } else {
                        throw new NotFoundException("Данный пользователь не ставил лайк этому фильму, удаление невозможно");
                    }
                    log.info("Удален лайк от пользователя");
                    break;
                case ("ADD"):
                    newSetWithLikes.add(userId);
                    log.info("Добавлен лайк от пользователя");
                    break;
                default:
                    break;
            }
            film.setUsersWhoLiked(newSetWithLikes);
            return film;
        } else {
            throw new NotFoundException("Фильм с таким id не найден");
        }

    }

    public List<Film> findMostPopularFilms(String countFilms) {
        return filmStorage.findFilms().stream()
                .sorted((o1, o2) -> (int) (o2.getCountLikes() - o1.getCountLikes()))
                .limit(Long.parseLong(countFilms))
                .collect(Collectors.toList());
    }
}
