import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.in.ItemRequestDto;
import ru.practicum.shareit.request.dto.our.ItemRequestDtoOutput;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ShareItServer.class)
public class ItemRequestControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private UserDto testUser;
    private ItemRequestDto testRequest;
    private HttpHeaders headers;

    @BeforeEach
    public void setup() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new UserDto(null, "Test User", "test@user.com");
        UserDto createdUser = restTemplate.postForObject("/users", testUser, UserDto.class);
        testUser.setId(createdUser.getId());

        testRequest = new ItemRequestDto();
        testRequest.setDescription("Test");

        headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", testUser.getId().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void createRequestTest() {
        ResponseEntity<ItemRequestDtoOutput> response = restTemplate.exchange(
                "/requests",
                HttpMethod.POST,
                new HttpEntity<>(testRequest, headers),
                ItemRequestDtoOutput.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemRequestDtoOutput createdRequest = response.getBody();
        assertNotNull(createdRequest);
        assertEquals(testRequest.getDescription(), createdRequest.getDescription());
        assertNotNull(createdRequest.getId());
    }

    @Test
    public void getAllForUserTest() {
        ItemRequestDtoOutput createdRequest = restTemplate.postForObject(
                "/requests",
                new HttpEntity<>(testRequest, headers),
                ItemRequestDtoOutput.class
        );

        ResponseEntity<List<ItemRequestDtoOutput>> response = restTemplate.exchange(
                "/requests",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ItemRequestDtoOutput> requests = response.getBody();
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(createdRequest.getId(), requests.get(0).getId());
    }

    @Test
    public void getAllRequestsTest() {
        UserDto anotherUser = new UserDto(null, "Another User", "another@user.com");
        UserDto createdAnotherUser = restTemplate.postForObject("/users", anotherUser, UserDto.class);

        ItemRequestDtoOutput createdRequest = restTemplate.postForObject(
                "/requests",
                new HttpEntity<>(testRequest, headers),
                ItemRequestDtoOutput.class
        );

        HttpHeaders anotherUserHeaders = new HttpHeaders();
        anotherUserHeaders.set("X-Sharer-User-Id", createdAnotherUser.getId().toString());

        ResponseEntity<List<ItemRequestDtoOutput>> response = restTemplate.exchange(
                "/requests/all",
                HttpMethod.GET,
                new HttpEntity<>(anotherUserHeaders),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ItemRequestDtoOutput> requests = response.getBody();
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(createdRequest.getId(), requests.get(0).getId());
    }

    @Test
    public void getRequestByIdTest() {
        ItemRequestDtoOutput createdRequest = restTemplate.postForObject(
                "/requests",
                new HttpEntity<>(testRequest, headers),
                ItemRequestDtoOutput.class
        );

        ResponseEntity<ItemRequestDtoOutput> response = restTemplate.exchange(
                "/requests/" + createdRequest.getId(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ItemRequestDtoOutput.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemRequestDtoOutput request = response.getBody();
        assertNotNull(request);
        assertEquals(createdRequest.getId(), request.getId());
        assertEquals(testRequest.getDescription(), request.getDescription());
    }

    @Test
    public void getNonExistentRequestTest() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/requests/999",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}