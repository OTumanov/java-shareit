package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.UnavailableBookingException;
import ru.practicum.shareit.exceptions.model.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.model.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
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
        checkUser(userId);
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
    public List<BookingDetailedDto> findAllByBooker(String state, Long userId, Integer from, Integer size) {
        checkUser(userId);
        checkPageAndSize(from, size);

        List<Booking> bookings;
        BookingStatus status = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();

        Pageable page = PageRequest.of(from / size, size, sort);

        switch (status) {
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, REJECTED, page).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, WAITING, page).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdCurrent(userId, now, page).toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, now, page).toList();
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, now, page).toList();
                break;
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, page).toList();
                break;
            default:
                throw new IllegalArgumentException("Со статусом какая-то беда...");
        }

        return BookingMapper.toListDetailedDto(bookings);
    }

    @Override
    public List<Booking> findAllByItemOwner(String state, Long userId, Integer from, Integer size) {
        checkUser(userId);
        checkPageAndSize(from, size);
        List<Booking> bookings;
        BookingStatus status = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();
        Pageable page = PageRequest.of(from / size, size, sort);

        switch (status) {
            case REJECTED:
                bookings = bookingRepository
                        .findBookingByItemOwnerIdAndStatus(userId, REJECTED, page).toList();
                break;
            case WAITING:
                bookings = bookingRepository
                        .findBookingByItemOwnerIdAndStatus(userId, WAITING, page).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, now, page);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findBookingByItemOwnerIdAndStartIsAfter(userId, now, page).toList();
                break;
            case PAST:
                bookings = bookingRepository
                        .findBookingByItemOwnerIdAndEndIsBefore(userId, now, page).toList();
                break;
            case ALL:
                bookings = bookingRepository
                        .findBookingByItemOwnerId(userId, page).toList();
                break;
            default:
                throw new IllegalArgumentException("Со статусом какая-то беда...");
        }

        return bookings;
    }

    @Override
    @Transactional
    public BookingPostResponseDto createBooking(BookingPostDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new ValidationException("Не указано время начала или время завершения бронирования");
        }

        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время начала -- " + dto.getStart() + " не может быть меньше текущего времени " + LocalDateTime.now());
        }

        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new ValidationException("Время начала -- " + dto.getStart() + " не может быть после времени завершения -- " + dto.getEnd());
        }

        if (userId.equals(itemRepository.findById(dto.getItemId()).get().getOwnerId())) {
            throw new NotFoundException("Нельзя забронировать свою же вещь");
        }

        if (!itemRepository.findById(dto.getItemId()).get().getAvailable()) {
            throw new UnavailableBookingException("Вещь уже забронирована и недоступна для бронирования");
        }

        Booking booking = BookingMapper.toModel(dto, item, user);
        bookingRepository.save(booking);
        return BookingMapper.toPostResponseDto(booking, item);
    }

    @Override
    @Transactional
    public BookingResponseDto patchBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow();

        if (!item.getOwnerId().equals(userId)) {
            throw new NotFoundException("пользователь не является владельцем вещи userId: " + userId + " itemId: " + item.getId());
        }
        BookingStatus status = convertToStatus(approved);

        if (booking.getStatus().equals(status)) {
            throw new IllegalArgumentException("статус уже выставлен state: " + status);
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return BookingMapper.toResponseDto(booking, booking.getBooker(), item);
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

    private void checkPageAndSize(Integer page, Integer size) {
        if (page < 0 || size < 0) {
            throw new ValidationException("Параметры не могут быть меньше ноля");
        }
    }

    private void checkUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }
}
