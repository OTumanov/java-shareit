package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {
    private Item item;
    private ItemDto itemDto;
    private Comment comment;

    @BeforeEach
    public void beforeEach() {
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .ownerId(1L)
                .itemRequestId(2L)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .requestId(2L)
                .build();

        User user = new User(1L, "name", "user@emali.com");
        comment = new Comment(1L, "comment", item, user, LocalDateTime.now());
    }

    @Test
    public void toDto() {
        ItemDto resultWithoutBookings = ItemMapper.toDto(item, Collections.singletonList(comment));
        ItemDto resultWithBookings = ItemMapper.toDto(item, Collections.singletonList(comment));

        assertNotNull(resultWithoutBookings);
        assertNotNull(resultWithBookings);
        assertEquals(item.getId(), resultWithBookings.getId());
        assertEquals(item.getId(), resultWithoutBookings.getId());
        assertFalse(resultWithBookings.getComments().isEmpty());
        assertFalse(resultWithBookings.getComments().isEmpty());
    }

    @Test
    public void toModel() {
        Item result = ItemMapper.toModel(itemDto, 1L);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(1L, result.getOwnerId());
    }

    @Test
    public void toRequestItemDto() {
        ItemInRequestDto result = ItemMapper.toRequestItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    public void toRequestItemDtoList() {
        List<ItemInRequestDto> result = ItemMapper.toRequestItemDtoList(Collections.singletonList(item));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(item.getAvailable(), result.get(0).getAvailable());
        assertEquals(item.getItemRequestId(), result.get(0).getRequestId());
        assertEquals(item.getOwnerId(), result.get(0).getOwner());

    }
}