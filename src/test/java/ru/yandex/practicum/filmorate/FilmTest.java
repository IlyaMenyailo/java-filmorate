package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;

class FilmTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = createTestFilm();
        Film createdFilm = filmController.createFilm(film);

        Assertions.assertNotNull(createdFilm.getId());
        Assertions.assertEquals(film.getName(), createdFilm.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        Film film = createTestFilm();
        film.setName("");

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        Film film = createTestFilm();
        film.setDescription("a".repeat(201));

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {
        Film film = createTestFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        Film film = createTestFilm();
        film.setDuration(-1);

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
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