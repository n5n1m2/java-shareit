import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.practicum.shareit.requests.ItemRequestDto;
import ru.practicum.shareit.requests.RequestClient;
import ru.practicum.shareit.requests.RequestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Headers.USER_ID_HEADER;

@WebMvcTest(controllers = RequestController.class)
@ContextConfiguration(classes = ShareItGateway.class)
class RequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    private ItemRequestDto itemRequest = new ItemRequestDto(null);
    @MockBean
    private RequestClient client;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createTest() throws Exception {
        itemRequest.setDescription("description");
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(client, Mockito.atLeastOnce()).addRequest(Mockito.any(), Mockito.anyLong());
    }

    @Test
    public void createFailTest() throws Exception {
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        Mockito.verify(client, Mockito.never()).addRequest(Mockito.any(), Mockito.anyLong());
    }

    @Test
    public void getAllForUserTest() throws Exception {
        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(client, Mockito.atLeastOnce()).getAllForUser(1L);
    }

    @Test
    public void getAllTest() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(client, Mockito.atLeastOnce()).getAll(1L);
    }

    @Test
    public void getTest() throws Exception {
        mockMvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(client, Mockito.atLeastOnce()).getById(1L);
    }
}