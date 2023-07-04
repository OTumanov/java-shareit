package ru.practicum.shareit.exceptions.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse extends Throwable {
    private final String error;
}
