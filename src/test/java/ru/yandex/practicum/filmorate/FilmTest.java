package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;

@SpringBootTest
class FilmTest {
    @Autowired
    private FilmController filmController;

    private Film film;

    @BeforeEach
    void createFilm() {
        film = createTestFilm();
    }

    @Test
    void shouldCreateValidFilm() {
        Film createdFilm = filmController.createFilm(film);

        Assertions.assertNotNull(createdFilm.getId());
        Assertions.assertEquals(film.getName(), createdFilm.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        film.setName(null);

        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        film.setName("");

        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        film.setDescription("a".repeat(201));

        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldAcceptDescriptionWhenDescriptionIs200() {
        film.setDescription("a".repeat(200));

        Assertions.assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldAcceptMinimumReleaseDate() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Assertions.assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        film.setDuration(-1);

        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZero() {
        film.setDuration(0);

        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldAcceptDurationWhenItIsOne() {
        film.setDuration(1);

        Assertions.assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("TestDescription");
        film.setReleaseDate(LocalDate.of(2007, 7, 7));
        film.setDuration(120);
        return film;
    }
}