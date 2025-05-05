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

        List<Long> filmIds = predictions.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }

        return getFilmsByIds(filmIds);
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

    private List<Film> getFilmsByIds(List<Long> ids) {
        String inClause = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM films WHERE id IN (%s)", inClause);

        Map<Long, Film> filmsMap = jdbc.query(sql,
                (ResultSet rs, int rowNum) -> mapRowToFilm(rs),
                ids.toArray()
        ).stream().collect(Collectors.toMap(Film::getId, film -> film));

        // Загружаем жанры для всех фильмов одним запросом
        loadGenresForFilms(filmsMap.keySet()).forEach((filmId, genres) -> {
            if (filmsMap.containsKey(filmId)) {
                filmsMap.get(filmId).setGenres(new HashSet<>(genres));
            }
        });

        // Загружаем MPA для всех фильмов одним запросом
        loadMpaForFilms(filmsMap.keySet()).forEach((filmId, mpa) -> {
            if (filmsMap.containsKey(filmId)) {
                filmsMap.get(filmId).setMpa(mpa);
            }
        });

        // Возвращаем фильмы в том же порядке, что и запрошенные ID
        return ids.stream()
                .map(filmsMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<Long, List<Genre>> loadGenresForFilms(Collection<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inClause = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format(
                "SELECT fg.film_id, g.id, g.name FROM film_genres fg " +
                        "JOIN genres g ON fg.genre_id = g.id " +
                        "WHERE fg.film_id IN (%s)", inClause);

        return jdbc.query(sql, rs -> {
            Map<Long, List<Genre>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));
                result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
            }
            return result;
        }, filmIds.toArray());
    }

    private Map<Long, MPA> loadMpaForFilms(Collection<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inClause = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format(
                "SELECT f.id as film_id, m.id, m.name FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.id " +
                        "WHERE f.id IN (%s)", inClause);

        return jdbc.query(sql, rs -> {
            Map<Long, MPA> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                MPA mpa = new MPA(rs.getInt("id"), rs.getString("name"));
                result.put(filmId, mpa);
            }
            return result;
        }, filmIds.toArray());
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .build();
    }
}