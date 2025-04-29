package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Long, Film> films = new HashMap<>();
    private long idCounter = 1;

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Получение списка всех фильмов, всего: {}", films.size());
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
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
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм обновлён: {}", newFilm);
        return newFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    // ЗАГЛУШКИ ДЛЯ МЕТОДОВ //

    @Override
    public void addLike(Long filmId, Long userId) {
        // Заглушка для in-memory реализации
        log.info("Добавлен лайк фильму {} от пользователя {}", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        // Заглушка для in-memory реализации
        log.info("Удалён лайк фильму {} от пользователя {}", filmId, userId);
    }

    @Override
    public Collection<Film> getFilmsByGenre(int genreId) {
        // Заглушка для in-memory реализации
        log.info("Получение фильмов по жанру {}", genreId);
        return getAllFilms();
    }

    @Override
    public void validateMpaExists(int mpaId) {
        // Заглушка для in-memory реализации
        log.info("Проверка существования MPA с id {}", mpaId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        // Заглушка для in-memory реализации
        log.info("Заглушка: Получение самых популярных фильмов, ограничение на {}", count);
        return new ArrayList<>(films.values()).subList(0, Math.min(count, films.size()));
    }

    @Override
    public List<Film> getMostPopularFilms(int count, Integer genreId, Integer year) {
        log.info("Заглушка: Получение популярных фильмов с фильтрацией по жанру {} и году {}", genreId, year);
        return getMostPopularFilms(count);
    }


    @Override
    public List<Film> getFilmsByDirectorSorted(int directorId, String sortBy) {
        // Заглушка для in-memory реализации
        log.info("Заглушка: Получение фильмов по директору с id {} и сортировкой по {}", directorId, sortBy);
        return new ArrayList<>();
    }

    @Override
    public void deleteFilm(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        films.remove(filmId);
    }
}