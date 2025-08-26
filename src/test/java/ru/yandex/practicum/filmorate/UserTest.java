package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserController userController;

    private User user;

    @BeforeEach
    void createUser() {
        user = createTestUser();
    }

    @Test
    void shouldCreateUser() {
        User createdUser = userController.createUser(user);

        Assertions.assertNotNull(createdUser.getId());
        Assertions.assertEquals(user.getName(), createdUser.getName());
    }

    @Test
    void shouldUseLoginWhenNameIsEmpty() {
        user.setName("");
        User createdUser = userController.createUser(user);

        Assertions.assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    void shouldUseLoginWhenNameIsNull() {
        user.setName(null);
        User createdUser = userController.createUser(user);

        Assertions.assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        user.setEmail(null);

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        user.setEmail("        ");

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        user.setEmail("email-without-at");

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginIsNull() {
        user.setLogin(null);

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginIsBlank() {
        user.setLogin("      ");

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        user.setLogin("login with spaces");

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        user.setBirthday(LocalDate.now().plusDays(2));

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldAcceptBirthdayWhenBirthdayIsToday() {
        user.setBirthday(LocalDate.now());

        Assertions.assertEquals(user.getBirthday(), LocalDate.now());
        Assertions.assertDoesNotThrow(() -> userController.createUser(user));
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