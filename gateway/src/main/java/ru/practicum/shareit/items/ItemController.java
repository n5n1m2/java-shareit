package ru.practicum.shareit.items;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.items.dto.CommentDto;
import ru.practicum.shareit.items.dto.ItemDto;

import static ru.practicum.shareit.constants.Headers.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Validated ItemDto item, @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.addItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable Long id,
                                             @RequestBody ItemDto item,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.updateItem(item, userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Integer id, @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getItem(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Validated CommentDto comment,
                                             @RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId) {
        return itemClient.addComment(itemId, userId, comment);
    }
}
