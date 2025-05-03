package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedDbStorage feedStorage;

    public List<Event> getUserFeed(Long userId) {
        return feedStorage.getUserFeed(userId);
    }

    public void addEvent(Long userId, EventType eventType, EventOperation operation, Long entityId) {
        Event event = new Event(
                null,                   // eventId пока null, базой сгенерируется
                System.currentTimeMillis(), // timestamp
                userId,
                eventType,
                operation,
                entityId
        );
        feedStorage.addEvent(event);
    }
}