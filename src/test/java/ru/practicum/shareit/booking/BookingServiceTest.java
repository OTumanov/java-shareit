package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.UnavailableBookingException;
import ru.practicum.shareit.exceptions.model.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.model.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storge.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingServiceTest {
    private BookingService bookingService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private User owner;
    private Booking booking;
    private BookingPostDto bookingPostDto;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        bookingPostDto = new BookingPostDto(1L, 1L, LocalDateTime.now().plusSeconds(2), LocalDateTime.now().plusDays(7));
        user = new User(1L, "name", "user@emali.com");
        owner = new User(2L, "owner", "user2@email.ru");

        item = new Item(
                1L,
                "name",
                "description",
                true,
                2L,
                2L);

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(7))
                .build();
    }

    @Test
    public void createBookingTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingPostResponseDto result = bookingService.createBooking(bookingPostDto, 1L);

        assertNotNull(result);
        assertEquals(bookingPostDto.getItemId(), result.getItem().getId());
        assertEquals(bookingPostDto.getStart(), result.getStart());
        assertEquals(bookingPostDto.getEnd(), result.getEnd());
    }

    @Test
    public void createBookingIllegalArgumentTest() {
        LocalDateTime date = LocalDateTime.now();
        bookingPostDto.setStart(date);
        bookingPostDto.setEnd(date.minusDays(1));

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(bookingPostDto, 1L));
        assertNotNull(e);
    }

    @Test
    public void createUnavailableBooking() {
        item.setAvailable(false);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));

        UnavailableBookingException e = assertThrows(UnavailableBookingException.class,
                () -> bookingService.createBooking(bookingPostDto, 1L));
        assertNotNull(e);
    }

    @Test
    public void createInvalidBookingTest() {
        item.setOwnerId(user.getId());

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingPostDto, 1L));
        assertNotNull(e);
    }

    @Test
    public void patchBookingTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booking));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.patchBooking(1L, true, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    public void patchBookingNotFoundExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);
        item.setOwnerId(1L);

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booking));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(NotFoundException.class,
                () -> bookingService.patchBooking(1L, true, 2L));
        assertNotNull(e);
    }

    @Test
    public void patchBookingIllegalArgumentExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booking));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> bookingService.patchBooking(1L, true, 2L));
        assertNotNull(e);
    }

    @Test
    public void findByIdTest() {
        item.setOwnerId(owner.getId());

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booking));

        Booking result = bookingService.findById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void findByIdNotFoundExceptionTest() {
        user.setId(11L);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(NotFoundException.class,
                () -> bookingService.findById(1L, 1L));
        assertNotNull(e);
    }

    @Test
    public void findAllByBookerStateRejectedTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdAndStatus(any(Long.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService.findAllByBooker("REJECTED", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateWaitingTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdAndStatus(any(Long.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService.findAllByBooker("WAITING", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateCurrentTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdCurrent(any(Long.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService.findAllByBooker("CURRENT", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateFutureTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdAndStartIsAfter(any(Long.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService.findAllByBooker("FUTURE", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStatePastTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdAndEndIsBefore(any(Long.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDetailedDto> result = bookingService.findAllByBooker("PAST", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateAllTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerId(any(Long.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDetailedDto> result = bookingService.findAllByBooker("ALL", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerUnsupportedStatus() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerId(any(Long.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        UnsupportedStatusException e = assertThrows(UnsupportedStatusException.class,
                () -> bookingService.findAllByBooker("unsupported", 1L, 0, 20));
        assertNotNull(e);
    }

    @Test
    public void findAllByItemOwnerStateRejectedTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStatus(any(Long.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<Booking> result = bookingService.findAllByItemOwner("REJECTED", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateWaitingTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStatus(any(Long.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<Booking> result = bookingService.findAllByItemOwner("WAITING", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    public void findAllByItemOwnerStateFutureTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStartIsAfter(any(Long.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<Booking> result = bookingService.findAllByItemOwner("FUTURE", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStatePastTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findBookingByItemOwnerIdAndEndIsBefore(any(Long.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<Booking> result = bookingService.findAllByItemOwner("PAST", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateAllTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findBookingByItemOwnerId(any(Long.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<Booking> result = bookingService.findAllByItemOwner("ALL", 1L, 0, 20);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}