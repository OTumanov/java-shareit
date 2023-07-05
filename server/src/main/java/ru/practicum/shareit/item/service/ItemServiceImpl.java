package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.exceptions.model.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.DetailedCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    public static final int MIN_SEARCH_REQUEST_LENGTH = 3;
    public static final String OWNER_NOT_FOUND_MESSAGE = "Не найден владелец c id: ";
    public static final String EMPTY_COMMENT_MESSAGE = "Комментарий не может быть пустым";
    public static final String DENIED_ACCESS_MESSAGE = "Пользователь не является владельцем вещи";
    public static final String COMMENT_EXCEPTION_MESSAGE = "Нельзя оставить комментарий на вещь, " +
            "который вы не пользовались или ещё не закончился срок аренды";

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toModel(itemDto, userId);
        boolean ownerExists = isOwnerExists(item.getOwner());
        if (!ownerExists) {
            throw new OwnerNotFoundException(OWNER_NOT_FOUND_MESSAGE + item.getOwner());
        }
        item = itemRepository.save(item);
        return ItemMapper.toDto(item, null);
    }

    @Override
    @Transactional
    public DetailedCommentDto createComment(CreateCommentDto dto, Long itemId, Long userId) {
        if (dto.getText().isBlank()) throw new CommentException(EMPTY_COMMENT_MESSAGE);
        Item item = itemRepository.findById(itemId).orElseThrow();
        User author = userRepository.findById(userId).orElseThrow();

        if (bookingRepository.findBookingsForAddComments(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new CommentException(COMMENT_EXCEPTION_MESSAGE + " itemId: " + itemId);
        }
        Comment comment = CommentMapper.toModel(dto, item, author);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDetailedDto(comment);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = ItemMapper.toModel(itemDto, userId);
        item.setId(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        item = itemRepository.save(refreshItem(item));
        return ItemMapper.toDto(item, comments);
    }

    @Override
    public ItemDto findItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new UserNotFoundException("Вещь не найдена"));
        List<Comment> comments = commentRepository.findAllCommentsByItemId(item.getId());

        if (item.getOwner().equals(userId)) {
            return setBookingToItems(item, comments);
        }

        return ItemMapper.toDto(item, null, null, comments);
    }

    @Override
    public List<ItemDto> findAllItems(Long userId, int from, int size) {
//        Pageable pageable = PageRequest.of(from / size, size);
//        Page<Item> itemPage = itemRepository.findAll(userId, pageable);
//        List<Item> userItems = itemPage.toList();
//
//        List<ItemDto> result = new ArrayList<>();
//        fillItemDtoList(result, userItems, userId);
//
//        result.sort((o1, o2) -> {
//            if (o1.getNextBooking() == null && o2.getNextBooking() == null) {
//                return 0;
//            }
//            if (o1.getNextBooking() != null && o2.getNextBooking() == null) {
//                return -1;
//            }
//            if (o1.getNextBooking() == null && o2.getNextBooking() != null) {
//                return 1;
//            }
//            if (o1.getNextBooking().getStart().isBefore(o2.getNextBooking().getStart())) {
//                return -1;
//            }
//            if (o1.getNextBooking().getStart().isAfter(o2.getNextBooking().getStart())) {
//                return 1;
//            }
//            return 0;
//        });

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
    public List<ItemDto> findItemsByRequest(String text, Long userId, int from, int size) {
        if (text == null || text.isBlank() || text.length() <= MIN_SEARCH_REQUEST_LENGTH) {
            return Collections.emptyList();
        }
        List<ItemDto> result = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> foundItems = itemRepository.search(text, pageable).toList();
        fillItemDtoList(result, foundItems, userId);
        return result;
    }

    private boolean isOwnerExists(long ownerId) {
        List<User> users = userRepository.findAll();
        List<User> result = users.stream().filter(user -> user.getId() == ownerId).collect(Collectors.toList());
        return result.size() > 0;
    }

    private Item refreshItem(Item patch) {
        Item entry = itemRepository.findById(patch.getId()).orElseThrow();

        if (!entry.getOwner().equals(patch.getOwner())) {
            throw new DeniedAccessException(DENIED_ACCESS_MESSAGE +
                    "userId: " + patch.getOwner() + ", itemId: " + patch.getId());
        }

        String name = patch.getName();
        if (name != null && !name.isBlank()) {
            entry.setName(name);
        }

        String description = patch.getDescription();
        if (description != null && !description.isBlank()) {
            entry.setDescription(description);
        }

        Boolean available = patch.getAvailable();
        if (available != null) {
            entry.setAvailable(available);
        }
        return entry;
    }

    private void fillItemDtoList(List<ItemDto> targetList, List<Item> foundItems, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Sort sortDesc = Sort.by("start").descending();

        for (Item item : foundItems) {
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            if (item.getOwner().equals(userId)) {
                ItemDto dto = constructItemDtoForOwner(item, now, sortDesc, comments);
                targetList.add(dto);
            } else {
                targetList.add(ItemMapper.toDto(item, comments));
            }
        }
    }

    private ItemDto constructItemDtoForOwner(Item item, LocalDateTime now, Sort sort, List<Comment> comments) {
        Booking lastBooking = bookingRepository.findBookingByItemIdAndEndBefore(item.getId(), now, sort)
                .stream().findFirst().orElse(null);
        Booking nextBooking = bookingRepository.findBookingByItemIdAndStartAfter(item.getId(), now, sort)
                .stream().findFirst().orElse(null);

        return ItemMapper.toDto(item, lastBooking, nextBooking, comments);
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
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findAllBookingsByItemId(item.getId());
        System.out.println(bookings);
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
}