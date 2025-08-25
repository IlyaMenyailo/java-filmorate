package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code GET /users} - Получение пользователей<br/>
 * {@code POST /users} - Создание пользователя<br/>
 * {@code PUT /users} - Редактирование пользователя<br/>
 */

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long nextUserId = 1;

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получение всех пользователей. Текущее количество: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(nextUserId++);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Попытка обновить несуществующего пользователя с ID: {}", user.getId());
            throw new ValidationException("Пользователь с ID: " + user.getId() + " не найден");
        }
        validateUser(user);
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя установлено как наименование логина: {}", user.getLogin());
        }
    }
}