package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private MPA mpa;
    private Set<Genre> genres = new HashSet<>();
    private Set<Long> likes = new HashSet<>();
    private Set<Director> directors = new HashSet<>();

    public Set<Long> getLikes() {
        if (likes == null) {
            likes = new HashSet<>();
        }
        return likes;
    }

    public List<Genre> getGenres() {
        if (genres == null) {
            genres = new HashSet<>();
        }
        return genres.stream().sorted(Comparator.comparing(Genre::getId)) // Или любое другое правило сортировки
                     .collect(Collectors.toList());
    }

    // Дополнительный метод для получения отсортированного списка
    public List<Genre> getSortedGenres() {
        if (genres == null) {
            return Collections.emptyList();
        }
        return genres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

}