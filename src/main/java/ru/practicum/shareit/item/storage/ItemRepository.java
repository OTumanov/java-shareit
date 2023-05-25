package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item getItemById(Long itemId);

    List<Item> getAllItems(Long userId);

    Item createItem(Item item, Long userId);

    Item updateItem(Long itemId, Long userId, Item item);

    void deleteItem(Long itemId);

    List<Item> searchItems(String text, Long userId);
}
