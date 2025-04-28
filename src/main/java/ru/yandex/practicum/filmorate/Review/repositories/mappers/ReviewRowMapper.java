package ru.yandex.practicum.filmorate.Review.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Review.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper  implements RowMapper<Review> {

    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                     .reviewId(rs.getLong("review_id"))
                     .userId(rs.getLong("user_id"))
                     .isPositive(rs.getBoolean("is_positive"))
                     .content(rs.getString("content"))
                     .filmId(rs.getLong("film_id"))
                     .useful(rs.getInt("useful"))
                     .build();
    }
}
