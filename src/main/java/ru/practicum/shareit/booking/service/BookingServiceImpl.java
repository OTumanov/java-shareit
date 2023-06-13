package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingPost;
import ru.practicum.shareit.booking.model.BookingPostResponse;
import ru.practicum.shareit.booking.model.BookingResponse;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.exceptions.model.AccessException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.UnavailableBookingException;
import ru.practicum.shareit.exceptions.model.UnsupportedStatusException;
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
            throw new AccessException("Это не принадлежит пользователю " + userId);
        }
        return result;
    }

    @Override
    public List<Booking> findAllByBooker(String state, Long userId) {
        userRepository.findById(userId);

        List<Booking> bookings;
        BookingStatus status = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();


        switch (status) {
            case REJECTED:
                bookings = bookingRepository
                        .findByBookerIdAndStatus(userId, REJECTED, sort);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findByBookerIdAndStatus(userId, WAITING, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdCurrent(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findByBookerIdAndStartIsAfter(userId, now, sort);
                break;
            case PAST:
                bookings = bookingRepository
                        .findByBookerIdAndEndIsBefore(userId, now, sort);
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
        userRepository.findById(userId);

        List<Booking> bookings;
        BookingStatus status = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();

        switch (status) {
            case REJECTED:
                bookings = bookingRepository
                        .findBookingByItemOwnerAndStatus(userId, REJECTED, sort);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findBookingByItemOwnerAndStatus(userId, WAITING, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findBookingByItemOwnerAndStartIsAfter(userId, now, sort);
                break;
            case PAST:
                bookings = bookingRepository
                        .findBookingByItemOwnerAndEndIsBefore(userId, now, sort);
                break;
            case ALL:
                bookings = bookingRepository
                        .findBookingByItemOwner(userId, sort);
                break;
            default:
                throw new IllegalArgumentException("Со статусом какая-то беда...");
        }
        return bookings;
    }

    @Override
    public BookingPostResponse createBooking(BookingPost bookingPost, Long userId) {
        userRepository.findById(userId);

        if (!bookingPost.getStart().isBefore(bookingPost.getEnd())) {
            throw new IllegalArgumentException("Время начала -- " + bookingPost.getStart() + " не может быть раньше времени завершения -- " + bookingPost.getEnd());
        }

        if (userId.equals(itemRepository.findById(bookingPost.getItemId()).get().getOwnerId())) {
            throw new ValidationException("Нельзя забронировать свою же вещь");
        }

        if (bookingRepository.findById(bookingPost.getItemId()).isPresent()) {
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
        return BookingMapper.toPostResponseDto(booking, item);
    }

    @Override
    public BookingResponse patchBooking(Long bookingId, Boolean approved, Long userId) {
        return null;
    }

    private BookingStatus parseState(String state) {
        BookingStatus status;
        try {
            status = BookingStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Запрошенного статуса не существует: " + state);
        }
        return status;
    }
}
