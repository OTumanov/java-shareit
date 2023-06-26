package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;
    final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<RequestWithItemsDto> findAllByUserId(@RequestHeader(value = HEADER_USER_ID) Long userId) {
        return requestService.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(value = HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return requestService.getAllRequest(userId, from, size);
    }


    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(value = HEADER_USER_ID) Long userId,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.addRequest(userId, itemRequestDto);
    }
}