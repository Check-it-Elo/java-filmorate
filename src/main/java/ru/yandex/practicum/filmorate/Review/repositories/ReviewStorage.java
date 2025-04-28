package ru.yandex.practicum.filmorate.Review.repositories;

import ru.yandex.practicum.filmorate.Review.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review findById(Long id);

    Review addReview(Review review);

    void deleteReview(Long id);

    Review updateReview(Review review);

    void addLikeReview(Long id, Long userId);

    void addDislikeReview(Long id, Long userId);

    void removeLikeReview(Long id, Long userId);

    void removeDislikeReview(Long id, Long userId);

    List<Review> findAllReviewsByFilmIdAndCount(Long id, Long count);

    List<Review> findAllReviewsByCount(Long count);
}
