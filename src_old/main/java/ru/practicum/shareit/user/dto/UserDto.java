package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotNull
    @NotBlank
    @NotEmpty
    private String name;

    @Email(message = "Некорректный email")
    @NotNull(message = "Поле email не может быть пустым")
    @NotEmpty(message = "Поле email не может быть пустым")
    @NotBlank
    private String email;
}