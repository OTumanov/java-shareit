package ru.practicum.shareit.item.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toDto(Item item, List<Comment> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setOwner(item.getOwner());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        if (comments != null) {
            dto.setComments(CommentMapper.toCommentDtoList(comments));
        }
        dto.setRequestId(item.getRequestId());
        return dto;
    }

    public static ItemDto toDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setOwner(item.getOwner());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(BookingMapper.toDto(lastBooking));
        System.out.println(BookingMapper.toDto(lastBooking));
        dto.setNextBooking(BookingMapper.toDto(nextBooking));
        System.out.println(BookingMapper.toDto(nextBooking));
        if (comments != null) {
            dto.setComments(CommentMapper.toCommentDtoList(comments));
        }

        System.out.println("toDto: " + dto);
        return dto;
    }

    public static Item toModel(ItemDto itemDto, Long ownerId) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(ownerId);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequestId());
        dto.setOwner(item.getOwner());
        return dto;
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(toItemDto(item));
        }
        return itemDtos;
    }

    public static ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setOwner(item.getOwner());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }
}