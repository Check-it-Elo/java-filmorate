package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

public interface MPAStorage {

    Collection<MPA> getAllMPA();

    MPA getMPAById(Integer id);

}
