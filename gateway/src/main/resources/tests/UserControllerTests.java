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
import ru.practicum.shareit.users.UserClient;
import ru.practicum.shareit.users.UserController;
import ru.practicum.shareit.users.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class UserControllerTests {
    @Autowired
    ObjectMapper mapper;
    private UserDto userDto = new UserDto(
            null,
            null
    );
    @MockBean
    private UserClient userClient;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addUserWithNullFieldsTest() throws Exception {
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        userDto.setEmail("mail@mail.com");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        userDto.setEmail(null);
        userDto.setName("name");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verify(userClient, Mockito.never()).addUser(Mockito.any());
    }

    @Test
    public void addUserTest() throws Exception {
        userDto.setEmail("mail@mail.com");
        userDto.setName("name");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.atLeastOnce()).addUser(Mockito.any());
    }

    @Test
    public void updateUserTest() throws Exception {
        userDto.setEmail("mail@mail.com");
        userDto.setName("name");

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        userDto.setName(null);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        userDto.setName("name");
        userDto.setEmail(null);
        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserFailTest() throws Exception {
        userDto.setEmail("mail");
        userDto.setName("name");
        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        Mockito.verify(userClient, Mockito.never()).addUser(Mockito.any());
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.atLeastOnce()).deleteUser(1);
    }

    @Test
    public void getUserTest() throws Exception {
        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.atLeastOnce()).getUser(1);
    }

    @Test
    public void getUsersTest() throws Exception {
        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.atLeastOnce()).getUsers();
    }
}
