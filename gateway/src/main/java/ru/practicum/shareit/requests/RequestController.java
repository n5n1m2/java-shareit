package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class RequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated final ItemRequestDto itemRequestDto,
                                         @RequestHeader(USER_ID_HEADER) final Long userId) {
        return requestClient.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUser(@RequestHeader(USER_ID_HEADER) final Long userId) {
        return requestClient.getAllForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) final long userId) {
        return requestClient.getAll(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable final Long id) {
        return requestClient.getById(id);
    }

}
