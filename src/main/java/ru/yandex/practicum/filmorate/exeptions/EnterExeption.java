package ru.yandex.practicum.filmorate.exeptions;

public class EnterExeption extends RuntimeException {
    public EnterExeption(String message) {
        super(message);
    }
}