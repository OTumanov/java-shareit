package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Min;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    public static final int MIN_VALUE = 0;
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Validated({Create.class})
                                                @RequestBody RequestDto requestDto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.createRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                          @Min(MIN_VALUE) int from,
                                          @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                          @Min(MIN_VALUE) int size,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestId,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.findById(requestId, userId);
    }
}