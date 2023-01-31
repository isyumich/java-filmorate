package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.List;

@Slf4j
@RestController
public class RecommendationController {
    private final RecommendationService recommendService;

    @Autowired
    public RecommendationController(RecommendationService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping("/users/{id}/recommendations")
    List<Film> getRecommendations(@PathVariable("id") long userId) {
        log.info("Получен запрос рекомендации фильма для пользователя id=" + userId);
        return recommendService.getRecommendation(userId);
    }

}
