package ru.yandex.practicum.filmorate.Review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Review.model.Review;
import ru.yandex.practicum.filmorate.Review.repositories.ReviewStorage;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewRepository;
    private final FilmDbStorage filmRepository;
    private final UserDbStorage userRepository;

    @Override
    public Review addReview(Review review) {
        checkUserAndFilmExists(review.getUserId(), review.getFilmId());
        return reviewRepository.addReview(review);
    }


    @Override
    public Review updateReview(Review review) {
        checkUserAndFilmExists(review.getUserId(), review.getFilmId());
        return  reviewRepository.updateReview(review);

    }

    @Override
    public List<Review> getAllByFilmId(Long id, Long count) {
        if (id == null) {
            return reviewRepository.findAllReviewsByCount(count);
        }
        return reviewRepository.findAllReviewsByFilmIdAndCount(id, count);
    }

    @Override
    public Review getById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public void removeLikeReview(Long id, Long userId) {
        reviewRepository.removeLikeReview(id, userId);
    }

    @Override
    public void addLikeReview(Long id, Long userId) {
        reviewRepository.addLikeReview(id, userId);
    }

    @Override
    public void addDislikeReview(Long id, Long userId) {
        reviewRepository.addDislikeReview(id, userId);
    }

    @Override
    public void removeDislikeReview(Long id, Long userId) {
        reviewRepository.removeDislikeReview(id, userId);
    }

    @Override
    public void deleteReview(Long id) {
        Long userId = reviewRepository.findById(id).getUserId();
        reviewRepository.deleteReview(id);
    }

    private void checkUserAndFilmExists(Long userId, Long filmId) {
        User user = userRepository.getUserById(userId);
        if (user == null ){
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        Film film = filmRepository.getFilmById(filmId);
        if (film == null){
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
    }
}
