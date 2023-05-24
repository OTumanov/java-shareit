package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "Поле name не может быть пустым")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Поле email не может быть пустым")
    private String email;
}
