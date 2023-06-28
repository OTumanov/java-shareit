package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestsRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storge.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRequestServiceTest {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestService requestService;
    private RequestsRepository requestRepository;

    private User user;
    private ItemRequest request;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        requestRepository = mock(RequestsRepository.class);
        UserService userService = mock(UserService.class);
        requestService = new ItemRequestServiceImpl(
                requestRepository,
                userService,
                itemRepository,
                userRepository
        );

        request = new ItemRequest(1L, "description", 1L, LocalDateTime.now(), new ArrayList<>());
        user = new User(1L, "name", "user@emali.com");
    }

    @Test
    public void createRequestTest() {
        PostRequestDto inputDto = new PostRequestDto(request.getDescription());

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        PostResponseRequestDto responseDto = requestService.addRequest(1L, inputDto);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getId());
        assertEquals(inputDto.getDescription(), responseDto.getDescription());
    }

    @Test
    void findAllByUserIdTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findItemRequestByRequesterOrderByCreatedDesc(any(Long.class))).thenReturn(new ArrayList<>());
        when(itemRepository.findAllByItemRequestId(any(Long.class))).thenReturn(new ArrayList<>());

        List<RequestWithItemsDto> result = requestService.findAllByUserId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findAll(any(Long.class), any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(itemRepository.findAllByItemRequestId(any(Long.class))).thenReturn(new ArrayList<>());

        List<RequestWithItemsDto> result = requestService.findAll(0, 20, 1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(request));
        when(itemRepository.findAllByItemRequestId(any(Long.class))).thenReturn(new ArrayList<>());

        RequestWithItemsDto result = requestService.findById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }
}