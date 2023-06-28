package ru.practicum.shareit.exceptions.model;

public class UnavailableBookingException extends RuntimeException {
    public UnavailableBookingException(String message) {
        super(message);
    }
}