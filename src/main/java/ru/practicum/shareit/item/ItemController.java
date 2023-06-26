package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentFromDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utils.ItemMapper;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
        log.info("Запрос на получение вещи с id = {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение всех вещей");
        return itemService.findAllItemsByUserId(userId, from, size);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Запрос на создание вещи");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader(name = USER_ID_HEADER) Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи с id = {}", itemId);
        return ItemMapper.toItemDto(itemService.updateItem(itemId, userId, ItemMapper.toItem(itemDto)));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с id = {}", itemId);
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @PositiveOrZero @RequestHeader(defaultValue = "0") Integer from,
                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на поиск вещей");
        return ItemMapper.toItemDtoList(itemService.search(text, from, size));
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CreateCommentFromDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на создание комментария");
        return itemService.createComment(commentDto, itemId, userId);
    }

}
