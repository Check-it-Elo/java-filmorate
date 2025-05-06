package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> getAllUsers();

    User addUser(User user);

    User updateUser(User newUser);

    User getUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getUserFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherId);

    void deleteUser(Long userId);
}