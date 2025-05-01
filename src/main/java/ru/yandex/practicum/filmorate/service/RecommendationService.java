package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.RecommendationDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationDbStorage recommendationDbStorage;

    public List<Film> getRecommendations(Long userId) {
        return recommendationDbStorage.findRecommendationsForUser(userId);
    }
}
