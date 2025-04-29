package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.EnterExeption;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private static final LocalDate MIN_DATE_OF_RELEASE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreService genreService;
    private final DirectorService directorService;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            GenreService genreService,
            DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    public Collection<Film> getAllFilms() {
        log.info("Получение списка всех фильмов");
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        log.info("Попытка добавить фильм: {}", film);
        validateFilm(film);

        // Проверка существования MPA через хранилище
        filmStorage.validateMpaExists(film.getMpa().getId());

        // Проверка жанров через GenreService
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreService.validateGenreExists(genre.getId());
            }
        }

        // Проверка режиссёров
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                directorService.validateDirectorExists(director.getId());
            }
        }

        try {
            Film createdFilm = filmStorage.addFilm(film);
            log.info("Фильм добавлен: {}", createdFilm);
            return createdFilm;
        } catch (Exception e) {
            log.error("Ошибка при добавлении фильма", e);
            throw e;
        }
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        validateFilm(film);

        // Проверка режиссёров
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                directorService.validateDirectorExists(director.getId());
            }
        }

        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Обновлен фильм: {}", updatedFilm);
        return updatedFilm;
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.error("Фильм с ID {} не найден", id);
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        getUserById(userId);

        filmStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        getUserById(userId);

        if (!film.getLikes().contains(userId)) {
            log.warn("Пользователь {} не ставил лайк фильму {}", userId, filmId);
        }

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public List<Film> getMostPopularFilms(int count, Integer genreId, Integer year) {
        if (count <= 0) {
            log.error("Запрошено некорректное количество фильмов: {}", count);
            throw new ValidationException("Количество фильмов должно быть положительным числом");
        }

        List<Film> popularFilms = new ArrayList<>();
        if (genreId == null && year == null) {
            popularFilms = filmStorage.getMostPopularFilms(count);
        } else {
            popularFilms = filmStorage.getMostPopularFilms(count, genreId, year);
        }

        log.info("Возвращено {} самых популярных фильмов", popularFilms.size());
        return popularFilms;
    }

    private User getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        return user;
    }

    public Collection<Film> getFilmsByGenre(int genreId) {
        genreService.getGenreById(genreId);
        return filmStorage.getFilmsByGenre(genreId);
    }

    public void addDirectors(Long filmId, List<Director> directors) {
        Film film = getFilmById(filmId);

        for (Director director : directors) {
            directorService.validateDirectorExists(director.getId());
        }

        film.getDirectors().addAll(directors);
        filmStorage.updateFilm(film);
        log.info("Режиссёры {} добавлены к фильму {}", directors, filmId);
    }

    public void removeDirectors(Long filmId, List<Director> directors) {
        Film film = getFilmById(filmId);

        for (Director director : directors) {
            film.getDirectors().removeIf(existing -> Integer.valueOf(existing.getId()).equals(director.getId()));
        }

        filmStorage.updateFilm(film);
        log.info("Режиссёры {} удалены из фильма {}", directors, filmId);
    }

    public List<Film> getFilmsByDirectorSorted(int directorId, String sortBy) {
        directorService.validateDirectorExists(directorId);
        return filmStorage.getFilmsByDirectorSorted(directorId, sortBy);
    }

    public void deleteFilm(Long filmId) {
        if (filmId <= 0) {
            throw new ValidationException("Некорректный ID фильма: " + filmId);
        }

        getFilmById(filmId);
        filmStorage.deleteFilm(filmId);
        log.info("Фильм с ID {} успешно удалён", filmId);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации: пустое название фильма");
            throw new EnterExeption("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("Ошибка валидации: описание длиннее {} символов", MAX_DESCRIPTION_LENGTH);
            throw new EnterExeption("Максимальная длина описания — " + MAX_DESCRIPTION_LENGTH + " символов");
        }
        if (film.getReleaseDate().isBefore(MIN_DATE_OF_RELEASE)) {
            log.error("Ошибка валидации: дата релиза раньше {}", MIN_DATE_OF_RELEASE);
            throw new EnterExeption("Дата релиза не может быть раньше " + MIN_DATE_OF_RELEASE);
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка валидации: продолжительность фильма должна быть положительной");
            throw new EnterExeption("Продолжительность фильма должна быть положительным числом");
        }
        if (film.getMpa() == null || film.getMpa().getId() <= 0) {
            log.error("Ошибка валидации: отсутствует или некорректен MPA");
            throw new EnterExeption("MPA должен быть указан и иметь корректный id");
        }
    }

}