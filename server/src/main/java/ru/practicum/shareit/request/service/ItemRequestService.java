package ru.practicum.shareit.request.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.out.ItemDtoShort;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.in.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.our.ItemRequestDtoOutput;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestDtoMapper itemRequestDtoMapper;
    private final ItemMapper itemMapper;

    public ItemRequestDtoOutput addRequest(final ItemRequestDto request, final int userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return itemRequestDtoMapper.toDtoOutput(repository.save(itemRequestDtoMapper.toItemRequest(request, requester)));
    }

    public List<ItemRequestDtoOutput> getRequests(final int requesterId) {
        List<ItemRequest> requests = repository.findAllByRequesterIdOrderByCreatedDesc(requesterId);
        if (requests.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> ids = requests
                .stream()
                .map(ItemRequest::getId)
                .toList();

        List<ItemDtoShort> items = itemRepository.findAllByRequestIdIn(ids)
                .stream()
                .map(itemMapper::toItemDtoShort)
                .toList();

        Map<Integer, List<ItemDtoShort>> map = items.stream()
                .collect(Collectors.groupingBy(ItemDtoShort::getId));

        return requests.stream()
                .map(obj -> itemRequestDtoMapper.toDtoOutput(obj, map.get(obj.getId())))
                .toList();
    }

    public List<ItemRequestDtoOutput> getAllRequests(int userId) {
        return repository.findAllByRequesterIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(itemRequestDtoMapper::toDtoOutput)
                .collect(Collectors.toList());
    }

    public ItemRequestDtoOutput getRequest(int requestId) {
        List<ItemDtoShort> items = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(itemMapper::toItemDtoShort)
                .toList();

        ItemRequest request = repository.findById(requestId).orElseThrow(()
                -> new NotFoundException("Request with id " + requestId + " not found"));

        return itemRequestDtoMapper.toDtoOutput(request, items);
    }
}
