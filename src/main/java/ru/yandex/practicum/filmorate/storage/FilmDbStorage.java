package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);

        Map<Long, Set<Long>> filmLikes = getAllLikes();

        for (Film film : films) {
            Set<Long> likes = filmLikes.getOrDefault(film.getId(), new HashSet<>());
            film.setLikes(likes);
        }

        return films;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);

        // Сохраняем жанры
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                        filmId, genre.getId()
                );
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        // Сначала удаляем старые жанры
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        // Затем вставляем актуальные жанры
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId()
                );
            }
        }

        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (films.isEmpty()) {
            return null;
        }
        return films.get(0);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Long filmId = rs.getLong("id");

        // Получение MPA
        int mpaId = rs.getInt("mpa_id");
        String mpaName = jdbcTemplate.queryForObject(
                "SELECT name FROM mpa WHERE id = ?",
                String.class,
                mpaId
        );
        MPA mpa = new MPA(mpaId, mpaName);

        // Получение жанров
        String sqlGenres = "SELECT g.id, g.name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?";
        List<Genre> genreList = jdbcTemplate.query(sqlGenres, (genreRs, genreRow) ->
                new Genre(genreRs.getInt("id"), genreRs.getString("name")), filmId);

        // Получение лайков
        String sqlLikes = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(sqlLikes,
                (likeRs, likeRow) -> likeRs.getLong("user_id"),
                filmId);

        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpa(mpa)
                .genres(new HashSet<>(genreList))
                .likes(new HashSet<>(likes))
                .build();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        String sql = "SELECT f.* FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }


    @Override
    public Collection<Film> getFilmsByGenre(int genreId) {
        // Проверка существования жанра
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM genres WHERE id = ?",
                    Integer.class,
                    genreId
            );

            if (count == null || count == 0) {
                throw new NotFoundException("Жанр с ID " + genreId + " не найден");
            }

            String sql = "SELECT f.* FROM films f " +
                    "JOIN film_genres fg ON f.id = fg.film_id " +
                    "WHERE fg.genre_id = ? " +
                    "ORDER BY f.id";

            return jdbcTemplate.query(sql, this::mapRowToFilm, genreId);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка базы данных", e);
        }
    }

    private Map<Long, Set<Long>> getAllLikes() {
        String sql = "SELECT film_id, user_id FROM film_likes";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        Map<Long, Set<Long>> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Long filmId = ((Number) row.get("film_id")).longValue();
            Long userId = ((Number) row.get("user_id")).longValue();
            result.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        }
        return result;
    }

    @Override
    public void validateMpaExists(int mpaId) {
        Integer mpaCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM mpa WHERE id = ?",
                Integer.class,
                mpaId
        );
        if (mpaCount == null || mpaCount == 0) {
            throw new NotFoundException("MPA с ID " + mpaId + " не найден");
        }
    }

}