package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
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

    @GetMapping("/genre/{genreId}")
    public Collection<Film> getFilmsByGenre(@PathVariable int genreId) {
        return filmService.getFilmsByGenre(genreId);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрос: получение фильма с ID {}", id);
        return filmService.getFilmById(id);
    }

}