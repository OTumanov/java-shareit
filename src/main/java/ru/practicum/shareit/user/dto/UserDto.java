package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotNull(message = "Поле name не может быть пустым")
    private String name;

    @Email(message = "Некорректный email")
    @NotNull(message = "Поле email не может быть пустым")
    private String email;
}
