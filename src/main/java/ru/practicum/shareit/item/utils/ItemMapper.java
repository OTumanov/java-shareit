package ru.practicum.shareit.item.utils;

import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

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

    public static ItemDto toItemDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        BookingInItemDto last;
        BookingInItemDto next;

        if (lastBooking == null) {
            last = null;
        } else {
            last = BookingInItemDto.builder()
                    .id(lastBooking.getId())
                    .start(lastBooking.getStart())
                    .end(lastBooking.getEnd())
                    .bookerId(lastBooking.getBooker().getId())
                    .status(lastBooking.getStatus())
                    .build();
        }
        if (nextBooking == null) {
            next = null;
        } else {
            next = BookingInItemDto.builder()
                    .id(nextBooking.getId())
                    .start(nextBooking.getStart())
                    .end(nextBooking.getEnd())
                    .bookerId(nextBooking.getBooker().getId())
                    .status(nextBooking.getStatus())
                    .build();
        }

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

    public static ItemDto toDto(Item item, List<Comment> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (comments != null) {
            itemDto.setComments(CommentMapper.toCommentDetailedDtoList(comments));
        }
        itemDto.setRequestId(item.getItemRequestId());
        return itemDto;
    }


    public static List<ItemDto> toItemDtoList(List<Item> items) {

        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(toItemDto(item));
        }
        return result;
    }

    public static ItemInRequestDto toRequestItemDto(Item item) {
        ItemInRequestDto dto = new ItemInRequestDto();
        dto.setId(item.getOwnerId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getItemRequestId());
        dto.setOwner(item.getOwnerId());
        return dto;
    }

    public static List<ItemInRequestDto> toRequestItemDtoList(List<Item> items) {
        List<ItemInRequestDto> result = new ArrayList<>();

        for (Item item : items) {
            ItemInRequestDto dto = ItemInRequestDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .requestId(item.getItemRequestId())
                    .owner(item.getOwnerId())
                    .build();
            result.add(dto);
        }
        return result;
    }

    public static Item toModel(ItemDto itemDto, Long ownerId) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(ownerId);
        item.setItemRequestId(itemDto.getRequestId());
        return item;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(itemDto.getId());
        item.setItemRequestId(itemDto.getRequestId());
        return item;
    }
}
