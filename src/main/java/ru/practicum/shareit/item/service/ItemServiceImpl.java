package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.model.CommentException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentFromDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storge.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public ru.practicum.shareit.item.dto.ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<Comment> comments = commentRepository.findAllCommentsByItemId(item.getId());

        if (item.getOwnerId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            Sort sort = Sort.by("start").descending();
            return constructItemDtoForOwner(item, now, sort, comments);
        }
        return ItemMapper.toItemDto(item, null, null, comments);
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    @Override
    public List<ItemDto> findAllItemsByUserId(Long userId) {
        List<Item> userItems = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> result = new ArrayList<>();
        fillItemAdvancedList(result, userItems, userId);
        result.sort((o1, o2) -> {
            if (o1.getNextBooking() == null && o2.getNextBooking() == null) {
                return 0;
            }
            if (o2.getNextBooking() == null) {
                return -1;
            }
            if (o1.getNextBooking() == null) {
                return 1;
            }
            if (o1.getNextBooking().getStart().isBefore(o2.getNextBooking().getStart())) {
                return -1;
            }
            if (o1.getNextBooking().getStart().isAfter(o2.getNextBooking().getStart())) {
                return 1;
            }
            return 0;
        });
        return result;
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
    public List<Item> search(String text, Long userId) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text);
    }

    @Override
    public CommentDto createComment(CreateCommentFromDto commentDto, Long itemId, Long userId) {
        itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (commentDto.getText() == null || commentDto.getText().isBlank() || commentDto.getText().isEmpty()) {
            throw new ValidationException("Комментарий не может быть пустым");
        }
        if (bookingRepository.findBookingsForAddComments(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new CommentException("Этой вещью вы не пользовались или не закончился срок аренды!");
        }
        Comment comment = CommentMapper.toModel(commentDto,
                itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена")),
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден")));

        return CommentMapper.toCommentDetailedDto(comment);
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

    private void fillItemAdvancedList(List<ItemDto> result, List<Item> foundItems, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();

        for (Item item : foundItems) {
            List<Comment> comments = commentRepository.findAllCommentsByItemId(item.getId());
            if (item.getOwnerId().equals(userId)) {
                ItemDto itemDto = constructItemDtoForOwner(item, now, sort, comments);
                result.add(itemDto);
            } else {
                result.add(ItemMapper.toItemDto(item, null, null, comments));
            }
        }
    }

    private ru.practicum.shareit.item.dto.ItemDto constructItemDtoForOwner(Item item, LocalDateTime now, Sort sort, List<Comment> comments) {
        Booking lastBooking = bookingRepository.findBookingByItemIdAndEndBefore(item.getId(), now, sort)
                .stream().findFirst().orElse(null);

        Booking nextBooking = bookingRepository.findBookingByItemIdAndStartAfter(item.getId(), now, sort)
                .stream().findFirst().orElse(null);

        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }
}
