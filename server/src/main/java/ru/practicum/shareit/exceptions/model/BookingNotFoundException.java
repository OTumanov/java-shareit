package ru.practicum.shareit.exceptions.model;

public class BookingNotFoundException extends  RuntimeException{
    public BookingNotFoundException(String message) {
    super(message);
    }
}
