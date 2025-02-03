package ru.yandex.practicum.filmorate.testControllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeptions.EnterExeption;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
    }

    @Test
    public void testAddFilmSuccessfully() {
        Film film = new Film();
        film.setName("Film 1");
        film.setDescription("Description of film 1");
        film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        film.setDuration(120);

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(film.getName(), addedFilm.getName());
        assertEquals(film.getDescription(), addedFilm.getDescription());
        assertEquals(film.getReleaseDate(), addedFilm.getReleaseDate());
        assertEquals(film.getDuration(), addedFilm.getDuration());
    }

    @Test
    public void testAddFilmWithEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        film.setDuration(120);

        Exception exception = assertThrows(EnterExeption.class, () -> filmController.addFilm(film));
        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    public void testAddFilmWithTooLongDescription() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(201));  // 201 character long description
        film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        film.setDuration(120);

        Exception exception = assertThrows(EnterExeption.class, () -> filmController.addFilm(film));
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    public void testAddFilmWithInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(1800, Month.JANUARY, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        film.setDuration(120);

        Exception exception = assertThrows(EnterExeption.class, () -> filmController.addFilm(film));
        assertEquals("Самая ранняя возможная дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    public void testAddFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        film.setDuration(-1);

        Exception exception = assertThrows(EnterExeption.class, () -> filmController.addFilm(film));
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    public void testUpdateFilmSuccessfully() {
        Film originalFilm = new Film();
        originalFilm.setName("Original Film");
        originalFilm.setDescription("Original description");
        originalFilm.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        originalFilm.setDuration(120);

        Film addedFilm = filmController.addFilm(originalFilm);

        Film updatedFilm = new Film();
        updatedFilm.setId(addedFilm.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated description");
        updatedFilm.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        updatedFilm.setDuration(150);

        Film result = filmController.udateFilm(updatedFilm);

        assertEquals(updatedFilm.getName(), result.getName());
        assertEquals(updatedFilm.getDescription(), result.getDescription());
        assertEquals(updatedFilm.getReleaseDate(), result.getReleaseDate());
        assertEquals(updatedFilm.getDuration(), result.getDuration());
    }

    @Test
    public void testUpdateFilmNotFound() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Non-existent Film");

        Exception exception = assertThrows(EnterExeption.class, () -> filmController.udateFilm(film));
        assertEquals("Фильм не найден", exception.getMessage());
    }

    @Test
    public void testUpdateFilmWithEmptyName() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant());
        film.setDuration(120);

        Film addedFilm = filmController.addFilm(film);
        Film updatedFilm = new Film();
        updatedFilm.setId(addedFilm.getId());
        updatedFilm.setName("");

        Exception exception = assertThrows(EnterExeption.class, () -> filmController.udateFilm(updatedFilm));
        assertEquals("Название не может быть пустым", exception.getMessage());
    }
}
