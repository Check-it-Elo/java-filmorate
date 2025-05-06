package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.EventOperation;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addEvent(Event event) {
        String sql = "INSERT INTO feed (timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId());
    }

    public List<Event> getUserFeed(Long userId) {
        String sql = "SELECT * FROM feed WHERE user_id = ? ORDER BY timestamp ASC, event_id ASC";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> new Event(
                    rs.getLong("event_id"),
                    rs.getLong("timestamp"),
                    rs.getLong("user_id"),
                    EventType.valueOf(rs.getString("event_type").toUpperCase()),
                    EventOperation.valueOf(rs.getString("operation").toUpperCase()),
                    rs.getLong("entity_id")
            ), userId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Ошибка при обработке событий пользователя: неверное значение event_type или operation", e);
        }
    }
}
