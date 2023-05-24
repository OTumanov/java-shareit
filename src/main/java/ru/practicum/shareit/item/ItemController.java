package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

//    @GetMapping("/{itemId}")
//    public ItemDto getItemById(@PathVariable Long itemId) {
//        return itemService.getItemById(itemId);
//    }
//
//    @GetMapping
//    public List<ItemDto> getAllItems() {
//        return itemService.getAllItems();
//    }
//
//    @PostMapping
//    public ItemDto createItem(@RequestBody ItemDto itemDto) {
//        return itemService.createItem(itemDto);
//    }
}
