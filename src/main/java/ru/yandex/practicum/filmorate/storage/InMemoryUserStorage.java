//package ru.yandex.practicum.filmorate.storage;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class InMemoryUserStorage implements UserStorage {
//
//    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
//
//    private final Map<Long, User> users = new HashMap<>();
//    private long idCounter = 1;
//
//    @Override
//    public Collection<User> getAllUsers() {
//        log.info("Получение всех пользователей, всего: {}", users.size());
//        return users.values();
//    }
//
//    @Override
//    public User addUser(User user) {
//        user.setId(idCounter++);
//        users.put(user.getId(), user);
//        log.info("Добавлен пользователь: {}", user);
//        return user;
//    }
//
//    @Override
//    public User updateUser(User newUser) {
//        if (!users.containsKey(newUser.getId())) {
//            log.error("Ошибка: пользователь с ID {} не найден", newUser.getId());
//            throw new NotFoundException("Пользователь с ID " + newUser.getId() + " не найден");
//        }
//        users.put(newUser.getId(), newUser);
//        log.info("Обновлён пользователь: {}", newUser);
//        return newUser;
//    }
//
//    @Override
//    public User getUserById(Long id) {
//        return users.get(id);
//    }
//
//}