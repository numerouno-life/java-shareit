package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private BookingDtoIn bookingDtoIn;
    private BookingDtoOut bookingDtoOut;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        itemDto = createItemDto();
        userDto = createUserDto();
        bookingDtoIn = createBookingDtoIn(itemDto.getId());
        bookingDtoOut = createBookingDtoOut(itemDto, userDto);
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("ItemDto name")
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("UserDto name")
                .email("user@example.com")
                .build();
    }

    private BookingDtoIn createBookingDtoIn(Long itemId) {
        return BookingDtoIn.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .itemId(itemId)
                .build();
    }

    private BookingDtoOut createBookingDtoOut(ItemDto item, UserDto user) {
        return BookingDtoOut.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(Booking.BookingStatus.APPROVED)
                .state(State.ALL)
                .item(item)
                .booker(user)
                .build();
    }

    private String asJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    @Test
    void addNewBooking_validBooking_returnNewBooking() throws Exception {
        when(bookingService.addNewBooking(anyLong(), any(BookingDtoIn.class))).thenReturn(bookingDtoOut);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(asJson(bookingDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(bookingDtoOut)));
    }

    @Test
    void approveBooking_validRequest_returnUpdatedBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), any(Boolean.class))).thenReturn(bookingDtoOut);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(bookingDtoOut)));
    }

    @Test
    void getBookingById_validRequest_returnBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDtoOut);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(bookingDtoOut)));
    }

    @Test
    void getAllUserBooking_validRequest_returnListOfBookings() throws Exception {
        when(bookingService.getAllUserBooking(anyLong(), any(State.class))).thenReturn(List.of(bookingDtoOut));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(List.of(bookingDtoOut))));
    }

    @Test
    void getAllOwnerBooking_validRequest_returnListOfBookings() throws Exception {
        when(bookingService.getAllOwnerBooking(anyLong(), any(State.class))).thenReturn(List.of(bookingDtoOut));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(List.of(bookingDtoOut))));
    }
}