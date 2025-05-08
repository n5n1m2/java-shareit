package ru.practicum.shareit.item;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Add;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody @Validated({Add.class, Default.class}) ItemDto item, @RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemService.addItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Integer id,
                              @RequestBody @Validated(Default.class) ItemDto item,
                              @RequestHeader(USER_ID_HEADER) Integer userId) {
        item.setId(id);
        return itemService.updateItem(item, userId);

    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Integer id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
