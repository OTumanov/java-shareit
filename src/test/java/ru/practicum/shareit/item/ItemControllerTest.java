package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utils.ItemMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private static final String BASE_PATH_ITEMS = "/items";
    public static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemDto itemDtoWithEmptyName = ItemDto.builder()
            .id(1L)
            .name("")
            .description("testDescription")
            .available(true)
            .requestId(1L)
            .build();
    private final ItemDto itemDtoWithEmptyDescription = ItemDto.builder()
            .id(1L)
            .name("testItem")
            .description("")
            .available(true)
            .requestId(1L)
            .build();
    private final ItemDto itemDtoWithoutAvailable = ItemDto.builder()
            .id(1L)
            .name("testItem")
            .description("")
            .requestId(1L)
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("testItem")
            .description("testDescription")
            .available(true)
            .requestId(1L)
            .build();
    private final ItemDto itemDto1 = ItemDto.builder()
            .id(1L)
            .name("testName")
            .description("testName")
            .available(true)
            .requestId(null)
            .nextBooking(null)
            .lastBooking(null)
            .comments(null)
            .build();
    private final ItemDto itemDto2 = ItemDto.builder()
            .id(2L)
            .name("testName2")
            .description("testDescription2")
            .available(true)
            .requestId(null)
            .nextBooking(null)
            .lastBooking(null)
            .comments(null)
            .build();

    private final List<ItemDto> itemsDtoList = List.of(itemDto1, itemDto2);
    private final CommentDto commentDto = CommentDto.builder().id(1L).text("testText").authorName("testName").build();
    private final CommentDto invalidCommentDto = CommentDto.builder().id(1L).text("").authorName("testName").build();

    @Test
    void createValidItem_shouldReturnJSONAndStatus200() throws Exception {
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDto);
        mvc.perform(post(BASE_PATH_ITEMS)
                        .header(HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void createInvalidItemWithEmptyName_shouldReturnStatus400() throws Exception {
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDtoWithEmptyName);
        mvc.perform(post(BASE_PATH_ITEMS)
                        .header(HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemDtoWithEmptyName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createInvalidItemWithEmptyDescription_shouldReturnStatus400() throws Exception {
        when(itemService.createItem(any(), anyLong()))
                .thenReturn(itemDtoWithEmptyDescription);
        mvc.perform(post(BASE_PATH_ITEMS)
                        .header(HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemDtoWithEmptyDescription))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createInvalidItemWithoutAvailable_shouldReturnStatus400() throws Exception {
        when(itemService.createItem(any(), anyLong()))
                .thenReturn(itemDtoWithoutAvailable);
        mvc.perform(post(BASE_PATH_ITEMS)
                        .header(HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemDtoWithoutAvailable))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(get(BASE_PATH_ITEMS + "/{id}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getAllUserItems() throws Exception {
        when(itemService.findAllItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemsDtoList);
        mvc.perform(get(BASE_PATH_ITEMS)
                        .header(HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemsDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(itemsDtoList.get(0).getName(),
                        itemsDtoList.get(1).getName())))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(itemsDtoList.get(0).getDescription(),
                        itemsDtoList.get(1).getDescription())));
    }

    @Test
    void editItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(ItemMapper.toItem(itemDto));
        mvc.perform(patch(BASE_PATH_ITEMS + "/{id}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void searchAvailableItems() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(ItemMapper.toItem(itemDto)));
        mvc.perform(get(BASE_PATH_ITEMS + "/search?text=TesT")
                        .header(HEADER_USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("testItem")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("testDescription")));
    }

    @Test
    void addValidComment() throws Exception {
        when(itemService.createComment(any(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post(BASE_PATH_ITEMS + "/1/comment")
                        .header(HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void addInvalidComment_shouldReturnStatus400() throws Exception {
        when(itemService.createComment(any(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post(BASE_PATH_ITEMS + "/1/comment")
                        .header(HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(invalidCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}