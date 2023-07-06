package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String email;
}