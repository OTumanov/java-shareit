package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @MockBean
    BookingService bookingService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mvc;

    @Test
    public void createBookingTest() throws Exception {
        BookingPostDto inputDto = generateInputDto();
        BookingPostResponseDto responseDto = generatePostResponseDto(1L, inputDto);

        when(bookingService.createBooking(any(BookingPostDto.class), any(Long.class))).thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(inputDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.item", is(responseDto.getItem()), Item.class));

        verify(bookingService, times(1)).createBooking(any(BookingPostDto.class), any(Long.class));
    }

    @Test
    public void patchBookingTest() throws Exception {
        BookingResponseDto responseDto = generateResponseDto(1L);

        when(bookingService.patchBooking(any(Long.class), any(Boolean.class), any(Long.class))).thenReturn(responseDto);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));

        verify(bookingService, times(1)).patchBooking(any(Long.class), any(Boolean.class), any(Long.class));
    }

    @Test
    public void findAllBookingsTest() throws Exception {
        when(bookingService.findAllByBooker(any(String.class), any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1)).findAllByBooker(any(String.class), any(Long.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void findAllTest() throws Exception {
        when(bookingService
                .findAllByItemOwner(any(String.class), any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1)).findAllByItemOwner(any(String.class), any(Long.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void findByIdTest() throws Exception {
        BookingDetailedDto responseDto = generateDetailedDto(1L);

        when(bookingService.findById(any(Long.class), any(Long.class))).thenReturn(BookingMapper.toModel(responseDto));

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));

        verify(bookingService, times(1)).findById(any(Long.class), any(Long.class));
    }

    private BookingPostDto generateInputDto() {
        BookingPostDto dto = new BookingPostDto();
        dto.setId(1L);
        return dto;
    }

    private BookingPostResponseDto generatePostResponseDto(Long id, BookingPostDto inputDto) {
        BookingPostResponseDto result = new BookingPostResponseDto();
        Item item = new Item();
        item.setId(id);
        item.setName("item");
        item.setDescription("item description");
        item.setAvailable(true);

        result.setId(id);
        result.setItem(item);
        result.setStart(LocalDateTime.now());
        result.setEnd(LocalDateTime.now().plusDays(7));

        return result;
    }

    private BookingResponseDto generateResponseDto(Long id) {
        BookingResponseDto result = new BookingResponseDto();
        result.setId(id);
        result.setBooker(new User());
        result.setItem(new Item());
        result.setName("Item name");
        return result;
    }

    private BookingDetailedDto generateDetailedDto(Long id) {
        BookingDetailedDto result = new BookingDetailedDto();
        result.setId(id);
        result.setName("name");
        result.setBooker(new User());
        result.setItem(new Item());
        result.setStart(LocalDateTime.now());
        result.setEnd(LocalDateTime.now().plusDays(7));
        return result;
    }
}