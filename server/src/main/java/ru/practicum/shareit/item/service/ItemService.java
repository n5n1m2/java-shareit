package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exceptions.CommentNoHavePermission;
import ru.practicum.shareit.error.exceptions.NoHavePermissionException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.in.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.in.CommentDto;
import ru.practicum.shareit.item.dto.out.ItemDtoOutput;
import ru.practicum.shareit.item.dto.out.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestRepository itemRequestRepository;

    public ItemDtoOutput addItem(ItemDto itemDto, Integer userId) {
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
             itemRequest =  itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("ItemRequest with id " + itemDto.getRequestId() + " not found"));
        }
        Item item = itemMapper.toItem(itemDto,
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found")),
                itemRequest);
        return itemMapper.toItemDtoOutput(itemRepository.save(item));
    }

    public ItemDtoOutput updateItem(ItemDto itemDto, Integer userId) {
        itemValidation(itemDto);

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest =  itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("ItemRequest with id " + itemDto.getRequestId() + " not found"));
        }

        Item item = itemMapper.toItem(itemDto,
                userRepository.findById(userId).orElseThrow(() ->
                        new NotFoundException("User with id " + userId + " not found")),
                itemRequest);
        Item oldItem = itemRepository.getItemById(item.getId());
        if (!Objects.equals(oldItem.getOwner().getId(), item.getOwner().getId())) {
            throw new NoHavePermissionException("Only owner can update this Item");
        }
        copyFields(oldItem, item);
        return itemMapper.toItemDtoOutput(itemRepository.save(oldItem));
    }

    public ItemDtoWithBookingAndComments getItemById(int itemId, int userId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        Item item;

        if (comments.isEmpty()) {
            item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
        } else {
            item = comments.getFirst().getItem();
        }

        boolean isOwner = item.getOwner().getId().equals(userId);

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (isOwner) {
            lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now());
            nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now());
        }

        return itemMapper.toItemDtoWithBookingAndComments(item,
                lastBooking,
                nextBooking,
                comments.stream()
                        .map(itemMapper::toCommentDto)
                        .collect(Collectors.toList()));
    }

    public List<ItemDtoWithBookingAndComments> getAllItems(int id) {
        List<Item> items = itemRepository.getItemByOwnerId(id);
        List<Integer> ids = items.stream().map(Item::getId).toList();
        List<Comment> comments = commentRepository.findAllByItemIdIn(ids);

        List<Booking> lastBookings = bookingRepository.findAllByItemIdInAndEndBeforeOrderByEndDesc(ids, LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findAllByItemIdInAndStartAfterOrderByStartAsc(ids, LocalDateTime.now());
        Map<Integer, List<Comment>> commentsByItemId = comments.stream().collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        Map<Integer, Booking> lastBookingsByItemId = lastBookings.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
        Map<Integer, Booking> nextBookingsByItemId = nextBookings.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));

        return items.stream()
                .map(item -> {
                    boolean isOwner = item.getOwner().getId().equals(id);
                    return itemMapper.toItemDtoWithBookingAndComments(item,
                            isOwner ? lastBookingsByItemId.get(item.getId()) : null,
                            isOwner ? nextBookingsByItemId.get(item.getId()) : null,
                            commentsByItemId.getOrDefault(item.getId(), List.of())
                                    .stream()
                                    .map(itemMapper::toCommentDto)
                                    .collect(Collectors.toList()));
                })
                .toList();
    }

    public List<ItemDtoOutput> searchItems(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return itemRepository.findAll()
                .stream()
                .filter(obj -> (
                        (obj.getName() != null && obj.getName().toLowerCase().contains(text.toLowerCase())) ||
                                (obj.getDescription() != null && obj.getDescription().toLowerCase().contains(text.toLowerCase()))
                ) && obj.getAvailable())
                .map(itemMapper::toItemDtoOutput)
                .collect(Collectors.toList());
    }

    public CommentDto addComment(CommentDto commentDto, Integer userId, Integer itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBeforeOrderByEndDesc(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new CommentNoHavePermission("User didn't rent this Item");
        }

        Comment comment = new Comment(
                null,
                commentDto.getText(),
                bookings.getFirst().getItem(),
                bookings.getFirst().getBooker(),
                commentDto.getCreated() == null ? LocalDateTime.now() : commentDto.getCreated()
        );
        return itemMapper.toCommentDto(commentRepository.save(comment));

    }

    private void itemValidation(ItemDto itemDto) {
        if (itemDto.getId() == null) {
            throw new IllegalArgumentException("Item id is null");
        }
        if (itemRepository.getItemById(itemDto.getId()) == null) {
            throw new NotFoundException("Item not found for id " + itemDto.getId());
        }
    }

    private void copyFields(Item old, Item newItem) {
        BeanUtils.copyProperties(newItem, old, getNotNullFields(newItem));
    }

    private String[] getNotNullFields(Object object) {
        BeanWrapper wrapper = new BeanWrapperImpl(object);
        PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
        Set<String> emptyFields = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Object value = wrapper.getPropertyValue(propertyDescriptor.getName());
            if (value == null) {
                emptyFields.add(propertyDescriptor.getName());
            }
        }
        return emptyFields.toArray(new String[0]);
    }
}
