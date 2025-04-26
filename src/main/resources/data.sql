DELETE FROM film_genres;
DELETE FROM film_likes;
DELETE FROM films;
DELETE FROM genres;
DELETE FROM mpa;
DELETE FROM friendships;
DELETE FROM users;

-- Заполнение жанров
INSERT INTO genres (id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

-- Заполнение MPA
INSERT INTO mpa (id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

-- Добавление тестового пользователя
INSERT INTO users (email, login, name, birthday)
VALUES
('user@example.com', 'user1', 'User Name', '1990-01-01'),
('friend@example.com', 'friend1', 'Friend Name', '1992-05-15');

-- Добавление тестовых фильмов
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES
('Film 1', 'Description 1', '2000-01-01', 120, 3),
('Film 2', 'Description 2', '2010-05-10', 90, 4),
('Film 3', 'Description 3', '2020-10-20', 150, 2);

-- Создание связей фильмов и жанров
INSERT INTO film_genres (film_id, genre_id)
VALUES
(1, 1), -- Film 1 - Комедия
(1, 2), -- Film 1 - Драма
(2, 4), -- Film 2 - Триллер
(3, 6), -- Film 3 - Боевик
(3, 2); -- Film 3 - Драма

-- Добавление лайков
INSERT INTO film_likes (user_id, film_id)
VALUES
(1, 1), -- User 1 likes Film 1
(1, 3), -- User 1 likes Film 3
(2, 2); -- User 2 likes Film 2

-- Добавление дружбы
INSERT INTO friendships (user_id, friend_id, friendship_status)
VALUES
(1, 2, 'CONFIRMED');



-- Добавим режиссёров
INSERT INTO directors (name) VALUES ('Квентин Тарантино'), ('Кристофер Нолан');

-- Привяжем к фильмам
INSERT INTO film_directors (film_id, director_id) VALUES
(1, 1),  -- Film 1 -> Тарантино
(2, 2);  -- Film 2 -> Нолан