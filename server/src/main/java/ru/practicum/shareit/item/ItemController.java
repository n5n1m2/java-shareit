package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.in.ItemDto;
import ru.practicum.shareit.item.dto.in.CommentDto;
import ru.practicum.shareit.item.dto.out.ItemDtoOutput;
import ru.practicum.shareit.item.dto.out.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDtoOutput addItem(@RequestBody ItemDto item, @RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemService.addItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDtoOutput updateItem(@PathVariable Integer id,
                                    @RequestBody ItemDto item,
                                    @RequestHeader(USER_ID_HEADER) Integer userId) {
        item.setId(id);
        return itemService.updateItem(item, userId);

    }

    @GetMapping
    public List<ItemDtoWithBookingAndComments> getItems(@RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDtoWithBookingAndComments getItem(@PathVariable Integer id, @RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDtoOutput> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto comment,
                                 @RequestHeader(USER_ID_HEADER) Integer userId,
                                 @PathVariable Integer itemId) {
        return itemService.addComment(comment, userId, itemId);
    }
}
