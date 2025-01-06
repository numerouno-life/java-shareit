package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.State;

import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingGatewayController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerGatewayTest {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    private final BookItemRequestDto dto = BookItemRequestDto.builder()
            .start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now().minusDays(1))
            .itemId(1L)
            .build();

    @SneakyThrows
    @Test
    void addNewBooking_ShouldForwardRequestToBookingClient() {
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient, times(1)).addNewBooking(anyLong(), any(BookItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void approveBooking_ShouldForwardRequestToBookingClient() {
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient, times(1)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void getBookingById_ShouldReturnOk() {
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient, times(1)).getBookingById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllUserBooking_ShouldReturnOk() {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient, times(1)).getAllUserBooking(anyLong(), any(State.class));
    }

    @SneakyThrows
    @Test
    void getAllOwnerBooking_ShouldReturnOk() {
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingClient, times(1)).getAllOwnerBooking(anyLong(), any(State.class));
    }

    @SneakyThrows
    @Test
    void allLogMessagesShouldBeCorrect() {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        verify(bookingClient, times(1)).getAllUserBooking(1L, State.ALL);
        verifyNoMoreInteractions(bookingClient);
    }


}
