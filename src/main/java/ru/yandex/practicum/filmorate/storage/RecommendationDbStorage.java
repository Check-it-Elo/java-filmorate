package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.util.SlopeOneRecommender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecommendationDbStorage {

    private final JdbcTemplate jdbc;
    private static final int DEFAULT_LIMIT = 10;

    public List<Film> findRecommendationsForUser(Long userId) {
        return findRecommendationsForUser(userId, DEFAULT_LIMIT);
    }

    public List<Film> findRecommendationsForUser(Long userId, int limit) {

        // загружаем все лайки в Map<user, Map<film, 1.0>>
        Map<Long, Map<Long, Double>> data = loadRatings();

        if (!data.containsKey(userId) || data.get(userId).isEmpty()) {
            return Collections.emptyList();   // юзер ничего не лайкал
        }

        // slope one алгоритм из статьи
        SlopeOneRecommender slopeOne = new SlopeOneRecommender();
        slopeOne.buildDiffMatrix(data);

        Map<Long, Double> predictions = slopeOne.predict(data, data.get(userId));

        return predictions.entrySet()
                          .stream()
                          .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                          .limit(limit)
                          .map(e -> getFilmById(e.getKey()))
                          .collect(Collectors.toList());
    }

    private Map<Long, Map<Long, Double>> loadRatings() {
        String sql = "SELECT user_id, film_id FROM film_likes";
        Map<Long, Map<Long, Double>> res = new HashMap<>();
        jdbc.query(sql, rs -> {
            long uid = rs.getLong("user_id");
            long fid = rs.getLong("film_id");
            res.computeIfAbsent(uid, k -> new HashMap<>()).put(fid, 1.0);  // лайк -> рейтинг 1.0
        });
        return res;
    }

    private Film getFilmById(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        return jdbc.queryForObject(sql, this::mapRowToFilm, id);
    }

    private Film mapRowToFilm(ResultSet rs, int rn) throws SQLException {
        int mpaId = rs.getInt("mpa_id");
        String mpaName = jdbc.queryForObject("SELECT name FROM mpa WHERE id = ?", String.class, mpaId);

        // Получение жанров
        Long filmId = rs.getLong("id");
        String sqlGenres = "SELECT g.id, g.name FROM film_genres fg " +
                           "JOIN genres g ON fg.genre_id = g.id " +
                           "WHERE fg.film_id = ?";
        List<Genre> genreList = jdbc.query(sqlGenres,
                                           (genreRs, genreRow) -> new Genre(genreRs.getInt("id"),
                                                                            genreRs.getString("name")),
                                           filmId);

        return Film.builder()
                   .id(rs.getLong("id"))
                   .name(rs.getString("name"))
                   .description(rs.getString("description"))
                   .releaseDate(rs.getDate("release_date").toLocalDate())
                   .duration(rs.getLong("duration"))
                   .mpa(new MPA(mpaId, mpaName))
                   .genres(new HashSet<>(genreList))
                   .build();
    }
}
