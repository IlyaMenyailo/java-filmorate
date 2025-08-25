package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void shouldCreateUser() {
        User user = createTestUser();
        User createdUser = userController.createUser(user);

        Assertions.assertNotNull(createdUser.getId());
        Assertions.assertEquals(user.getName(), createdUser.getName());
    }

    @Test
    void shouldUseLoginWhenNameIsEmpty() {
        User user = createTestUser();
        user.setName("");
        User createdUser = userController.createUser(user);

        Assertions.assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        User user = createTestUser();
        user.setEmail("email-without-at");

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = createTestUser();
        user.setLogin("login with spaces");

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        User user = createTestUser();
        user.setBirthday(LocalDate.now().plusDays(2));

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("testUserLogin");
        user.setName("testUserName");
        user.setBirthday(LocalDate.of(1996, 7, 31));
        return user;
    }
}