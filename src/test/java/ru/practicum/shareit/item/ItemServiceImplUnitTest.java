package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.exceptions.model.AccessException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplUnitTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    private final User user = new User(1L, "testUser", "test@email.com");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("testDescription")
            .requester(user)
            .items(new ArrayList<>())
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("testItem")
            .description("testDescription")
            .available(true)
            .itemRequestId(itemRequest.getId())
            .ownerId(1L)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("testItem")
            .description("testDescription")
            .available(true)
            .requestId(1L)
            .build();
    private final List<Booking> bookingList = List.of(
            Booking.builder()
                    .id(1L).item(item).booker(user)
                    .start(LocalDateTime.now().minusHours(2L))
                    .end(LocalDateTime.now().minusHours(1L))
                    .status(BookingStatus.WAITING).build(),
            Booking.builder()
                    .id(2L).item(item).booker(user)
                    .start(LocalDateTime.now().plusHours(1L))
                    .end(LocalDateTime.now().plusHours(2L))
                    .status(BookingStatus.WAITING).build());
    private final Comment comment = Comment.builder()
            .id(1L)
            .text("testText")
            .item(item)
            .author(user)
            .build();

    @Test
    public void getItemById_shouldReturnItemDtoWithBookingsWhenOwnerRequestItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(commentRepository.findAllCommentsByItemId(anyLong())).thenReturn(List.of(comment));

        when(bookingRepository.findAllBookingsByItemId(anyLong())).thenReturn(bookingList);

        ItemDto requestedItemDto = itemService.getItemById(1L, 1L);

        assertThat(requestedItemDto.getName(), equalTo(item.getName()));
        assertThat(requestedItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(requestedItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(requestedItemDto.getComments(), hasSize(1));
        assertThat(requestedItemDto.getLastBooking().getId(), equalTo(1L));
        assertThat(requestedItemDto.getLastBooking().getBookerId(), equalTo(1L));
        assertThat(requestedItemDto.getNextBooking().getId(), equalTo(2L));
        assertThat(requestedItemDto.getNextBooking().getBookerId(), equalTo(1L));
    }

    @Test
    public void getItemById_shouldReturnItemDtoWithoutBookingsWhenNotOwnerRequestItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(commentRepository.findAllCommentsByItemId(anyLong())).thenReturn(List.of(comment));

        ItemDto requestedItemDto = itemService.getItemById(1L, 2L);

        assertThat(requestedItemDto.getName(), equalTo(item.getName()));
        assertThat(requestedItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(requestedItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(requestedItemDto.getComments(), hasSize(1));
        assertThat(requestedItemDto.getLastBooking(), nullValue());
        assertThat(requestedItemDto.getNextBooking(), nullValue());
    }

    @Test
    void getItemById_shouldThrowNotFoundExceptionWhenItemIsNotExist() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
        assertThat(e.getMessage(), equalTo("Вещь не найдена"));
    }

    @Test
    void getAllUserItems_shouldReturnItem() {
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));
        when(bookingRepository.findAllBookingsByItemId(anyLong())).thenReturn(bookingList);

        List<ItemDto> userItemsList = itemService.findAllItemsByUserId(1L, 0, 10);

        assertThat(userItemsList, hasSize(1));
        assertThat(userItemsList.get(0).getLastBooking().getId(), equalTo(1L));
        assertThat(userItemsList.get(0).getLastBooking().getBookerId(), equalTo(1L));
        assertThat(userItemsList.get(0).getNextBooking().getId(), equalTo(2L));
        assertThat(userItemsList.get(0).getNextBooking().getBookerId(), equalTo(1L));
    }

    @Test
    void editItem_shouldThrowUserIsNotOwnerExceptionWhenNotOwnerRequestUpdate() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Exception e = Assertions.assertThrows(AccessException.class,
                () -> itemService.updateItem(1L, 2L, ItemMapper.toItem(itemDto)));

        assertThat(e.getMessage(), equalTo("Доступ запрещен"));
    }
}