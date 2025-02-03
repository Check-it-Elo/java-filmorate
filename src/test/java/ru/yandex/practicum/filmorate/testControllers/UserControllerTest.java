package ru.yandex.practicum.filmorate.testControllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeptions.EnterExeption;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void testAddUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(Instant.parse("2000-01-01T00:00:00Z"));

        User addedUser = userController.addUser(user);

        assertNotNull(addedUser);
        assertEquals(user.getEmail(), addedUser.getEmail());
        assertEquals(user.getLogin(), addedUser.getLogin());
        assertEquals(user.getName(), addedUser.getName());
    }

    @Test
    void testAddUser_InvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(Instant.parse("2000-01-01T00:00:00Z"));

        EnterExeption exception = assertThrows(EnterExeption.class, () -> userController.addUser(user));

        assertEquals("Email должен содержать символ @", exception.getMessage());
    }

    @Test
    void testAddUser_EmptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(Instant.parse("2000-01-01T00:00:00Z"));

        EnterExeption exception = assertThrows(EnterExeption.class, () -> userController.addUser(user));

        assertEquals("Email не может оставаться пустым", exception.getMessage());
    }

    @Test
    void testUpdateUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(Instant.parse("2000-01-01T00:00:00Z"));

        User addedUser = userController.addUser(user);

        User updatedUser = new User();
        updatedUser.setId(addedUser.getId());
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setLogin("newLogin");
        updatedUser.setName("New Name");
        updatedUser.setBirthday(Instant.parse("1995-05-05T00:00:00Z"));

        User returnedUser = userController.updateUser(updatedUser);

        assertEquals(updatedUser.getEmail(), returnedUser.getEmail());
        assertEquals(updatedUser.getLogin(), returnedUser.getLogin());
        assertEquals(updatedUser.getName(), returnedUser.getName());
        assertEquals(updatedUser.getBirthday(), returnedUser.getBirthday());
    }

    @Test
    void testUpdateUser_InvalidEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(Instant.parse("2000-01-01T00:00:00Z"));

        User addedUser = userController.addUser(user);

        User updatedUser = new User();
        updatedUser.setId(addedUser.getId());
        updatedUser.setEmail("invalid-email");
        updatedUser.setLogin("newLogin");
        updatedUser.setName("New Name");
        updatedUser.setBirthday(Instant.parse("1995-05-05T00:00:00Z"));

        EnterExeption exception = assertThrows(EnterExeption.class, () -> userController.updateUser(updatedUser));

        assertEquals("Email должен содержать символ @", exception.getMessage());
    }

    @Test
    void testUpdateUser_NotFound() {
        User updatedUser = new User();
        updatedUser.setId(999L);
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setLogin("newLogin");
        updatedUser.setName("New Name");
        updatedUser.setBirthday(Instant.parse("1995-05-05T00:00:00Z"));

        EnterExeption exception = assertThrows(EnterExeption.class, () -> userController.updateUser(updatedUser));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(Instant.parse("2000-01-01T00:00:00Z"));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(Instant.parse("2000-02-01T00:00:00Z"));

        userController.addUser(user1);
        userController.addUser(user2);

        Collection<User> allUsers = userController.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
    }
}
