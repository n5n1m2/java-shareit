package ru.practicum.shareit.item.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoShort {
    private Integer id;
    private String name;
    private Integer ownerId;
}
