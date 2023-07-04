package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.utils.RequestMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class RequestMapperTest {
    public static final long ID = 1L;
    private ItemRequest request;
    private PostRequestDto postRequestDto;
    private ItemRepository itemRepository;

    @BeforeEach
    public void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(1L)
                .created(LocalDateTime.now())
                .build();

        postRequestDto = new PostRequestDto("description");
    }

    @Test
    public void toModelTest() {
        ItemRequest result = RequestMapper.toModel(postRequestDto, ID);

        assertNotNull(result);
        assertEquals(ID, result.getRequester());
        assertEquals(postRequestDto.getDescription(), result.getDescription());
    }

    @Test
    public void toPostResponseDtoTest() {
        PostResponseRequestDto result = RequestMapper.toPostResponseDto(request);

        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getCreated(), result.getCreated());
    }

    @Test
    public void toRequestWithItemsDtoTest() {
        RequestWithItemsDto result = RequestMapper.toRequestWithItemsDto(request, new ArrayList<>());

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCreated(), result.getCreated());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    public void toRequestWithItemsDtoListTest() {
        List<ItemRequest> requests = Collections.singletonList(request);
        Page<ItemRequest> page = new PageImpl<>(requests);

        List<RequestWithItemsDto> fromList = RequestMapper.toRequestWithItemsDtoList(page, itemRepository);
        List<RequestWithItemsDto> fromPage = RequestMapper.toRequestWithItemsDtoList(requests, itemRepository);

        assertNotNull(fromList);
        assertNotNull(fromPage);
        assertEquals(request.getId(), fromList.get(0).getId());
        assertEquals(request.getId(), fromPage.get(0).getId());
        assertTrue(fromList.get(0).getItems().isEmpty());
        assertTrue(fromPage.get(0).getItems().isEmpty());
    }
}