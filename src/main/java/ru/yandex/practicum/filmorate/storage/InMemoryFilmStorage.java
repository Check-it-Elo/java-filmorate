package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.EnterExeption;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Long, Film> films = new HashMap<>();
    private long idCounter = 1;
    private static final LocalDate MIN_DATE_OF_RELEASE = LocalDate.of(1895, 12, 28);
    private static final long MAX_DESCRIPTION_LENGTH = 200;

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Получение списка всех фильмов, всего: {}", films.size());
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            log.error("Ошибка: фильм с ID {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с ID " + newFilm.getId() + " не найден");
        }
        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм обновлён: {}", newFilm);
        return newFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    // Валидация фильма
    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            log.error("Ошибка валидации: пустое название фильма");
            throw new EnterExeption("Название не может быть пустым");
        }
        if (film.getDescription().trim().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("Ошибка валидации: описание длиннее {} символов", MAX_DESCRIPTION_LENGTH);
            throw new EnterExeption("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(MIN_DATE_OF_RELEASE)) {
            log.error("Ошибка валидации: дата релиза раньше {}", MIN_DATE_OF_RELEASE);
            throw new EnterExeption("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка валидации: продолжительность фильма должна быть положительной, а не {}", film.getDuration());
            throw new EnterExeption("Продолжительность фильма должна быть положительным числом");
        }
    }

}