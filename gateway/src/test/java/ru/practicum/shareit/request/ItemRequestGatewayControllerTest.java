package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRequestGatewayController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestGatewayControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private final ItemRequestDtoRequest dto = ItemRequestDtoRequest.builder()
            .description("test")
            .build();

    @SneakyThrows
    @Test
    void addNewRequest_ShouldReturnOk() {
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemRequestClient, times(1)).addNewRequest(1L, dto);
    }

    @SneakyThrows
    @Test
    void getOwnerRequests_ShouldReturnOk() {
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-id", "1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemRequestClient, times(1)).getOwnerRequests(1L);
    }

    @SneakyThrows
    @Test
    void getAllRequests_ShouldReturnOk() {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemRequestClient, times(1)).getAllRequests(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void getRequestByIdWithItems_ShouldReturnOk() {
        mvc.perform(get("/requests/{1}", "1")
                        .header("X-Sharer-User-id", "1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemRequestClient, times(1)).getRequestByIdWithItems(1L, 1L);
    }

    @Test
    @SneakyThrows
    void createRequestWithoutDescription_ShouldReturnBadRequest() {
        ItemRequestDtoRequest dtoWithoutDescription = ItemRequestDtoRequest.builder().build();

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dtoWithoutDescription))
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void createRequestWithEmptyDescription_ShouldReturnBadRequest() {
        ItemRequestDtoRequest dtoWithEmptyDescription = ItemRequestDtoRequest.builder()
                .description("")
                .build();

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dtoWithEmptyDescription))
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
