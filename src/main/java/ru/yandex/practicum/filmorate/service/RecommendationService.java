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
        List<Long> userLikedFilms = findUserLikes(userId);                                                      //Получаем лайки пользователя
        LinkedHashMap<Long, List<Long>> friendsLikedFilms = findFriendsLikes(userId);                           //Получаем лайки друзей
        Long friendWithMutualInterests = findFriendWithMutualInterests(userLikedFilms, friendsLikedFilms);     //Выбираем друга, у которого будем брать рекомендацию
        List<Long> recommendation = new ArrayList<>();
        if (friendsLikedFilms.isEmpty()) {
            log.info("Не удалось рекомендовать фильм: не найдены лайки от друзей пользователя id=" + userId);
            return new ArrayList<Film>();
        }
        for (Long filmId : friendsLikedFilms.get(friendWithMutualInterests)) {
            if (!userLikedFilms.contains(filmId)) {
                recommendation.add(filmId);
            }
        }
        return findMultipleFilms(recommendation);
    }

    private List<Long> findUserLikes(Long userId) {
        List<Long> likedFilms = new ArrayList<>();
        String sqlQuery =
                "SELECT user_id, film_id " +
                "FROM film_likes_by_user " +
                "WHERE user_id = " + userId + ";";
        SqlRowSet userLikesRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (userLikesRows.next()) {
            Long filmId = Long.parseLong(userLikesRows.getString("film_id"));
            likedFilms.add(filmId);
        }
        return likedFilms;
    }

    private LinkedHashMap<Long, List<Long>> findFriendsLikes(Long userId){
        LinkedHashMap<Long, List<Long>> likedFilms = new LinkedHashMap<>();
        String sqlQuery =
                "SELECT fl.user_id, fl.friend_id, flbu.film_id " +
                "FROM users u " +
                "JOIN friends_list fl ON fl.user_id = u.id " +
                "LEFT JOIN film_likes_by_user flbu ON fl.friend_id = flbu.user_id " +
                "WHERE u.id = " + userId + ";";
        SqlRowSet usersFriendsLikesRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (usersFriendsLikesRows.next()) {
            Long friend = Long.parseLong(usersFriendsLikesRows.getString("friend_id"));
            Long film = 0L;
            try {
                film = Long.parseLong(usersFriendsLikesRows.getString("film_id"));
            } catch (NumberFormatException e) {
            }
            if(film != 0L && film != null) {
                if (likedFilms.containsKey(friend)) {
                    likedFilms.get(friend).add(film);
                } else {
                    ArrayList<Long> list = new ArrayList<>();
                    list.add(film);
                    likedFilms.put(friend, list);
                }
            }
        }
        return likedFilms;
    }

    private Long findFriendWithMutualInterests(List<Long> userLikes, LinkedHashMap<Long, List<Long>> friendsLikes) {
        if (userLikes.isEmpty()) {
            if (friendsLikes.isEmpty()) {
                return 0L;
            } else {
                for (Long friendId : friendsLikes.keySet()) {
                    return friendId;
                }
            }
        }
        LinkedHashMap<Long, Long> matchingCounter = new LinkedHashMap<>();
        for (Long friendId : friendsLikes.keySet()) {
            Long counter = 0L;
            for (Long friendLikes : friendsLikes.get(friendId)) {
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