package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(@Valid BookingDto dto, Long userId) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new IllegalArgumentException("Не указано время начала или время завершения бронирования");
        }

        if (!isStartBeforeEnd(dto)) {
            throw new IllegalArgumentException(
                    "недопустимые значения времени бронирования start: " + dto.getStart() + " end: " + dto.getEnd() + " now: ");
        }

        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Время начала -- " + dto.getStart() + " не может быть меньше текущего времени " + LocalDateTime.now());
        }

        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new IllegalArgumentException(
                    "Время начала -- " + dto.getStart() + " не может быть после времени завершения -- " + dto.getEnd());
        }
        return post("", userId, dto);
    }

    public ResponseEntity<Object> patchBooking(Long bookingId, Boolean approved, Long userId) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters);
    }

    public ResponseEntity<Object> findById(Long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllByBooker(String state, Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of("state", state, "from", from, "size", size);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllByItemOwner(String state, Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of("state", state, "from", from, "size", size);
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    private boolean isStartBeforeEnd(@Valid BookingDto dto) {
        return dto.getStart().isBefore(dto.getEnd());
    }
}