package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;
    final String USERIDHEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<RequestWithItemsDto> findAllByUserId(@RequestHeader(value = USERIDHEADER) Long userId) {
        log.info("Запрос на получение всех вещей пользователя с id = {}", userId);
        return requestService.findAllByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public RequestWithItemsDto findById(@PathVariable Long requestId,
                                        @RequestHeader(value = USERIDHEADER) Long userId) {
        log.info("Запрос на получение вещи с id = {}", requestId);
        return requestService.findById(requestId, userId);
    }

    @GetMapping("/all")
    public List<RequestWithItemsDto> getAllRequests(
            @RequestHeader(value = USERIDHEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение всех вещей пользователя с id = {}", userId);
        return requestService.getAllRequest(userId, from, size);
    }


    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(value = USERIDHEADER) Long userId,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос на добавление вещи");
        return requestService.addRequest(userId, itemRequestDto);
    }
}