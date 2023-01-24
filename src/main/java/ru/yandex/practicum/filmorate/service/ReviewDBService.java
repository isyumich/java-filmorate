package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Data
@Repository
public class ReviewDBService {

    private static final Logger log = LoggerFactory.getLogger(ReviewDBService.class);

    private final JdbcTemplate jdbcTemplate;
    public ReviewDBService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review addReview(Review review) {
        review.setUseful(0);
        jdbcTemplate.update("INSERT INTO film_reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)", review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(), review.getUseful());
        SqlRowSet reviewSet = jdbcTemplate.queryForRowSet("SELECT * FROM film_reviews WHERE user_id = ? AND film_id = ?", review.getUserId(), review.getFilmId());
        if(reviewSet.next()){
            review.setReviewId(reviewSet.getInt("id"));
            log.info("Film has been created, ID: {}", review.getReviewId());
        }
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
        SqlRowSet reviewSet = jdbcTemplate.queryForRowSet("SELECT * FROM film_reviews WHERE id = ?", review.getReviewId());
        if(reviewSet.next()) {
            String sql = "MERGE INTO film_reviews (id, content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, review.getReviewId(), review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(), review.getUseful());
        } else {
            throw new NotFoundException("Review not found");
        }
        return review;
    }

    public void updateStatus(int id, int userId, int value) {
        jdbcTemplate.update("MERGE INTO films_reviews_like (review_id, user_id, review_like_count) KEY (review_id, user_id) VALUES (?, ?, ?)", id, userId, value);
        usefulUpdate(id);
    }

    public void removeReviewById(int id) {
        SqlRowSet reviewSet = jdbcTemplate.queryForRowSet("SELECT * FROM film_reviews WHERE id = ?", id);
        if(reviewSet.next()) {
            System.out.println("FLAG-- " + id);
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
        review.setPositive(reviewSet.getBoolean("is_positive"));
        review.setUserId(reviewSet.getInt("user_id"));
        review.setFilmId(reviewSet.getInt("film_id"));
        review.setUseful(reviewSet.getInt("useful"));
        return review;
    }

    void usefulUpdate(int review_id) {
        String sql = "UPDATE film_reviews SET useful = (SELECT SUM(review_like_count) FROM films_reviews_like WHERE review_id = ?) WHERE id = ?";
        jdbcTemplate.update(sql, review_id, review_id);
    }


}
