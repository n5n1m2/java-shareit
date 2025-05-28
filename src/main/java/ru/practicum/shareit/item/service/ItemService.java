package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exceptions.CommentNoHavePermission;
import ru.practicum.shareit.error.exceptions.NoHavePermissionException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.dto.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        Item item = ItemDtoMapper.toItem(itemDto,
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found")));
        return ItemDtoMapper.toDto(itemRepository.save(item));
    }

    public ItemDto updateItem(ItemDto itemDto, Integer userId) {
        itemValidation(itemDto);
        Item item = ItemDtoMapper.toItem(itemDto,
                userRepository.findById(userId).orElseThrow(() ->
                        new NotFoundException("User with id " + userId + " not found")));
        Item oldItem = ItemDtoMapper.toItem(itemRepository.getItemById(item.getId()));
        if (!Objects.equals(oldItem.getOwner().getId(), item.getOwner().getId())) {
            throw new NoHavePermissionException("Only owner can update this Item");
        }
        copyFields(oldItem, item);
        return ItemDtoMapper.toDto(itemRepository.save(oldItem));
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

        return ItemDtoMapper.toDtoWithBooking(item,
                lastBooking,
                nextBooking,
                comments.stream()
                        .map(CommentDtoMapper::toDto)
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
                    return ItemDtoMapper.toDtoWithBooking(item,
                            isOwner ? lastBookingsByItemId.get(item.getId()) : null,
                            isOwner ? nextBookingsByItemId.get(item.getId()) : null,
                            commentsByItemId.getOrDefault(item.getId(), List.of())
                                    .stream()
                                    .map(CommentDtoMapper::toDto)
                                    .collect(Collectors.toList()));
                })
                .toList();
    }

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return itemRepository.findAll()
                .stream()
                .filter(obj -> (
                        (obj.getName() != null && obj.getName().toLowerCase().contains(text.toLowerCase())) ||
                                (obj.getDescription() != null && obj.getDescription().toLowerCase().contains(text.toLowerCase()))
                ) && obj.getAvailable())
                .map(ItemDtoMapper::toDto)
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
        return CommentDtoMapper.toDto(commentRepository.save(comment));

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
