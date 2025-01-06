package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemGatewayController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemGatewayControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    private final ItemDtoRequest dto = ItemDtoRequest.builder()
            .name("Test name")
            .available(true)
            .requestId(1L)
            .description("Test description")
            .build();

    private final CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
            .text("Nice item!")
            .build();

    @SneakyThrows
    @Test
    void addNewItem_ShouldReturnOk() {
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient, times(1)).addNewItem(eq(1L), eq(dto));
    }

    @SneakyThrows
    @Test
    void getItemById_ShouldReturnOk() {
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient, times(1)).getItemById(eq(1L), eq(1L));
    }

    @SneakyThrows
    @Test
    void updateItem_ShouldReturnOk() {
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient, times(1)).updateItem(eq(1L), eq(1L), eq(dto));
    }

    @SneakyThrows
    @Test
    void getOwnerItems_ShouldReturnOk() {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient, times(1)).getOwnerItems(eq(1L));
    }

    @SneakyThrows
    @Test
    void searchItemByText_ShouldReturnOk() {
        mvc.perform(get("/items/search")
                        .param("text", "Test")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient, times(1)).searchItemByText(eq("Test"));
    }

    @SneakyThrows
    @Test
    void aadComment_ShouldReturnOk() {
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDtoRequest))
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemClient, times(1)).saveComment(eq(1L), eq(commentDtoRequest), eq(1L));
    }


}
