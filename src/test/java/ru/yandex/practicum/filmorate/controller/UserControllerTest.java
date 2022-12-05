package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.user.IsAlreadyUserExistException;
import ru.yandex.practicum.filmorate.exception.user.UserBirthdayValidationException;
import ru.yandex.practicum.filmorate.exception.user.UserEmailValidationException;
import ru.yandex.practicum.filmorate.exception.user.UserLoginValidationException;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UserControllerTest {
    UserController userController;
    @BeforeEach
    public void BeforeEach() {
        userController = new UserController();
    }

    @Test
    void validateLoginTest() {
        final User userWithEmptyLogin = User.builder()
                .id(1)
                .email("andrew@mail.ru")
                .login("")
                .name("andrew")
                .birthday(LocalDate.of(1994, 4, 3))
                .build();
        assertThrows(UserLoginValidationException.class, () -> userController.addNewUser(userWithEmptyLogin), "without login exception");
    }

    @Test
    void validateBirthdayTest() {
        final User userWithTooBigBirthday = User.builder()
                .id(1)
                .email("andrew@mail.ru")
                .login("andrew")
                .name("andrew")
                .birthday(LocalDate.of(2094, 4, 3))
                .build();
        assertThrows(UserBirthdayValidationException.class, () -> userController.addNewUser(userWithTooBigBirthday), "without birthday exception");
    }

    @Test
    void validateEmailTest() {
        final User userWithBadEmail = User.builder()
                .id(1)
                .email("andrew.mail.ru")
                .login("andrew")
                .name("andrew")
                .birthday(LocalDate.of(1994, 4, 3))
                .build();
        assertThrows(UserEmailValidationException.class, () -> userController.addNewUser(userWithBadEmail), "without email exception");
    }

    @Test
    void validateAlreadyExistUserTest() {
        final User user = User.builder()
                .id(1)
                .email("andrew@mail.ru")
                .login("andrew")
                .name("andrew")
                .birthday(LocalDate.of(1994, 4, 3))
                .build();
        userController.addNewUser(user);
        user.setId(2);
        assertThrows(IsAlreadyUserExistException.class, () -> userController.updateUser(user), "without exception");
    }
}
