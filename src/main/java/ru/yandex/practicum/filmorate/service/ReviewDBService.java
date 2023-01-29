package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Data
@Repository
@Slf4j
public class ReviewDBService {

    private final JdbcTemplate jdbcTemplate;
    public ReviewDBService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review addReview(Review review) {
        reviewValidation(review);
        jdbcTemplate.update("INSERT INTO film_reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)", review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(), review.getUseful());
        SqlRowSet reviewSet = jdbcTemplate.queryForRowSet("SELECT * FROM film_reviews WHERE user_id = ? AND film_id = ?", review.getUserId(), review.getFilmId());
        if(reviewSet.next()){
            review.setReviewId(reviewSet.getInt("id"));
            log.info("Film has been created, ID: {}", review.getReviewId());
        }
        addToFeedAddReview(review.getUserId(), review.getReviewId());
        return review;
    }

    public Review findReviewById(int id) {
        SqlRowSet reviewSet = jdbcTemplate.queryForRowSet("SELECT * FROM film_reviews WHERE id = ?", id);
        if(reviewSet.next()) {
            return getReview(reviewSet);
        } else {
            throw new NotFoundException("Review not found");
        }
    }

    public List<Review> getReviewListByFilmId(int filmId, int count) {
        List<Review> reviews;
        if (filmId == 0){
            String sql = "SELECT id FROM film_reviews ORDER BY useful DESC LIMIT ?";
            reviews = jdbcTemplate.query(sql, (resultSet, row) -> getReviewWithId(resultSet), count);
        } else {
            String sql = "SELECT id FROM film_reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            reviews = jdbcTemplate.query(sql, (resultSet, rowNumber) -> getReviewWithId(resultSet), filmId, count);
        }
        return reviews;
    }

    public Review updateReview(Review review) {
        if(jdbcTemplate.queryForRowSet("SELECT * FROM film_reviews WHERE id = ?", review.getReviewId()).next()) {
            jdbcTemplate.update("MERGE INTO film_reviews (id, content, is_positive) KEY (id) VALUES (?, ?, ?)",review.getReviewId(), review.getContent(), review.getIsPositive());
            addToFeedUpdateReview(getUserIdFromReviewId(review.getReviewId()), review.getReviewId());
        } else {
            throw new NotFoundException("Review not found");
        }

        return findReviewById(review.getReviewId());
    }

    public void updateStatus(int id, int userId, int value) {
        jdbcTemplate.update("MERGE INTO films_reviews_like (review_id, user_id, review_like_count) KEY (review_id, user_id) VALUES (?, ?, ?)", id, userId, value);
        usefulUpdate(id);
    }

    public void removeReviewById(int id) {
        if(jdbcTemplate.queryForRowSet("SELECT * FROM film_reviews WHERE id = ?", id).next()) {
            addToFeedDeleteReview(id);
            jdbcTemplate.update("DELETE FROM films_reviews_like WHERE review_id = ?", id);
            jdbcTemplate.update("DELETE FROM film_reviews WHERE id = ?", id);
        } else {
            throw new NotFoundException("Review not found");
        }
    }

    public void removeStatus(int id, int userId, int value) {
        jdbcTemplate.update("DELETE FROM films_reviews_like WHERE review_id = ? AND user_id = ? AND review_like_count = ?", id, userId, value);
        usefulUpdate(id);
    }


    // %%%%%%%%%% %%%%%%%%%% additional methods %%%%%%%%%% %%%%%%%%%%

    public Review getReviewWithId (ResultSet resultSet) throws SQLException {
        return findReviewById(resultSet.getInt("id"));
    }

    public Review getReview(SqlRowSet reviewSet) {
        Review review = new Review();
        review.setReviewId(reviewSet.getInt("id"));
        review.setContent(reviewSet.getString("content"));
        review.setIsPositive(reviewSet.getBoolean("is_positive"));
        review.setUserId(reviewSet.getInt("user_id"));
        review.setFilmId(reviewSet.getInt("film_id"));
        review.setUseful(reviewSet.getInt("useful"));
        return review;
    }

    void usefulUpdate(int review_id) {
        String sql = "UPDATE film_reviews SET useful = (SELECT SUM(review_like_count) FROM films_reviews_like WHERE review_id = ?) WHERE id = ?";
        jdbcTemplate.update(sql, review_id, review_id);
    }

    void reviewValidation(Review review) {
        userExistCheckUp(review.getUserId());
        filmExistCheckUp(review.getFilmId());
         if (review.getContent() == null || review.getIsPositive() == null || review.getUserId() == null || review.getFilmId() == null) {
            throw new ValidationException("Validation Exception");
        }
    }

    void userExistCheckUp(int userId){
        if(!jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", userId).next()) {
            throw new NotFoundException("Review not found");
        }
    }

    void filmExistCheckUp(int filmId){
        if(!jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ?", filmId).next()) {
            throw new NotFoundException("Review not found");
        }
    }

    private void addToFeedAddReview(Integer userId, int reviewId) {
        String query = "INSERT INTO events_history (user_id, event_type_id, operations_type_id, entity_id, date_time) " +
                "VALUES (?, 3, 1, ?, ?)";
        jdbcTemplate.update(query, userId, reviewId, Date.from(Instant.now()));
    }

    private void addToFeedUpdateReview(Integer userId, int reviewId) {
        String query = "INSERT INTO events_history (user_id, event_type_id, operations_type_id, entity_id, date_time) " +
                "VALUES (?, 3, 3, ?, ?)";
        jdbcTemplate.update(query, userId, reviewId, Date.from(Instant.now()));
    }

    private void addToFeedDeleteReview(int reviewId) {
        String query = "INSERT INTO events_history (user_id, event_type_id, operations_type_id, entity_id, date_time) " +
                "VALUES (?, 3, 2, ?, ?)";
        jdbcTemplate.update(query, getUserIdFromReviewId(reviewId), reviewId, Date.from(Instant.now()));
    }

    private int getUserIdFromReviewId(int reviewId) {
        SqlRowSet reviewSet = jdbcTemplate.queryForRowSet("SELECT user_id FROM film_reviews WHERE id = ?", reviewId);
        reviewSet.next();
        return reviewSet.getInt("user_id");
    }

}
