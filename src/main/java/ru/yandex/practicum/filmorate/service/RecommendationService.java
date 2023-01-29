package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationService {
    private final UserService userService;
    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecommendationService(UserService userService, FilmService filmService, JdbcTemplate jdbcTemplate) {
        this.filmService = filmService;
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Film> getRecommendation(Long userId) {
        LinkedHashMap<Long, List<Long>> allUsersLikes = getAllUsersLikes();
        List<Long> userLikedFilms = findUserLikes(allUsersLikes, userId);                                                      //Получаем лайки пользователя
        LinkedHashMap<Long, List<Long>> otherUsersLikedFilms = findOtherUsersLikes(allUsersLikes, userId);                           //Получаем лайки друзей
        Long otherUsersWithMutualInterests = findUsersWithMutualInterests(userLikedFilms, otherUsersLikedFilms);     //Выбираем друга, у которого будем брать рекомендацию
        List<Long> recommendation = new ArrayList<>();
        if (otherUsersLikedFilms.isEmpty()) {
            log.info("Не удалось рекомендовать фильм: не найдены лайки от друзей пользователя id=" + userId);
            return new ArrayList<Film>();
        }
        for (Long filmId : otherUsersLikedFilms.get(otherUsersWithMutualInterests)) {
            if (!userLikedFilms.contains(filmId)) {
                recommendation.add(filmId);
            }
        }
        return findMultipleFilms(recommendation);
    }

    private LinkedHashMap<Long, List<Long>> getAllUsersLikes() {
        LinkedHashMap<Long, List<Long>> likedFilms = new LinkedHashMap<>();
        String sqlQuery = "SELECT user_id, film_id FROM film_likes_by_user;";
        SqlRowSet usersFriendsLikesRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (usersFriendsLikesRows.next()) {
            Long userId = Long.parseLong(usersFriendsLikesRows.getString("user_id"));
            Long filmId = 0L;
            try {
                filmId = Long.parseLong(usersFriendsLikesRows.getString("film_id"));
            } catch (NumberFormatException e) {
            }
            if(filmId != 0L && filmId != null) {
                if (likedFilms.containsKey(userId)) {
                    likedFilms.get(userId).add(filmId);
                } else {
                    ArrayList<Long> list = new ArrayList<>();
                    list.add(filmId);
                    likedFilms.put(userId, list);
                }
            }
        }
        return likedFilms;
    }

    private List<Long> findUserLikes(LinkedHashMap<Long, List<Long>> allUsersLikes, Long userId) {
        if(allUsersLikes.containsKey(userId)) {
            return allUsersLikes.get(userId);
        }
        return new ArrayList<>();
    }

    private LinkedHashMap<Long, List<Long>> findOtherUsersLikes(LinkedHashMap<Long, List<Long>> allUsersLikes, Long userId) {
        if (!allUsersLikes.containsKey(userId) || allUsersLikes == null) {
            return allUsersLikes;
        }
        allUsersLikes.remove(userId);
        return allUsersLikes;
    }

    private Long findUsersWithMutualInterests(List<Long> userLikes, LinkedHashMap<Long, List<Long>> otherUsersLikes) {
        if (userLikes == null || userLikes.isEmpty()) {
            if (otherUsersLikes == null || otherUsersLikes.isEmpty()) {
                return 0L;
            } else {
                for (Long otherUserId : otherUsersLikes.keySet()) {
                    return otherUserId;
                }
            }
        }
        LinkedHashMap<Long, Long> matchingCounter = new LinkedHashMap<>();
        for (Long friendId : otherUsersLikes.keySet()) {
            Long counter = 0L;
            for (Long friendLikes : otherUsersLikes.get(friendId)) {
                if (userLikes.contains(friendLikes)) {
                    counter++;
                }
            }
            matchingCounter.put(friendId, counter);
        }
        matchingCounter = sortMapByValue(matchingCounter);
        if (matchingCounter.isEmpty()) {
            return 0L;
        }
        return sortMapByValue(matchingCounter).entrySet().iterator().next().getKey();
    }

    private LinkedHashMap<Long, Long> sortMapByValue(LinkedHashMap<Long, Long> unsortedMap) {
        if (unsortedMap.isEmpty()) return unsortedMap;
        return unsortedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((v1,v2)->v2.compareTo(v1)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1,v2)->v1, LinkedHashMap::new));
    }

    public List<Film> findMultipleFilms(List<Long> filmIdList) {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmRows;
        StringBuilder sqlQuery = new StringBuilder(
                "SELECT f.*, m.name AS mpa_name " +
                "FROM films f INNER JOIN MPA m  " +
                "ON f.mpa_id = m.id WHERE f.id IN("
        );
        String prefix = "";
        for (Long filmId : filmIdList) {
            sqlQuery.append(prefix);
            sqlQuery.append(filmId);
            prefix = ", ";
        }
        sqlQuery.append(");");
        return jdbcTemplate.query(sqlQuery.toString(), new FilmMapper(jdbcTemplate));
    }
}