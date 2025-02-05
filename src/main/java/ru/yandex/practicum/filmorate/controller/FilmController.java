package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.EnterExeption;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_DATE_OF_RELEASE = LocalDate.of(1895,12,28);
    private static final long MAX_DESCRIPTION_LENGTH = 200;
    private long idCounter = 1;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получены фильмы: {}", films.values());
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {

        if (film.getName() == null || film.getName().trim().isEmpty()) {
            log.error("ОШИБКА: Пустое название фильма");
            throw new EnterExeption("Название не может быть пустым");
        }

        if (film.getDescription().trim().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("ОШИБКА: Длинна описания более 200 символов");
            throw new EnterExeption("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate().isBefore(MIN_DATE_OF_RELEASE)) {
            log.error("ОШИБКА: Дата меньше допустимой");
            throw new EnterExeption("Самая ранняя возможная дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 0) {
            log.error("ОШИБКА: Отрицательная продолжительность");
            throw new EnterExeption("Продолжительность фильма должна быть положительным числом");
        }

        film.setId(idCounter++);
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен: {}", film);

        return film;
    }

    @PutMapping
    public Film udateFilm(@RequestBody Film newFilm) {

        if (!films.containsKey(newFilm.getId())) {
            log.error("ОШИБКА: Фильм с таким ID не найден");
            throw new EnterExeption("Фильм не найден");
        }

        if (newFilm.getName() == null || newFilm.getName().trim().isEmpty()) {
            log.error("ОШИБКА: Пустое название фильма");
            throw new EnterExeption("Название не может быть пустым");
        }

        if (newFilm.getDescription().trim().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("ОШИБКА: Длинна описания более 200 символов");
            throw new EnterExeption("Максимальная длина описания — 200 символов");
        }

        if (newFilm.getReleaseDate().isBefore(MIN_DATE_OF_RELEASE)) {
            log.error("ОШИБКА: Дата меньше допустимой");
            throw new EnterExeption("Самая ранняя возможная дата релиза — не раньше 28 декабря 1895 года");
        }

        if (newFilm.getDuration() < 0) {
            log.error("ОШИБКА: Отрицательная продолжительность");
            throw new EnterExeption("Продолжительность фильма должна быть положительным числом");
        }


        Film oldFilm = films.get(newFilm.getId());

        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
            log.info("У фильма {} обновлено название: {}", newFilm, newFilm.getName());
        }

        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
            log.info("У фильма {} обновлено описание: {}", newFilm, newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.info("У фильма {} обновлена дата релиза: {}", newFilm, newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() > 0) {
            newFilm.setDuration(newFilm.getDuration());
            log.info("У фильма {} имя установлена продолжительность: {}", newFilm, newFilm.getDuration());
        }

        return newFilm;

    }

}
