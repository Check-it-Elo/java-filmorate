package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.EnterExeption;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;
    private final FeedService feedService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FeedService feedService) {
        this.userStorage = userStorage;
        this.feedService = feedService;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers());
    }

    public User getUserById(Long userId) {
        return Optional.ofNullable(userStorage.getUserById(userId))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

//    public User addUser(User user) {
//        validateUser(user);
//        return userStorage.addUser(user);
//    }

    public User addUser(User user) {
        try {
            validateUser(user);
            return userStorage.addUser(user);
        } catch (EnterExeption e) {
            log.error("Validation error when adding user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error when adding user: {}", e.getMessage());
            throw new RuntimeException("Internal server error when creating user", e);
        }
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
        feedService.addEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
        feedService.addEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
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

    public List<Event> getUserFeed(Long userId) {
        return feedService.getUserFeed(userId);
    }

    //Валидация пользователя
//    private void validateUser(User user) {
//        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
//            log.error("Ошибка валидации: некорректный email {}", user.getEmail());
//            throw new EnterExeption("Некорректный email");
//        }
//        if (user.getLogin().trim().isEmpty() || user.getLogin().contains(" ")) {
//            log.error("Ошибка валидации: логин не может быть пустым или содержать пробелы {}", user.getLogin());
//            throw new EnterExeption("Логин не может быть пустым или содержать пробелы");
//        }
//        if (user.getName() == null || user.getName().isEmpty()) {
//            log.info("Имя пользователя установлено по умолчанию как логин: {}", user.getLogin());
//            user.setName(user.getLogin());
//        }
//        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
//            log.error("Ошибка валидации: некорректная дата рождения {}", user.getBirthday());
//            throw new EnterExeption("Некорректная дата рождения");
//        }
//    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Validation error: Invalid email {}", user.getEmail());
            throw new EnterExeption("Некорректный email");
        }
        if (user.getLogin() == null || user.getLogin().trim().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Validation error: Login cannot be empty or contain spaces {}", user.getLogin());
            throw new EnterExeption("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Name is empty, setting to login: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Validation error: Invalid birthday {}", user.getBirthday());
            throw new EnterExeption("Некорректная дата рождения");
        }
    }

}