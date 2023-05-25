package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemRepository.getAllItems();
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        return itemRepository.createItem(itemDto, userId);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        return itemRepository.updateItem(itemId, userId, itemDto);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
    }
}
