package ru.yandex.practicum.filmorate.Review.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Review {
    @Positive(message = "Id должен быть положительным числом")
    private Long reviewId;

    @NotBlank(message = "Строка не может быть пустой")
    private String content;

    @NotNull(message = "isPositive не может быть пустым")
    private Boolean isPositive;

    @NotNull(message = "userId не может быть пустой")
    private Long userId;

    @NotNull(message = "userId не может быть пустой")
    private Long filmId;

    private Integer useful;
}
