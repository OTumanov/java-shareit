package ru.practicum.shareit.item.utils;

import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentMapper;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .build();
    }

    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .build();
        if (comments != null) {
            itemDto.setComments(CommentMapper.toCommentDtoList(comments));
        } else {
            itemDto.setComments(new ArrayList<>());
        }

        return itemDto;
    }

    public static ItemDto toItemDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {

        if (lastBooking == null || nextBooking == null) {
            return toItemDto(item, comments);
        }
        BookingInItemDto last = BookingInItemDto.builder()
                .id(lastBooking.getId())
                .start(lastBooking.getStart())
                .end(lastBooking.getEnd())
                .bookerId(lastBooking.getBooker().getId())
                .status(lastBooking.getStatus())
                .build();

        BookingInItemDto next = BookingInItemDto.builder()
                .id(nextBooking.getId())
                .start(nextBooking.getStart())
                .end(nextBooking.getEnd())
                .bookerId(nextBooking.getBooker().getId())
                .status(nextBooking.getStatus())
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .nextBooking(next)
                .lastBooking(last)
                .build();

        if (comments != null) {
            itemDto.setComments(CommentMapper.toCommentDtoList(comments));
        } else {
            itemDto.setComments(new ArrayList<>());
        }

        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(itemDto.getOwnerId())
                .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {

        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(toItemDto(item));
        }
        return result;
    }
}
