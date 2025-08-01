package ru.practicum.shareit.items;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.items.dto.CommentDto;
import ru.practicum.shareit.items.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(ItemDto itemDto, Long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, Long userId, Long id) {
        return patch("/" + id, userId, itemDto);
    }

    public ResponseEntity<Object> getItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItem(Integer id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> search(String text) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> addComment(Long id, Long userId, CommentDto commentDto) {
        return post("/" + id + "/comment", userId, commentDto);
    }
}
