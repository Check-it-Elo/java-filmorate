package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Repository
public class MPADbStorage implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<MPA> getAllMPA() {
        String sql = "SELECT * FROM mpa ORDER BY id";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> MPA.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .build());
        } catch (Exception e) {
            e.printStackTrace(); // Логирование ошибки
            throw new RuntimeException("Error fetching all MPA records", e);
        }
    }

    @Override
    public MPA getMPAById(Integer id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> MPA.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .build(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA с id=" + id + " не найден");
        }
    }
}