package ru.practicum.shareit.item;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(createRestTemplate(serverUrl, builder));
    }

    private static RestTemplate createRestTemplate(String serverUrl, RestTemplateBuilder builder) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> requestFactory)
                .build();
    }

    public ResponseEntity<Object> addNewItem(Long userId, ItemDtoRequest itemDtoRequest) {
        return post("", userId, itemDtoRequest);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/" + itemId, userId, null);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDtoRequest itemDtoRequest) {
        return patch("/" + itemId, userId, itemDtoRequest);
    }

    public ResponseEntity<Object> getOwnerItems(Long userId) {
        return get("", userId, null);
    }

    public ResponseEntity<Object> searchItemByText(String text) {
        return get("/search", null, Map.of("text", text));
    }

    public ResponseEntity<Object> saveComment(Long itemId, CommentDtoRequest commentDtoRequest, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDtoRequest);
    }
}
