package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingPost;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.exceptions.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storge.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.utils.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.utils.BookingStatus.WAITING;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Booking findById(Long bookingId, Long userId) {
        userRepository.findById(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isEmpty()) {
            throw new NotFoundException("Заявка не найдена");
        }

        Booking result = booking.get();
        Long itemOwner = result.getItem().getOwnerId();
        Long bookingOwner = result.getBooker().getId();

        if (!(userId.equals(bookingOwner) || userId.equals(itemOwner))) {
            throw new NotFoundException("Это не принадлежит пользователю " + userId);
        }
        return result;
    }

    @Override
    public List<Booking> findAllByBooker(String state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        List<Booking> bookings;
        BookingStatus status = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();


        switch (status) {
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, REJECTED, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, WAITING, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdCurrent(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, now, sort);
                break;
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, sort);
                break;
            default:
                throw new IllegalArgumentException("Со статусом какая-то беда...");
        }
        return bookings;

    }

    @Override
    public List<Booking> findAllByItemOwner(String state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        List<Booking> bookings;
        BookingStatus status = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();

        switch (status) {
            case REJECTED:
                bookings = bookingRepository
                        .findBookingByItemOwnerIdAndStatus(userId, REJECTED, sort);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findBookingByItemOwnerIdAndStatus(userId, WAITING, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findBookingByItemOwnerIdAndStartIsAfter(userId, now, sort);
                break;
            case PAST:
                bookings = bookingRepository
                        .findBookingByItemOwnerIdAndEndIsBefore(userId, now, sort);
                break;
            case ALL:
                bookings = bookingRepository
                        .findBookingByItemOwnerId(userId, sort);
                break;
            default:
                throw new IllegalArgumentException("Со статусом какая-то беда...");
        }
        return bookings;
    }

    @Override
    public Booking createBooking(BookingPost bookingPost, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        itemRepository.findById(bookingPost.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (bookingPost.getStart() == null || bookingPost.getEnd() == null) {
            throw new ValidationException("Не указано время начала или время завершения бронирования");
        }

        if (bookingPost.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время начала -- " + bookingPost.getStart() + " не может быть меньше текущего времени");
        }

        if (!bookingPost.getStart().isBefore(bookingPost.getEnd())) {
            throw new ValidationException("Время начала -- " + bookingPost.getStart() + " не может быть после времени завершения -- " + bookingPost.getEnd());
        }

        if (userId.equals(itemRepository.findById(bookingPost.getItemId()).get().getOwnerId())) {
            throw new NotFoundException("Нельзя забронировать свою же вещь");
        }

        if (!itemRepository.findById(bookingPost.getItemId()).get().getAvailable()) {
            throw new UnavailableBookingException("Вещь уже забронирована и недоступна для бронирования");
        }

        Booking booking = Booking.builder()
                .start(bookingPost.getStart())
                .end(bookingPost.getEnd())
                .booker(userRepository.findById(userId).get())
                .item(itemRepository.findById(bookingPost.getItemId()).get())
                .status(WAITING)
                .build();
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Booking patchBooking(Long bookingId, Boolean approved, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Booking patchedBooking = bookingRepository.findById(bookingId).get();
        Item item = itemRepository.findById(patchedBooking.getItem().getId()).get();

        if (!item.getAvailable()) {
            throw new UnavailableBookingException("Вещь уже забронирована и недоступна для бронирования");
        }

        if (!item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не является владельцем цещи: " + item.getId());
        }
        BookingStatus status = convertToStatus(approved);

        if (patchedBooking.getStatus().equals(status)) {
            throw new UnsupportedStatusException("Статус уже и так " + status);
        }

        patchedBooking.setStatus(status);
        bookingRepository.save(patchedBooking);
        return patchedBooking;
    }

    private BookingStatus parseState(String state) {
        BookingStatus status;
        try {
            status = BookingStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return status;
    }

    private BookingStatus convertToStatus(Boolean approved) {
        return approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
    }
}
