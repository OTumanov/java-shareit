package ru.practicum.shareit.item.storage.inMemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.model.AccessException;
import ru.practicum.shareit.exceptions.model.ConflictException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final UserService userService;
    Map<Long, Item> allItems = new HashMap<>();
    Long nextId = 0L;

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(allItems.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<ItemDto> result = new ArrayList<>();
        for (Item i : allItems.values()) {
            result.add(ItemMapper.toItemDto(i));
        }
        return result;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        userService.getUserById(userId);
        checkItemAvailable(itemDto);
        checkItemName(itemDto);
        checkDescription(itemDto);
//        for (Item i : allItems.values()) {
//            checkItemDuplicate(itemDto);
//        }

        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setId(++nextId);
        newItem.setOwner(userId);
        allItems.put(newItem.getId(), newItem);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        checkItem(itemId);
        checkOwnerOfItem(itemId, userId);

        Item updatedItem = allItems.get(itemId);

        if (!(itemDto.getName() == null)) {
            updatedItem.setName(itemDto.getName());
        }

        if (!(itemDto.getDescription() == null)) {
            updatedItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public void deleteItem(Long itemId) {

    }

    private void checkItem(Long itemId) {
        if (allItems.get(itemId) == null) {
            throw new NotFoundException("Такая вещь не найдена!");
        }
    }

//    private void checkItemDuplicate(ItemDto itemDto) {
//        if (itemDto.getName().equals(itemDto.getName())) {
//            throw new ConflictException("Вещь с именем = " + itemDto.getName() + " уже существует!");
//        }
//    }

    private void checkItemAvailable(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Вещь не доступна!");
        }
    }

    private void checkItemName(ItemDto itemDto) {
        if (itemDto.getName() == null) {
            throw new ConflictException("Вещь не может быть безымянной!");
        }
        if (itemDto.getName().isEmpty() || itemDto.getName().isBlank()) {
            throw new ValidationException("Поле имени должно быть заполнено!");
        }
    }

    private void checkDescription(ItemDto itemDto) {
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Отсутствует описание!");
        }
    }

    private void checkOwnerOfItem(Long itemDto, Long userId) {
        if (!allItems.get(itemDto).getOwner().equals(userId)) {
            throw new AccessException("Вещь не принадлежит данному пользователю!");
        }
    }
}
