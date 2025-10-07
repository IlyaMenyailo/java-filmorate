package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final MpaStorage mpaStorage;

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Получение всех рейтингов MPA");
        return mpaStorage.getAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        log.info("Получение рейтинга MPA с ID: {}", id);
        return mpaStorage.getById(id)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exception.NotFoundException("Рейтинг MPA с ID " + id + " не найден"));
    }
}