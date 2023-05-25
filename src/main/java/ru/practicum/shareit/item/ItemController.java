package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Запрос вещи с id = {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос всех вещей с пользователем id = {}", userId);
        return itemService.getAllItems(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на создание вещи");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader(name = "X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи с id = {}", itemId);
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с id = {}", itemId);
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        // На самом деле не очень ясно зачем в заголовке запроса во время тестирования
        // в постмане передается id пользователя. Пусть он будет в таком случае в данном методе.
        // Может, когда-то пригодится ))
        log.info("Запрос на поиск вещей с текстом = {} и пользователем с id = {}", text, userId);
        return itemService.searchItems(text, userId);
    }

}
