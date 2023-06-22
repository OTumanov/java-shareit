package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final RequestService requestService;
    static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(value = HEADER_USER_ID) Long userId,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(value = HEADER_USER_ID) Long userId,
                                         @PathVariable Long requestId) {
        return requestService.getById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequest(
            @RequestHeader(value = HEADER_USER_ID) Long userId) {
        return requestService.getAllUserRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(value = HEADER_USER_ID) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return requestService.getAllRequest(userId, from, size);
    }
}