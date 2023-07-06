package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.exceptions.model.CommentException;
import ru.practicum.shareit.exceptions.model.DeniedAccessException;
import ru.practicum.shareit.exceptions.model.ItemNotFoundException;
import ru.practicum.shareit.exceptions.model.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto findItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        List<Comment> comments = commentRepository.findAllCommentsByItemId(item.getId());

        if (item.getOwner().equals(userId)) {
            return setBookingToItems(item, comments);
        }

        return ItemMapper.toDto(item, null, null, comments);
    }

    @Transactional
    @Override
    public List<ItemDto> findAllItems(Long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Item> userItems = itemRepository.findAllByOwner(userId, page);
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
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        checkItem(itemDto, userId);
        itemDto.setOwner(userId);
        Item item = ItemMapper.toModel(itemDto, userId);
        item = itemRepository.save(item);

        return ItemMapper.toDto(item, null);
    }

    @Override
    public ItemDto updateItem(ItemDto item, Long itemId, Long userId) {
        String name = (item.getName() == null || item.getName().isEmpty() || item.getName().isBlank())
                ? getItemById(itemId).getName() : item.getName();
        String description = (item.getDescription() == null || item.getDescription().isEmpty() || item.getDescription().isBlank()
                ? getItemById(itemId).getDescription() : item.getDescription());
        boolean available = (item.getAvailable() == null) ? getItemById(itemId).getAvailable() : item.getAvailable();
        if (Objects.equals(userId, itemRepository.findById(itemId).get().getOwner())) {

            Item updatedItem;
            updatedItem = Item.builder()
                    .id(itemId)
                    .name(name)
                    .description(description)
                    .available(available)
                    .owner(userId)
                    .build();


            itemRepository.save(updatedItem);
            return ItemMapper.toDto(updatedItem, null);
        } else {
            throw new DeniedAccessException("Доступ запрещен");
        }
    }

    @Transactional
    @Override
    public List<ItemDto> findItemsByRequest(String text, Long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailableItems(text, page).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (commentDto.getText() == null || commentDto.getText().isBlank() || commentDto.getText().isEmpty()) {
            throw new CommentException("Комментарий не может быть пустым");
        }
        if (bookingRepository.findBookingsForAddComments(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new CommentException("Этой вещью вы не пользовались или не закончился срок аренды!");
        }
        Comment comment = CommentMapper.toModel(commentDto,
                itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена")),
                userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден")));
        commentRepository.save(comment);

        return CommentMapper.toDto(comment);
    }

    public void checkItem(ItemDto itemDto, Long userId) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Не указана доступность вещи");
        } else if (itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getName().isEmpty()) {
            throw new ValidationException("У вещи должно быть указано имя");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank() || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("У вещи должно быть указано описание");
        } else if (userService.findUserById(userId) == null) {
            throw new UserNotFoundException("Нет такого пользователя");
        }
    }

    private void fillItemAdvancedList(List<ItemDto> result, List<Item> foundItems, Long userId) {
        for (Item item : foundItems) {
            List<Comment> comments = commentRepository.findAllCommentsByItemId(item.getId());
            if (item.getOwner().equals(userId)) {
                ItemDto itemDto = setBookingToItems(item, comments);
                result.add(itemDto);
            } else {
                result.add(ItemMapper.toDto(item, null, null, comments));
            }
        }
    }

    private ItemDto setBookingToItems(Item item, List<Comment> comments) {

        List<Booking> bookings = bookingRepository.findAllBookingsByItemId(item.getId());
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isBefore(now))
                .min((obj1, obj2) -> obj2.getStart().compareTo(obj1.getStart())).orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);

        return ItemMapper.toDto(item, lastBooking, nextBooking, comments);
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
    }
}