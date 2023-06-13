package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.model.AccessException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storge.UserRepository;

import java.util.List;
import java.util.Optional;

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
    public List<BookingDetailed> findAllByBooker(String state, Long userId) {
        return null;
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
}
