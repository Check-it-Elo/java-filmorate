package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director create(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        jdbcTemplate.update(sql, director.getName());
        Integer id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM directors", Integer.class);
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        if (jdbcTemplate.update(sql, director.getName(), director.getId()) == 0) {
            throw new NotFoundException("Director not found");
        }
        return director;
    }

    @Override
    public void deleteById(int id) {
        jdbcTemplate.update("DELETE FROM directors WHERE id = ?", id);
    }

    @Override
    public Optional<Director> findById(int id) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        List<Director> result = jdbcTemplate.query(sql, this::mapRow, id);
        return result.stream().findFirst();
    }

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query("SELECT * FROM directors", this::mapRow);
    }

    private Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getInt("id"), rs.getString("name"));
    }

    @Override
    public Optional<Director> findByName(String name) {
        String sql = "SELECT * FROM directors WHERE name = ?";
        List<Director> result = jdbcTemplate.query(sql, this::mapRow, name);
        return result.stream().findFirst();
    }

}
