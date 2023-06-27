package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;

import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utils.UserMapper;

import javax.swing.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;

    private final UserDto userDto = new UserDto(1L, "testUser", "test@email.com");
    private final UserDto requester = new UserDto(2L, "test1User", "test1@email.com");

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("nameTest")
            .description("descriptionTest")
            .available(true)
            .build();
    private final ItemDto itemDtoUpdate = ItemDto.builder()
            .name("updateNameTest")
            .description("UpdateDescriptionTest")
            .available(false)
            .build();
    private final ItemRequestDto itemRequestToCreate = ItemRequestDto.builder()
            .id(1L)
            .description("descriptionTest")
            .requester(requester)

            .build();
    private final ItemDto itemDtoToRequest = ItemDto.builder()
            .id(1L)
            .name("nameTest")
            .description("descriptionTest")
            .available(true)
            .requestId(1L)
            .build();


    @Nested
    class ItemTests {
//        @Test
//        void createItemWithRequest() {
//
//            User createdUser = userService.createUser(UserMapper.toUser(userDto));
////            createdUser.setId(userDto.getId());
//
//            User createdRequester = userService.createUser(UserMapper.toUser(userDto));
////            createdRequester.setId(requester.getId());
//
//            ItemRequestDto createdItemRequest = itemRequestService.addRequest(createdRequester.getId(), itemRequestToCreate);
//            System.out.println(createdItemRequest);
//
//            ItemDto itemDto = itemService.createItem(itemDtoToRequest, 1L);
//            System.out.println(itemDto);
//            Item item = itemRepository.findById(itemDto.getId()).orElse(new Item());
//            System.out.println(item);
//
//            assertThat(item.getId(), equalTo(1L));
//            assertThat(item.getName(), equalTo(itemDtoToRequest.getName()));
//            assertThat(item.getDescription(), equalTo(itemDtoToRequest.getDescription()));
//            assertThat(item.getAvailable(), equalTo(itemDtoToRequest.getAvailable()));
//            assertThat(item.getOwnerId(), equalTo(createdUser.getId()));
//            assertThat(item.getItemRequestId(), equalTo(createdItemRequest.getId()));
//        }

        @Test
        void createItem() {
            User createdUser = userService.createUser(UserMapper.toUser(userDto));
            itemService.createItem(itemDto, 1L);
            Item item = itemRepository.findById(itemDto.getId()).orElse(new Item());

            assertThat(item.getId(), equalTo(1L));
            assertThat(item.getName(), equalTo(itemDto.getName()));
            assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
            assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
            assertThat(item.getOwnerId(), equalTo(createdUser.getId()));
        }

        @Test
        void getItemById() {
            userService.createUser(UserMapper.toUser(userDto));
            itemService.createItem(itemDto, 1L);
            ItemDto itemReturned = itemService.getItemById(1L, 1L);

            assertThat(itemReturned.getId(), equalTo(1L));
            assertThat(itemReturned.getName(), equalTo(itemDto.getName()));
            assertThat(itemReturned.getDescription(), equalTo(itemDto.getDescription()));
            assertThat(itemReturned.getAvailable(), equalTo(itemDto.getAvailable()));
        }
//
//        @Test
//        void getAllUserItems() {
//            userService.addUser(userDto);
//            itemService.addItem(itemDto, 1L);
//            List<ItemDto> itemReturned = itemService.getAllUsersItems(1L, 0, 10);
//
//            assertThat(itemReturned.get(0).getId(), equalTo(1L));
//            assertThat(itemReturned.get(0).getName(), equalTo(itemDto.getName()));
//            assertThat(itemReturned.get(0).getDescription(), equalTo(itemDto.getDescription()));
//            assertThat(itemReturned.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
//        }
//
//        @Test
//        void updateItems() {
//            UserDto createdUser = userService.addUser(userDto);
//            itemService.addItem(itemDto, 1L);
//            itemService.updateItem(itemDtoUpdate, 1L, 1L);
//            Item item = itemRepository.findById(itemDto.getId()).orElse(new Item());
//
//            assertThat(item.getId(), equalTo(1L));
//            assertThat(item.getName(), equalTo(itemDtoUpdate.getName()));
//            assertThat(item.getDescription(), equalTo(itemDtoUpdate.getDescription()));
//            assertThat(item.getAvailable(), equalTo(itemDtoUpdate.getAvailable()));
//            assertThat(item.getOwnerId(), equalTo(createdUser.getId()));
//        }
//
//        @Test
//        void searchAvailableItems() {
//            userService.addUser(userDto);
//            itemService.addItem(itemDto, 1L);
//            itemService.addItem(itemDtoUpdate, 1L);
//            List<ItemDto> itemReturned = itemService.searchAvailableItems("Test", 0, 10);
//
//            assertThat(itemReturned, hasSize(1));
//            assertThat(itemReturned.get(0).getId(), equalTo(1L));
//            assertThat(itemReturned.get(0).getName(), equalTo(itemDto.getName()));
//            assertThat(itemReturned.get(0).getDescription(), equalTo(itemDto.getDescription()));
//            assertThat(itemReturned.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
//        }
//
//        @Test
//        void getOwnerId() {
//            UserDto user = userService.addUser(userDto);
//            itemService.addItem(itemDto, 1L);
//
//            assertThat(itemService.getOwnerId(user.getId()), equalTo(user.getId()));
//        }
//    }
//
//    @Nested
//    class CommentTests {
//        private final BookingDtoShort booking = BookingDtoShort.builder()
//                .itemId(1L)
//                .start(LocalDateTime.now().minusHours(2))
//                .end(LocalDateTime.now().minusHours(1))
//                .build();
//        private final CommentDto comment = CommentDto.builder()
//                .text("text")
//                .build();
//
//        @Test
//        void addComment() {
//            UserDto owner = userService.addUser(userDto);
//            UserDto commentator = userService.addUser(requester);
//            ItemDto item = itemService.addItem(itemDto, 1L);
//            BookingDto bookingDto = bookingService.addBooking(booking, commentator.getId());
//            bookingService.approve(bookingDto.getId(), owner.getId(), true);
//            itemService.addComment(item.getId(), commentator.getId(), comment);
//
//            ItemDto itemReturned = itemService.getItemById(item.getId(), owner.getId());
//            assertThat(itemReturned.getComments(), hasSize(1));
//            assertThat(itemReturned.getComments().get(0).getItem(), equalTo(item));
//            assertThat(itemReturned.getComments().get(0).getAuthorName(), equalTo(commentator.getName()));
//            assertThat(itemReturned.getComments().get(0).getText(), equalTo(comment.getText()));
//        }
    }
}