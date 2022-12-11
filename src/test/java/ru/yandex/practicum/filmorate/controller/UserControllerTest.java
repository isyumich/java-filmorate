package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.user.IsAlreadyUserExistException;
import ru.yandex.practicum.filmorate.exception.user.UserBirthdayValidationException;
import ru.yandex.practicum.filmorate.exception.user.UserEmailValidationException;
import ru.yandex.practicum.filmorate.exception.user.UserLoginValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UserControllerTest {
    UserController userController;
    User user;
    @BeforeEach
    public void BeforeEach() {
        userController = new UserController();
        user = createUser();
    }

    private User createUser() {
        return User.builder()
                .email("andrey@yandex.ru")
                .login("LoginUser")
                .name("NameUser")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void validateLoginTest() {
        user.setLogin("");
        assertThrows(UserLoginValidationException.class, () -> userController.addNewUser(user), "without login exception");
    }

    @Test
    void validateBirthdayTest() {
        user.setBirthday(LocalDate.of(2023, 1, 1));
        assertThrows(UserBirthdayValidationException.class, () -> userController.addNewUser(user), "without birthday exception");
    }

    @Test
    void validateEmailTest() {
        user.setEmail("andrey.ru");
        assertThrows(UserEmailValidationException.class, () -> userController.addNewUser(user), "without email exception");
    }

    @Test
    void validateAlreadyExistUserTest() {
        userController.addNewUser(user);
        user.setId(2);
        assertThrows(IsAlreadyUserExistException.class, () -> userController.updateUser(user), "without exception");
    }
}
