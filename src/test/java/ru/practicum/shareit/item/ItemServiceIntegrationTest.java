package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utils.UserMapper;

import javax.transaction.Transactional;
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
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    private final UserDto userDto = new UserDto(1L, "testUser", "test@email.com");

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

    @Nested
    class ItemTests {
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

        @Test
        void getAllUserItems() {
            userService.createUser(UserMapper.toUser(userDto));
            itemService.createItem(itemDto, 1L);
            List<ItemDto> itemReturned = itemService.findAllItemsByUserId(1L, 0, 10);

            assertThat(itemReturned.get(0).getId(), equalTo(1L));
            assertThat(itemReturned.get(0).getName(), equalTo(itemDto.getName()));
            assertThat(itemReturned.get(0).getDescription(), equalTo(itemDto.getDescription()));
            assertThat(itemReturned.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
        }

        @Test
        void updateItems() {
            User createdUser = userService.createUser(UserMapper.toUser(userDto));
            itemService.createItem(itemDto, 1L);
            itemService.updateItem(1L, 1L, ItemMapper.toItem(itemDtoUpdate));
            Item item = itemRepository.findById(itemDto.getId()).orElse(new Item());

            assertThat(item.getId(), equalTo(1L));
            assertThat(item.getName(), equalTo(itemDtoUpdate.getName()));
            assertThat(item.getDescription(), equalTo(itemDtoUpdate.getDescription()));
            assertThat(item.getAvailable(), equalTo(itemDtoUpdate.getAvailable()));
            assertThat(item.getOwnerId(), equalTo(createdUser.getId()));
        }

        @Test
        void searchAvailableItems() {
            userService.createUser(UserMapper.toUser(userDto));
            itemService.createItem(itemDto, 1L);
            itemService.createItem(itemDtoUpdate, 1L);
            List<ItemDto> itemReturned = itemService.searchAvailableItems("Test", 0, 10);

            assertThat(itemReturned, hasSize(1));
            assertThat(itemReturned.get(0).getId(), equalTo(1L));
            assertThat(itemReturned.get(0).getName(), equalTo(itemDto.getName()));
            assertThat(itemReturned.get(0).getDescription(), equalTo(itemDto.getDescription()));
            assertThat(itemReturned.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
        }

        @Test
        void getOwnerId() {
            User user = userService.createUser(UserMapper.toUser(userDto));
            itemService.createItem(itemDto, 1L);

            assertThat(itemService.getOwnerId(user.getId()), equalTo(user.getId()));
        }
    }
}