package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review add(@Valid @RequestBody Review addReview) {
        return reviewService.addReview(addReview);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getAllByFilmId(@RequestParam(required = false) Long filmId,
                                       @RequestParam(defaultValue = "10")
                                       @Positive(message = "count должен быть положительным") Long count) {
        return reviewService.getAllByFilmId(filmId, count);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addUserLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addUserDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislikeReview(id, userId);
    }

}