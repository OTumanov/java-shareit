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

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Validated({Create.class})
                                                @RequestBody RequestDto requestDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.createRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestParam(defaultValue = "0")
                                          @Min(0) int from,
                                          @RequestParam(defaultValue = "20")
                                          @Min(0) int size,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.findById(requestId, userId);
    }
}