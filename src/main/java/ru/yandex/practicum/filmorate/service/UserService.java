package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.EnterExeption;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers());
    }


    public User getUserById(Long userId) {
        return Optional.ofNullable(userStorage.getUserById(userId))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }


    public User addUser(User user) {
        validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        User user = getUserById(userId);
        return user.getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user1 = getUserById(userId);
        User user2 = getUserById(otherId);
        return user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    //Валидация пользователя
    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации: некорректный email {}", user.getEmail());
            throw new EnterExeption("Некорректный email");
        }
        if (user.getLogin().trim().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: логин не может быть пустым или содержать пробелы {}", user.getLogin());
            throw new EnterExeption("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Имя пользователя установлено по умолчанию как логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: некорректная дата рождения {}", user.getBirthday());
            throw new EnterExeption("Некорректная дата рождения");
        }
    }

}