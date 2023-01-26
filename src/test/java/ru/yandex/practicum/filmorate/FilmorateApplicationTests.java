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
   /* @Autowired
    final UserDbStorage userDbStorage;
    @Autowired
    final FilmDbStorage filmDbStorage;
    List<User> usersList;
    List<Film> filmsList;*/

    @Test
    void contextLoads() {
    }

   /* @Test
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
        usersList.add(createTestUser(1, "testMail4@mail.ru", "testLogin4", "testName4", 1983));
        usersList.add(createTestUser(2, "testMail5@mail.ru", "testLogin5", "testName5", 1984));
        usersList.add(createTestUser(3, "testMail6@mail.ru", "testLogin6", "testName6", 1985));
        return usersList;
    }

    private User createTestUser(long id, String email, String login, String name, int year) {
        return User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(LocalDate.of(year, 1, 1))
                .build();
    }

    private List<Film> createTestFilms() {
        List<Film> filmsList = new ArrayList<>();
        filmsList.add(createTestFilm(1, "testName4", "testDesc4", 1990, 180, 1, "G"));
        filmsList.add(createTestFilm(2, "testName5", "testDesc5", 1991, 180, 2, "PG"));
        filmsList.add(createTestFilm(3, "testName6", "testDesc6", 1992, 180, 3, "PG-13"));
        return filmsList;
    }

    private Film createTestFilm(long id, String name, String desc, int year, int duration, int mpaId, String mpaName) {
        return Film.builder()
                .id(id)
                .name(name)
                .description(desc)
                .releaseDate(LocalDate.of(year, 1, 1))
                .duration(duration)
                .mpa(MPA.builder().id(mpaId).name(mpaName).build())
                .build();
    }*/
}
