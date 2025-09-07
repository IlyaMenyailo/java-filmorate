package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        Optional<Film> optionalFilm = filmStorage.getById(id);

        if (optionalFilm.isPresent()) {
            return optionalFilm.get();
        } else {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        validateFilm(film);
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);

        userService.getUserById(userId);

        if (!likes.containsKey(filmId)) {
            likes.put(filmId, new HashSet<>());
        }

        Set<Long> filmLikes = likes.get(filmId);

        filmLikes.add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        getFilmById(filmId);
        if (likes.containsKey(filmId)) {
            Set<Long> filmLikes = likes.get(filmId);
            if (!filmLikes.contains(userId)) {
                throw new NotFoundException("Лайк от пользователя " + userId + " для фильма " + filmId + " не найден");
            }
            filmLikes.remove(userId);
        } else {
            throw new NotFoundException("Лайки для фильма " + filmId + " не найдены");
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(
                        getLikesCount(f2.getId()),
                        getLikesCount(f1.getId())
                ))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int getLikesCount(Long filmId) {
        return likes.getOrDefault(filmId, Collections.emptySet()).size();
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Дата релиза {} раньше минимально допустимой {}", film.getReleaseDate(), MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза не может быть раньше " + MIN_RELEASE_DATE);
        }
    }
}