package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.EnterExeption;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
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
        // Добавляем проверку на существование пользователя
        if (userStorage.getUserById(user.getId()) == null) {
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        return userStorage.updateUser(user);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить самого себя в друзья");
        }
        getUserById(userId);
        getUserById(friendId);
        userStorage.addFriend(userId, friendId);
        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        getUserById(userId);
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        getUserById(userId);
        getUserById(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    public void deleteUser(Long userId) {
        getUserById(userId);
        userStorage.deleteUser(userId);
        log.info("Пользователь с ID {} удален", userId);
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