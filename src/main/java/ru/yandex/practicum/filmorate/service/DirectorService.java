package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director create(Director director) {

        if (director.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Director has empty name ");
        }

        directorStorage.findByName(director.getName()).ifPresent(existingDirector -> {
            throw new IllegalArgumentException("Director with name '" + director.getName() + "' already exists.");
        });

        return directorStorage.create(director);
    }

    public Director update(Director director) {
        Director existingDirector = directorStorage.findById(director.getId())
                                                   .orElseThrow(() -> new NotFoundException("Director with id " +
                                                                                            director.getId() +
                                                                                            " not found"));

        return directorStorage.update(director);
    }

    public void deleteById(int id) {
        directorStorage.findById(id).orElseThrow(() -> new NotFoundException("Director with id " + id + " not found"));

        directorStorage.deleteById(id);
    }

    public Director findById(int id) {
        return directorStorage.findById(id)
                              .orElseThrow(() -> new NotFoundException("Director with id " + id + " not found"));
    }

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public void validateDirectorExists(int directorId) {
        if (directorStorage.findById(directorId) == null) {
            throw new NotFoundException("Режиссёр с ID " + directorId + " не найден");
        }
    }
}
