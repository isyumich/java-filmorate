package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewDBService;

import java.util.List;

@RestController
public class ReviewController {

    final ReviewDBService reviewDBService;

    private final String ep_reviews = "/reviews";
    private final String ep_reviewById = "/reviews/{id}";
    private final String ep_reviewLike = "/reviews/{id}/like/{userId}";
    private final String ep_reviewDislike = "/reviews/{id}/dislike/{userId}";

    public ReviewController(ReviewDBService reviewDBService) {
        this.reviewDBService = reviewDBService;
    }

    @GetMapping(ep_reviewById)
    public Review findReviewById(@PathVariable int id) {
        return reviewDBService.findReviewById(id);
    }

    @GetMapping(value = ep_reviews)
    public List<Review> getReviewListByFilmId(@RequestParam(defaultValue = "0", required = false) int filmId, @RequestParam(defaultValue = "10", required = false) int count) {
        return reviewDBService.getReviewListByFilmId(filmId, count);
    }

    @PutMapping(ep_reviews)
    public Review updateReview(@RequestBody Review review) {
        return reviewDBService.updateReview(review);
    }

    @PutMapping(ep_reviewLike)
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        reviewDBService.updateStatus(id, userId, 1);
    }

    @PutMapping(ep_reviewDislike)
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        reviewDBService.updateStatus(id, userId, -1);
    }

    @PostMapping(ep_reviews)
    public Review addReview(@RequestBody Review review) {
        return reviewDBService.addReview(review);
    }

    @DeleteMapping(ep_reviewById)
    public void removeReviewById(@PathVariable int id) {
        reviewDBService.removeReviewById(id);
    }

    @DeleteMapping(ep_reviewLike)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        reviewDBService.removeStatus(id, userId, 1);
    }

    @DeleteMapping(ep_reviewDislike)
    public void removeDislike(@PathVariable int id, @PathVariable int userId) {
        reviewDBService.removeStatus(id, userId, -1);
    }

}
