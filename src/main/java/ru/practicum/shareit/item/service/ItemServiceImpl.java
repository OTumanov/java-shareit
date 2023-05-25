package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        return itemRepository.getAllItems(userId);
    }

    @Override
    public Item createItem(Item item, Long userId) {
        return itemRepository.createItem(item, userId);
    }

    @Override
    public Item updateItem(Long itemId, Long userId, Item item) {
        return itemRepository.updateItem(itemId, userId, item);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
    }

    @Override
    public List<Item> searchItems(String text, Long userId) {
        return itemRepository.searchItems(text, userId);
    }
}
