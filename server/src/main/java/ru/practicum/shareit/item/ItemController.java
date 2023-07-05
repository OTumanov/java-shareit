package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable Long itemId,
                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAllItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @RequestParam(defaultValue = DEFAULT_FROM_VALUE) int from,
                                      @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) int size) {
        return itemService.findAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByRequest(@RequestParam String text,
                                            @RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestParam(defaultValue = DEFAULT_FROM_VALUE) int from,
                                            @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) int size) {
        return itemService.findItemsByRequest(text, userId, from, size);
    }
}