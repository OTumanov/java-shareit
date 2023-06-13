package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.State;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.exceptions.model.AccessException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.UnsupportedStatusException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storge.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.utils.BookingStatus.*;

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
            throw new AccessException("Это не принадлежит пользователю " + userId);
        }
        return result;
    }

    @Override
    public List<Booking> findAllByBooker(String state, Long userId) {

        State status = parseState(state);
        userRepository.findById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        Sort sort = Sort.by("start").descending();

        switch (status) {
            case REJECTED :
                bookings = bookingRepository
                        .findByBookerIdAndStatus(userId, REJECTED, sort);
                break;
            case WAITING :
                bookings = bookingRepository
                        .findByBookerIdAndStatus(userId, WAITING, sort);
                break;
            case CURRENT :
                bookings = bookingRepository.findByBookerIdCurrent(userId, now);
                break;
            case FUTURE :
                bookings = bookingRepository
                        .findByBookerIdAndStartIsAfter(userId, now, sort);
                break;
            case PAST :
                bookings = bookingRepository
                        .findByBookerIdAndEndIsBefore(userId, now, sort);
                break;
            case ALL :
                bookings = bookingRepository.findByBookerId(userId, sort);
                break;
            default :
                throw new IllegalArgumentException("Со статусом какая-то беда...");
        }
        return bookings;

    }

    @Override
    public List<BookingDetailed> findAllByItemOwner(String state, Long userId) {
        return null;
    }

    @Override
    public BookingPostResponse createBooking(BookingPost bookingPost, Long userId) {
        return null;
    }

    @Override
    public BookingResponse patchBooking(Long bookingId, Boolean approved, Long userId) {
        return null;
    }

    private State parseState(String state) {
        State status;
        try {
            status = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Запрошенного статуса не существует: " + state);
        }
        return status;
    }
}
