package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.TypeOperations;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource("/test-application.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
class FilmorateApplicationTests {
    @Autowired
    final UserDbStorage userDbStorage;
    @Autowired
    final FilmDbStorage filmDbStorage;
    List<User> usersList;
    List<Film> filmsList;

    @Test
    void contextLoads() {
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addNewUserTest() {
        usersList = createTestUsers();
        for (User user : usersList) {
            userDbStorage.addNewUser(user);
        }
        Optional<User> userOptional = Optional.of(userDbStorage.findUser(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "testMail4@mail.ru"));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateUserTest() {
        usersList = createTestUsers();
        for (User user : usersList) {
            userDbStorage.updateUser(user);
        }

        Optional<User> userOptional = Optional.of(userDbStorage.findUser(2));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "testMail5@mail.ru"));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllUsersTest() {
        List<User> usersList = userDbStorage.findUsers();

        Optional<User> firstUserOptional = Optional.of(usersList.get(0));
        Optional<User> secondUserOptional = Optional.of(usersList.get(1));
        assertThat(firstUserOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "testMail1@mail.ru"));
        assertThat(secondUserOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "testMail2@mail.ru"));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findUserTest() {

        Optional<User> userOptional = Optional.of(userDbStorage.findUser(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "testMail1@mail.ru"));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void addOrDeleteFriendTest() {
        userDbStorage.addOrDeleteToFriends(1, 2, TypeOperations.ADD.toString());
        User firstUser = userDbStorage.findUser(1);
        User secondUser = userDbStorage.findUser(2);
        assertEquals(secondUser.getFriendsIdsSet(), new HashSet<>());
        assertEquals(firstUser.getFriendsIdsSet(), Set.of(2L));
        userDbStorage.addOrDeleteToFriends(1, 2, TypeOperations.DELETE.toString());
        firstUser = userDbStorage.findUser(1);
        assertEquals(firstUser.getFriendsIdsSet(), new HashSet<>());
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getMutualFriendsTest() {
        userDbStorage.addOrDeleteToFriends(1, 3, TypeOperations.ADD.toString());
        userDbStorage.addOrDeleteToFriends(2, 3, TypeOperations.ADD.toString());
        List<User> mutualFriends = userDbStorage.getMutualFriends(1, 2);
        assertEquals(mutualFriends, List.of(userDbStorage.findUser(3)));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getFriendsListTest() {
        userDbStorage.addOrDeleteToFriends(1, 2, TypeOperations.ADD.toString());
        userDbStorage.addOrDeleteToFriends(1, 3, TypeOperations.ADD.toString());
        List<User> friendsList = userDbStorage.getFriendsList(1);
        assertEquals(friendsList, List.of(userDbStorage.findUser(2), userDbStorage.findUser(3)));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void addNewFilmTest() {
        filmsList = createTestFilms();
        for (Film film : filmsList) {
            filmDbStorage.addNewFilm(film);
        }
        Optional<Film> filmOptional = Optional.of(filmDbStorage.findFilm(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "testName4"));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-films.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateFilm() {
        filmsList = createTestFilms();
        for (Film film : filmsList) {
            filmDbStorage.updateFilm(film);
        }

        Optional<Film> filmOptional = Optional.of(filmDbStorage.findFilm(2));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "testName5"));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-films.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findFilms() {
        List<Film> filmsList = filmDbStorage.findFilms();

        Optional<Film> firstFilmOptional = Optional.of(filmsList.get(0));
        Optional<Film> secondFilmOptional = Optional.of(filmsList.get(1));
        assertThat(firstFilmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "testName1"));
        assertThat(secondFilmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 2L))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "testName2"));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-films.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findFilm() {

        Optional<Film> filmOptional = Optional.of(filmDbStorage.findFilm(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "testName1"));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-films.sql", "create-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void addOrDeleteLikeToFilm() {
        filmDbStorage.addOrDeleteLikeToFilm(1, 1, TypeOperations.ADD.toString());
        Film film = filmDbStorage.findFilm(1);
        assertEquals(film.getUsersWhoLiked(), Set.of(1L));
        filmDbStorage.addOrDeleteLikeToFilm(1, 1, TypeOperations.DELETE.toString());
        film = filmDbStorage.findFilm(1);
        assertEquals(film.getUsersWhoLiked(), new HashSet<>());
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql", "create-films.sql", "create-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findMostPopularFilms() {
        filmDbStorage.addOrDeleteLikeToFilm(2, 1, TypeOperations.ADD.toString());
        filmDbStorage.addOrDeleteLikeToFilm(2, 2, TypeOperations.ADD.toString());
        filmDbStorage.addOrDeleteLikeToFilm(2, 3, TypeOperations.ADD.toString());
        filmDbStorage.addOrDeleteLikeToFilm(1, 3, TypeOperations.ADD.toString());
        List<Film> mostPopularFilms = List.of(filmDbStorage.findFilm(2), filmDbStorage.findFilm(1), filmDbStorage.findFilm(3));
        assertEquals(filmDbStorage.findMostPopularFilms("10"), mostPopularFilms);
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllGenres() {
        assertEquals(Genre.builder().id(1).name("Комедия").build(), filmDbStorage.findAllGenres().get(0));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findGenre() {
        assertEquals(Genre.builder().id(1).name("Комедия").build(), filmDbStorage.findGenre(1));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllMPA() {
        assertEquals(MPA.builder().id(1).name("G").build(), filmDbStorage.findAllMPA().get(0));
    }

    @Test
    @Sql(value = {"test-schema.sql", "test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findMPA() {
        assertEquals(MPA.builder().id(1).name("G").build(), filmDbStorage.findMPA(1));
    }

    private List<User> createTestUsers() {
        List<User> usersList = new ArrayList<>();
        User firstUser = User.builder()
                .id(1)
                .email("testMail4@mail.ru")
                .login("testLogin4")
                .name("testName4")
                .birthday(LocalDate.of(1983, 1, 1))
                .build();
        User secondUser = User.builder()
                .id(2)
                .email("testMail5@mail.ru")
                .login("testLogin5")
                .name("testName5")
                .birthday(LocalDate.of(1984, 1, 1))
                .build();
        User thirdUser = User.builder()
                .id(3)
                .email("testMail6@mail.ru")
                .login("testLogin6")
                .name("testName6")
                .birthday(LocalDate.of(1985, 1, 1))
                .build();
        usersList.add(firstUser);
        usersList.add(secondUser);
        usersList.add(thirdUser);
        return usersList;
    }

    private List<Film> createTestFilms() {
        List<Film> filmsList = new ArrayList<>();
        Film firstFilm = Film.builder()
                .id(1)
                .name("testName4")
                .description("testDesc4")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(180)
                .mpa(MPA.builder().id(1).name("G").build())
                .build();
        Film secondFilm = Film.builder()
                .id(2)
                .name("testName5")
                .description("testDesc5")
                .releaseDate(LocalDate.of(1991, 1, 1))
                .duration(180)
                .mpa(MPA.builder().id(2).name("PG").build())
                .build();
        Film thirdFilm = Film.builder()
                .id(3)
                .name("testName6")
                .description("testDesc6")
                .releaseDate(LocalDate.of(1992, 1, 1))
                .duration(180)
                .mpa(MPA.builder().id(3).name("PG-13").build())
                .build();
        filmsList.add(firstFilm);
        filmsList.add(secondFilm);
        filmsList.add(thirdFilm);
        return filmsList;
    }
}
