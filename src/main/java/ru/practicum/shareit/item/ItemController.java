package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentFromDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utils.ItemMapper;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                                                             @RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Запрос вещи с id = {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Запрос всех вещей пользователя с id = {}", userId);
        return itemService.findAllItemsByUserId(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ru.practicum.shareit.item.dto.ItemDto itemDto,
                                                            @RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Запрос на создание вещи");
        return ItemMapper.toItemDto(itemService.createItem(ItemMapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                                                            @RequestHeader(name = USER_ID_HEADER) Long userId,
                                                            @RequestBody ru.practicum.shareit.item.dto.ItemDto itemDto) {
        log.info("Запрос на обновление вещи с id = {}", itemId);
        return ItemMapper.toItemDto(itemService.updateItem(itemId, userId, ItemMapper.toItem(itemDto)));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с id = {}", itemId);
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ru.practicum.shareit.item.dto.ItemDto> searchItems(@RequestParam String text,
                                                                   @RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Запрос на поиск вещей с текстом = {} и пользователем с id = {}", text, userId);
        return ItemMapper.toItemDtoList(itemService.search(text, userId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CreateCommentFromDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }

}
