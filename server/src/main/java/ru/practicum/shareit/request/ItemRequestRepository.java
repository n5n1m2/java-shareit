package ru.practicum.shareit.request;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(int id);

    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(int id);
}
