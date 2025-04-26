package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Director create(Director director);

    Director update(Director director);

    void deleteById(int id);

    Optional<Director> findById(int id);

    List<Director> findAll();

    Optional<Director> findByName(String name);

}
