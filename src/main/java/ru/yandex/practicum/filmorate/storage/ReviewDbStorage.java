package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    private static final String GET_BY_ID = """
            SELECT r.*, (SELECT sum(type) FROM reviews_likes WHERE review_id = r.review_id GROUP BY review_id) as useful
            FROM reviews as r where review_id = ?""";

    private static final String ADD_FILM_REVIEW = """
            INSERT INTO reviews(user_id, film_id, content, is_positive) VALUES (?, ?, ?, ?)""";

    private static final String DELETE_REVIEW = """
            DELETE FROM reviews
            WHERE review_id = ?""";

    private static final String UPDATE_REVIEW = """
            UPDATE reviews SET content = ?, is_positive = ?
            WHERE review_id = ?""";

    private static final String FIND_ALL_REVIEWS = """
            SELECT r.*, COALESCE(SUM(reviews_likes.type), 0) as useful
            FROM reviews as r
            LEFT JOIN reviews_likes ON r.review_id = reviews_likes.review_id
            GROUP BY r.review_id
            ORDER BY useful
            DESC LIMIT ?""";

    private static final String FIND_ALL_FILM_REVIEWS_BY_COUNT = """
            SELECT r.*, COALESCE(SUM(reviews_likes.type), 0) as useful
            FROM reviews as r
            LEFT JOIN reviews_likes ON r.review_id = reviews_likes.review_id
            WHERE r.film_id = ?
            GROUP BY r.review_id
            ORDER BY useful DESC
            LIMIT ?""";

    private static final String ADD_LIKE = """
            INSERT INTO reviews_likes(review_id, user_id, type) VALUES (?, ?, ?)""";

    private static final String UPDATE_LIKE = """
            UPDATE reviews_likes SET type = ? WHERE review_id = ? AND user_id = ?""";

    private static final String REMOVE_LIKE = """
            DELETE FROM reviews_likes
            WHERE review_id = ? AND user_id = ?""";

    private static final String GET_LIKE = """
            SELECT * FROM reviews_likes WHERE review_id = ? AND user_id = ?""";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review findById(Long id) {
        Optional<Review> review = findOne(GET_BY_ID, id);
        return review.orElseThrow(() -> new NotFoundException("Ревью с Id " + id + " не найдено"));
    }

    @Override
    public Review addReview(Review review) {
        Long id = insert(ADD_FILM_REVIEW,
                         review.getUserId(),
                         review.getFilmId(),
                         review.getContent(),
                         review.getIsPositive());

        return findById(id);
    }

    @Override
    public void deleteReview(Long id) {
        update(DELETE_REVIEW, id);
    }

    @Override
    public Review updateReview(Review review) {
        update(UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findById(review.getReviewId());
    }

    @Override
    public void addLikeReview(Long id, Long userId) {
        insertOrUpdateReviewLike(id, userId, 1);
    }

    @Override
    public void addDislikeReview(Long id, Long userId) {
        insertOrUpdateReviewLike(id, userId, -1);
    }

    @Override
    public void removeLikeReview(Long id, Long userId) {
        update(REMOVE_LIKE, id, userId);
    }

    private void insertOrUpdateReviewLike(Long reviewId, Long userId, Integer type) {
        var like = getReviewLike(reviewId, userId);
        if (!like.isEmpty()) {
            update(UPDATE_LIKE, type, reviewId, userId);
        } else {
            insert(ADD_LIKE, reviewId, userId, type);
        }
    }

    @Override
    public void removeDislikeReview(Long id, Long userId) {
        update(REMOVE_LIKE, id, userId);
    }

    private Map<Long, Set<Long>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Set<Long>> data = new LinkedHashMap<>();
        while (rs.next()) {
            Long userId = rs.getLong("user_id");
            data.putIfAbsent(userId, new HashSet<>());
            data.get(userId).add(rs.getLong("review_id"));
        }
        return data;
    }

    private Map<Long, Set<Long>> getReviewLike(Long id, Long reviewId) {
        return jdbc.query(GET_LIKE, this::extractData, id, reviewId);
    }

    @Override
    public List<Review> findAllReviewsByFilmIdAndCount(Long id, Long count) {
        return findMany(FIND_ALL_FILM_REVIEWS_BY_COUNT, id, count);
    }

    @Override
    public List<Review> findAllReviewsByCount(Long count) {
        return findMany(FIND_ALL_REVIEWS, count);
    }

}
