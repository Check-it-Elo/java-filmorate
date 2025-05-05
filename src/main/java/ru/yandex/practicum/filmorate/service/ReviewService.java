package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    Review addReview(Review review);

    Review updateReview(Review review);

    List<Review> getAllByFilmId(Long id, Long count);

    Review getById(Long id);

    void removeLikeReview(Long id, Long userId);

    void addLikeReview(Long id, Long userId);

    void addDislikeReview(Long id, Long userId);

    void removeDislikeReview(Long id, Long userId);

    void deleteReview(Long id);

}