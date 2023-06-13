package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item getItemById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Вещь не найдена!");
        } else {
            return item.get();
        }
    }

    @Override
    public List<Item> findAllItemsByUserId(Long userId) {
        return itemRepository.findAll()
                .stream().filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item createItem(Item item, Long userId) {
        if (checkItem(item, userId)) {
            item.setOwnerId(userId);
            return itemRepository.save(item);
        } else {
            throw new ValidationException("Не все поля заполнены!");
        }
    }

    @Override
    public Item updateItem(Long itemId, Long userId, Item item) {

        String name = (item.getName() == null || item.getName().isEmpty() || item.getName().isBlank())
                ? getItemById(itemId).getName() : item.getName();
        String description = (item.getDescription() == null || item.getDescription().isEmpty() || item.getDescription().isBlank()
                ? getItemById(itemId).getDescription() : item.getDescription());
        boolean available = (item.getAvailable() == null) ? getItemById(itemId).getAvailable() : item.getAvailable();
        Long ownerId = (userId != null) ? userId : getItemById(itemId).getOwnerId();

        Item updatedItem = Item.builder()
                .id(itemId)
                .name(name)
                .description(description)
                .available(available)
                .ownerId(ownerId)
                .build();

        return itemRepository.save(updatedItem);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<Item> searchItemByText(String text, Long userId) {
        return itemRepository.searchItemByText(text);
    }

    private boolean checkItem(Item item, Long userId) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Не указана доступность вещи");
        } else if (item.getName() == null || item.getName().isBlank() || item.getName().isEmpty()) {
            throw new ValidationException("У вещи должно быть указано имя");
        } else if (item.getDescription() == null || item.getDescription().isBlank() || item.getDescription().isEmpty()) {
            throw new ValidationException("У вещи должно быть указано описание");
        } else if (userService.findUserById(userId) == null) {
            throw new NotFoundException("Нет такого пользователя");
        }

        return true;
    }
}
