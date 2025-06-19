import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ShareItServer.class)
public class UserControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;
    private UserDto testUser;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        restTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory()
        );
        testUser = new UserDto(null, "test", "test@test.com");
        userRepository.deleteAll();
    }

    @Test
    public void createUserTest() {
        UserDto createdUser = restTemplate.postForObject("/users", testUser, UserDto.class);

        assertNotNull(createdUser);
        assertEquals(testUser.getName(), createdUser.getName());
        assertEquals(testUser.getEmail(), createdUser.getEmail());
        assertNotNull(createdUser.getId());
    }

    @Test
    public void getUserByIdTest() {
        UserDto createdUser = restTemplate.postForObject("/users", testUser, UserDto.class);
        UserDto retrievedUser = restTemplate.getForObject("/users/" + createdUser.getId(), UserDto.class);

        assertNotNull(retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals(createdUser.getName(), retrievedUser.getName());
        assertEquals(createdUser.getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void getAllUsersTest() {
        UserDto createdUser = restTemplate.postForObject("/users", testUser, UserDto.class);

        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
                "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<UserDto> users = response.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(createdUser.getId(), users.getFirst().getId());
    }

    @Test
    public void updateUserTest() {
        UserDto createdUser = restTemplate.postForObject("/users", testUser, UserDto.class);
        createdUser.setEmail("updated@mail.com");

        UserDto updatedUser = restTemplate.patchForObject(
                "/users/" + createdUser.getId(),
                createdUser,
                UserDto.class
        );

        assertEquals("updated@mail.com", updatedUser.getEmail());
        assertEquals(createdUser.getName(), updatedUser.getName());
    }

    @Test
    public void deleteUserTest() {
        UserDto createdUser = restTemplate.postForObject("/users", testUser, UserDto.class);

        restTemplate.delete("/users/" + createdUser.getId());

        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
                "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<UserDto> users = response.getBody();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }
}