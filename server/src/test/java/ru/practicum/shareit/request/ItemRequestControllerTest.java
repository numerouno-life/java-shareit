package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;

    private final Long id = 1L;

    private final ItemRequestDtoOut requestDtoOut = ItemRequestDtoOut.builder()
            .id(id)
            .description("Description")
            .requestorId(2L)
            .created(LocalDateTime.of(2023, 1, 1, 1, 1, 1))
            .build();

    @SneakyThrows
    @Test
    void addNewRequest_validRequest_returnsCreatedRequest() {
        when(requestService.addNewRequest(anyLong(), any())).thenReturn(requestDtoOut);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDtoOut)))
                .andExpect(jsonPath("$.id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$.requestorId", is(requestDtoOut.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.created", is("2023-01-01T01:01:01")));

        verify(requestService, times(1)).addNewRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getOwnerRequests_validUser_returnsRequestList() {
        when(requestService.getOwnerRequests(anyLong())).thenReturn(List.of(requestDtoOut));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDtoOut))))
                .andExpect(jsonPath("$[0].id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestorId", is(requestDtoOut.getRequestorId()), Long.class))
                .andExpect(jsonPath("$[0].created", is("2023-01-01T01:01:01")));

        verify(requestService, times(1)).getOwnerRequests(anyLong());
    }

    @SneakyThrows
    @Test
    void getOwnerRequests_missingHeader_returnsBadRequest() {
        mockMvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getOwnerRequests(anyLong());
    }

    @SneakyThrows
    @Test
    void getAllRequests_validParams_returnsRequestList() {
        int from = 0;
        int size = 10;

        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(requestDtoOut));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDtoOut))))
                .andExpect(jsonPath("$[0].id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestorId", is(requestDtoOut.getRequestorId()), Long.class))
                .andExpect(jsonPath("$[0].created", is("2023-01-01T01:01:01")));

        verify(requestService, times(1)).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getRequestByIdWithItems_requestNotFound_returnsNotFound() {
        when(requestService.getRequestByIdWithItems(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Request not found")));
    }


    @SneakyThrows
    @Test
    void getRequestByIdWithItems_validRequest_returnsRequestAndItems() {
        when(requestService.getRequestByIdWithItems(anyLong(), anyLong())).thenReturn(requestDtoOut);

        mockMvc.perform(get("/requests/{1}", "1")
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(requestService, times(1)).getRequestByIdWithItems(anyLong(), anyLong());
    }

}
