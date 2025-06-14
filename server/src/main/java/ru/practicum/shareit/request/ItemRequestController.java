package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.in.ItemRequestDto;
import ru.practicum.shareit.request.dto.our.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDtoOutput create(@RequestBody final ItemRequestDto itemRequestDto,
                                       @RequestHeader(USER_ID_HEADER) final int userId) {
        return service.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> getAllForUser(@RequestHeader(USER_ID_HEADER) final int userId) {
        return service.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> getAll(@RequestHeader(USER_ID_HEADER) final int userId) {
        return service.getAllRequests(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDtoOutput get(@PathVariable final int id) {
        return service.getRequest(id);
    }

}
