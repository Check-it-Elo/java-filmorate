package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.EnterExeption;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private long idCounter = 1;

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получены пользователи: {}", users.values());
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {

        log.info("Попытка добавления пользователя: {}", user);

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            log.error("ОШИБКА: Email не может оставаться пустым");
            throw new EnterExeption("Email не может оставаться пустым");
        }

        boolean emailAlreadyUsed = false;
        for (User u : users.values()) {
            if (u != null && u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId())) {
                emailAlreadyUsed = true;
                break;
            }
        }
        if (emailAlreadyUsed) {
            log.error("ОШИБКА: Этот email уже используется {}", user.getEmail());
            throw new EnterExeption("Этот email уже используется");
        }

        if (!user.getEmail().contains("@")) {
            log.error("ОШИБКА: Email должен содержать символ @ {}", user.getEmail());
            throw new EnterExeption("Email должен содержать символ @");
        }

        if (user.getLogin().trim().isEmpty() || user.getLogin().trim().contains(" ")) {
            log.error("ОШИБКА: Логин не может быть пустым или содержать пробелы: {}", user.getLogin());
            throw new EnterExeption("Логин не может быть пустым или содержать пробелы");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Имя пользователя установлено как и Login: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("ОШИБКА: Дата рождения не может быть в будущем или пустой");
            throw new EnterExeption("Дата рождения не может быть в будущем или пустой");
        }

        user.setId(idCounter++);
        users.put(user.getId(), user);

        log.info("Пользователь успешно добавлен: {}", user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {

        if (newUser.getId() == null) {
            log.error("ОШИБКА: ID должен быть указан");
            throw new EnterExeption("ID должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.error("ОШИБКА: Пользователь не найден: {}", newUser.getId());
            throw new EnterExeption("Пользователь не найден");
        }

        if (newUser.getEmail() == null) {
            log.error("ОШИБКА: Email не может оставаться пустым");
            throw new EnterExeption("Email не может оставаться пустым");
        }

        if (!newUser.getEmail().contains("@")) {
            log.error("ОШИБКА: Email должен содержать символ @");
            throw new EnterExeption("Email должен содержать символ @");
        }

        if (newUser.getLogin().trim().isEmpty() || newUser.getLogin().trim().contains(" ")) {
            log.error("ОШИБКА: Логин не может быть пустым или содержать пробелы");
            throw new EnterExeption("Логин не может быть пустым или содержать пробелы");
        }

        if (newUser.getBirthday() == null || newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("ОШИБКА: Дата рождения не может быть в будущем или пустой");
            throw new EnterExeption("Дата рождения не может быть в будущем или пустой");
        }

        if (users.containsKey(newUser.getId())) {
            boolean emailAlreadyUsed = false;

            for (User u : users.values()) {
                if (u != null && u.getEmail().equals(newUser.getEmail()) && !u.getId().equals(newUser.getId())) {
                    emailAlreadyUsed = true;
                    break;
                }
            }
            if (emailAlreadyUsed) {
                log.error("ОШИБКА: Этот email уже используется");
                throw new EnterExeption("Этот email уже используется");
            }
        }

        User oldUser = users.get(newUser.getId());

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
            log.info("У пользователя {} обновлен Email: {}", newUser, newUser.getEmail());
        }

        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
            log.info("У пользователя {} обновлен Login: {}", newUser, newUser.getLogin());
        }

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
            log.info("У пользователя {} обновлено имя: {}", newUser, newUser.getName());
        }
        if (newUser.getName() == null || newUser.getName().isEmpty()) {
            newUser.setName(newUser.getLogin());
            log.info("У пользователя {} имя установлено в соответствии с Login: {}", newUser, newUser.getLogin());
        }

        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
            log.info("У пользователя {} обновлена дата рождения: {}", newUser, newUser.getBirthday());
        }

        return newUser;
    }

}
