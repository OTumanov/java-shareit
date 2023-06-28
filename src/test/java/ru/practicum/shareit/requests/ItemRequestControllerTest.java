package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    public void createRequestTest() throws Exception {
        PostRequestDto requestDto = new PostRequestDto("description");
        long requestId = 1;
        LocalDateTime creationDate = LocalDateTime.now();
        PostResponseRequestDto responseDto = createPostResponseDto(requestId, requestDto, creationDate);

        when(itemRequestService.addRequest(any(Long.class), any(PostRequestDto.class))).thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).addRequest(any(Long.class), any(PostRequestDto.class));
    }

    @Test
    public void findAllTest() throws Exception {
        when(itemRequestService.getAllRequest(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "20")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService, times(1)).getAllRequest(any(Long.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void findByIdTest() throws Exception {
        RequestWithItemsDto dto = new RequestWithItemsDto();
        dto.setId(1L);
        dto.setDescription("description");
        dto.setItems(Collections.emptyList());

        when(itemRequestService.findById(any(Long.class), any(Long.class))).thenReturn(dto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription()), String.class))
                .andExpect(jsonPath("$.items", is(dto.getItems()), List.class));

        verify(itemRequestService, times(1)).findById(any(Long.class), any(Long.class));
    }

    @Test
    public void findAllByUserIdTest() throws Exception {
        when(itemRequestService.findAllByUserId(any(Long.class))).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService, times(1)).findAllByUserId(any(Long.class));
    }

    private PostResponseRequestDto createPostResponseDto(Long id, PostRequestDto dto, LocalDateTime date) {
        PostResponseRequestDto responseDto = new PostResponseRequestDto();
        responseDto.setDescription(dto.getDescription());
        responseDto.setId(1L);
        responseDto.setId(id);
        responseDto.setCreated(date);
        return responseDto;
    }
}