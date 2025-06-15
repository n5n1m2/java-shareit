import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.items.ItemClient;
import ru.practicum.shareit.items.ItemController;
import ru.practicum.shareit.items.dto.CommentDto;
import ru.practicum.shareit.items.dto.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Headers.USER_ID_HEADER;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemClient itemClient;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addItemWithNullFieldsTest() throws Exception {
        ItemDto itemDto = new ItemDto();

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        itemDto.setName("name");
        itemDto.setDescription(null);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        itemDto.setDescription("description");
        itemDto.setAvailable(null);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemClient, Mockito.never()).addItem(Mockito.any(), Mockito.anyLong());
    }

    @Test
    public void addItemTest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1)).addItem(Mockito.any(), Mockito.eq(1L));
    }

    @Test
    public void updateItemTest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("updated name");

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        itemDto.setName(null);
        itemDto.setDescription("updated description");

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(2)).updateItem(Mockito.any(), Mockito.eq(1L), Mockito.eq(1L));
    }

    @Test
    public void getItemsTest() throws Exception {
        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1)).getItems(1L);
    }

    @Test
    public void getItemTest() throws Exception {
        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1)).getItem(1, 1L);
    }

    @Test
    public void searchItemsTest() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "search text")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1)).search("search text");
    }

    @Test
    public void addCommentWithNullTextTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(null);

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemClient, Mockito.never()).addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
    }

    @Test
    public void addCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment text");

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1)).addComment(1L, 1L, commentDto);
    }
}