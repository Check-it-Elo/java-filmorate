package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director create(@RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        directorService.deleteById(id);
    }

    @GetMapping
    public List<Director> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable int id) {
        return directorService.findById(id);
    }

}
