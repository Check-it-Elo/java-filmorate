package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmController(FilmService filmService, JdbcTemplate jdbcTemplate) {
        this.filmService = filmService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрос: получение всех фильмов");
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        // Проверка, что MPA не null
        if (film.getMpa() == null) {
            throw new ValidationException("MPA должен быть указан");
        }

        // Проверка жанров
        if (film.getGenres() != null) {
            Set<Integer> existingGenreIds = new HashSet<>(jdbcTemplate.queryForList(
                    "SELECT id FROM genres", Integer.class));

            for (Genre genre : film.getGenres()) {
                if (!existingGenreIds.contains(genre.getId())) {
                    throw new NotFoundException("Жанр с ID " + genre.getId() + " не найден");
                }
            }
        }

        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Запрос: обновление фильма {}", film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostPopularFilms(count);
    }

    @GetMapping("/films/genre/{genreId}")
    public Collection<Film> getFilmsByGenre(@PathVariable int genreId) {
        return filmService.getFilmsByGenre(genreId);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрос: получение фильма с ID {}", id);
        return filmService.getFilmById(id);
    }

}