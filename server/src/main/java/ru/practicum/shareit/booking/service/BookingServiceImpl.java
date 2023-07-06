package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.booking.utils.State;
import ru.practicum.shareit.exceptions.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.practicum.shareit.booking.utils.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.utils.BookingStatus.WAITING;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto dto, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Нет такого пользователя!"));
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new UserNotFoundException("Нет такого вещи!"));

        if (userId.equals(item.getOwner())) {
            throw new InvalidBookingException("Нельзя забронировать свою же вещь");
        }

        if (!item.getAvailable()) {
            throw new UnavailableBookingException("Сейчас нельзя забронировать это - " + item.getId());
        }

        Booking booking = BookingMapper.toModel(dto, item, user);
        booking = bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto patchBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Нет такого бронирования!"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new ItemNotFoundException("Нет такого вещи!"));

        if (!item.getOwner().equals(userId)) {
            throw new NoSuchElementException("Этот пользователь " + userId + " не владелец вещи " + item.getId());
        }
        BookingStatus status = convertToStatus(approved);

        if (booking.getStatus().equals(status)) {
            throw new IllegalArgumentException("Статус уже установлен");
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);

        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Нет такого пользователя!"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Long itemOwner = booking.getItem().getOwner();
        Long bookingOwner = booking.getBooker().getId();
        boolean itemOrBookingOwner = userId.equals(bookingOwner) || userId.equals(itemOwner);

        if (!itemOrBookingOwner) {
            throw new NoSuchElementException("Пользователь " + userId + " не является владельцем вещи");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> findAllByBooker(String state, Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Нет такого пользователя!"));
        List<Booking> bookings = new ArrayList<>();
        State status = parseState(state);
        Sort sort = Sort.by("start").descending();
        Pageable pageable = PageRequest.of(from / size, size, sort);
        LocalDateTime now = LocalDateTime.now();

        switch (status) {
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, REJECTED, pageable).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, WAITING, pageable).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdCurrent(userId, now, pageable).toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, now, pageable).toList();
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, now, pageable).toList();
                break;
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, pageable).toList();
                break;
        }

        System.out.println(bookings);
        return BookingMapper.toListDetailedDto(bookings);
    }

    @Override
    public List<BookingDto> findAllByItemOwner(String state, Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Нет такого пользователя!"));
        checkPageAndSize(from, size);
        List<Booking> bookings;
        State status = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();
        Pageable page = PageRequest.of(from / size, size, sort);

        switch (status) {
            case REJECTED:
                bookings = bookingRepository.findBookingByItemOwnerAndStatus(userId, REJECTED, page).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findBookingByItemOwnerAndStatus(userId, WAITING, page).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, now, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingByItemOwnerAndStartIsAfter(userId, now, page).toList();
                break;
            case PAST:
                bookings = bookingRepository.findBookingByItemOwnerAndEndIsBefore(userId, now, page).toList();
                break;
            case ALL:
                bookings = bookingRepository.findBookingByItemOwner(userId, page).toList();
                break;
            default:
                throw new IllegalArgumentException("Со статусом какая-то беда...");
        }

        return BookingMapper.toListDetailedDto(bookings);
    }

    private State parseState(String state) {
        State status;
        try {
            status = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("  state: " + state);
        }
        return status;
    }

    private void checkPageAndSize(Integer page, Integer size) {
        if (page < 0 || size < 0) {
            throw new ValidationException("Параметры не могут быть меньше ноля");
        }
    }

    private BookingStatus convertToStatus(Boolean approved) {
        return approved ? BookingStatus.APPROVED : REJECTED;
    }
}