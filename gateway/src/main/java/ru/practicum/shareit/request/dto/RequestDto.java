package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    @NotBlank(groups = Create.class)
    private String description;
}