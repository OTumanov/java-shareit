package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    public void handleOwnerNotFoundExceptionTest() {
        AccessException e = new AccessException("Нарушение прав доступа");
        ErrorResponse errorResponse = handler.handlerAccessException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void handleDeniedAccessExceptionTest() {
        CommentException e = new CommentException("Неверный комментарий");
        ErrorResponse errorResponse = handler.handleValidationException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void handleNoSuchElementExceptionTest() {
        ConflictException e = new ConflictException("Конфликт интересов )) ");
        ErrorResponse errorResponse = handler.handleValidationException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void handleUnavailableBookingExceptionTest() {
        ErrorResponse e = new ErrorResponse("Ошибка ответа");
        ErrorResponse errorResponse = handler.handlerAccessException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void handleIllegalArgumentExceptionTest() {
        NotFoundException e = new NotFoundException("Не найдено!");
        ErrorResponse errorResponse = handler.handleUnknownDataException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void handleUnsupportedStatusExceptionTest() {
        UnsupportedStatusException e = new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        ErrorResponse errorResponse = handler.handleValidationException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void handleInvalidBookingExceptionTest() {
        UnavailableBookingException e = new UnavailableBookingException("недопустимое бронирование");
        ErrorResponse errorResponse = handler.handleValidationException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void handleCommentExceptionTest() {
        UserNotFoundException e = new UserNotFoundException("Пользователь не найден");
        ErrorResponse errorResponse = handler.handleUnknownDataException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }
}