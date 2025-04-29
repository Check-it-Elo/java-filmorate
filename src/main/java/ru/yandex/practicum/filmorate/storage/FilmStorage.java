package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    Film getFilmById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Film> getFilmsByGenre(int genreId);

    void validateMpaExists(int mpaId);

    List<Film> getMostPopularFilms(int count);

    List<Film> getMostPopularFilms(int count, Integer genreId, Integer year);

    List<Film> getFilmsByDirectorSorted(int directorId, String sortBy);

    List<Film> getCommonFilms(Long userId, Long friendId);
}