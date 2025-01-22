package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    private final Long id = 1L;

    private final ItemDtoOut itemDtoOut = ItemDtoOut.builder()
            .id(id)
            .name("Name")
            .description("Description")
            .available(false)
            .requestId(1L)
            .build();

    private final ItemDtoIn itemDtoIn = ItemDtoIn.builder()
            .id(id)
            .name("Name")
            .description("Description")
            .available(false)
            .requestId(1L)
            .build();

    private final CommentDtoOut commentDtoOut = CommentDtoOut.builder()
            .id(1L)
            .text("Comment text")
            .authorName("Author name")
            .created(LocalDateTime.of(2000, 1, 1, 1, 1))
            .build();

    private final CommentDtoIn commentDtoIn = CommentDtoIn.builder()
            .id(1L)
            .text("Comment text")
            .build();


    @SneakyThrows
    @Test
    void addNewItem_validItem_returnNewItem() {
        when(itemService.addNewItem(anyLong(), any())).thenReturn(itemDtoOut);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)))
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDtoOut.getRequestId()), Long.class));

        verify(itemService, times(1)).addNewItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getItemById_validUser_returnUserItem() {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoOut);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItem_validUser_returnUpdatedItem() {
        when(itemService.updateItem(eq(1L), anyLong(), any(ItemDtoIn.class))).thenReturn(itemDtoOut);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDtoOut.getRequestId()), Long.class));

        verify(itemService, times(1)).updateItem(anyLong(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getOwnerItems_validUser_returnOwnerItems() {
        when(itemService.getOwnerItems(anyLong())).thenReturn(List.of(itemDtoOut));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoOut))))
                .andExpect(jsonPath("$[0].id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDtoOut.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDtoOut.getRequestId()), Long.class));

        verify(itemService, times(1)).getOwnerItems(anyLong());
    }

    @SneakyThrows
    @Test
    void searchItemByText_validText_returnItemByText() {
        String searchText = "item";
        when(itemService.searchItemByText(eq(searchText))).thenReturn(List.of(itemDtoOut));

        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoOut))))
                .andExpect(jsonPath("$[0].id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDtoOut.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDtoOut.getRequestId()), Long.class));

        verify(itemService, times(1)).searchItemByText(searchText);
    }

    @SneakyThrows
    @Test
    void saveComment_validComment_returnComment() {
        when(itemService.saveComment(anyLong(), any(CommentDtoIn.class), anyLong())).thenReturn(commentDtoOut);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoOut.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDtoOut.getAuthorName()), String.class))
                .andExpect(jsonPath("$.created").isNotEmpty());

        verify(itemService, times(1)).saveComment(eq(1L), any(CommentDtoIn.class), eq(1L));
    }
}