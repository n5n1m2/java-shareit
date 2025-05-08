package ru.practicum.shareit.item;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Add;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody @Validated({Add.class, Default.class}) Item item, @RequestHeader(userIdHeader) Integer userId) {
        log.debug("{}\n\n\n\n{} method addItem", item.toString(), userId);
        return itemService.addItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Integer id,
                              @RequestBody @Validated(Default.class) Item item,
                              @RequestHeader(userIdHeader) Integer userId) {
        log.debug("{}\n\n\n{} method updateItem", item.toString(), userId);
        item.setId(id);
        return itemService.updateItem(item, userId);

    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(userIdHeader) Integer userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Integer id) {
        return itemService.getItemById(id);
    }
}
