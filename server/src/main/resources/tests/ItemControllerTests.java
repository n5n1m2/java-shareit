import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.in.BookingDtoInput;
import ru.practicum.shareit.booking.dto.out.BookingDtoOutput;
import ru.practicum.shareit.item.dto.in.CommentDto;
import ru.practicum.shareit.item.dto.in.ItemDto;
import ru.practicum.shareit.item.dto.out.ItemDtoOutput;
import ru.practicum.shareit.item.dto.out.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@ActiveProfiles("test")
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ShareItServer.class)
public class ItemControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private ItemDto testItem;
    private UserDto testUser;
    private CommentDto testComment;

    @BeforeEach
    public void setup() {
        restTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory()
        );

        itemRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new UserDto(null, "test user", "user@test.com");
        UserDto createdUser = restTemplate.postForObject("/users", testUser, UserDto.class);
        testUser.setId(createdUser.getId());

        testItem = new ItemDto();
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setAvailable(true);

        testComment = new CommentDto();
        testComment.setText("Test comment");
    }

    @Test
    public void createItemTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, testUser.getId().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(testItem, headers);

        ItemDtoOutput createdItem = restTemplate.postForObject(
                "/items",
                requestEntity,
                ItemDtoOutput.class
        );

        assertNotNull(createdItem);
        assertEquals(testItem.getName(), createdItem.getName());
        assertEquals(testItem.getDescription(), createdItem.getDescription());
        assertNotNull(createdItem.getId());
    }

    @Test
    public void updateItemTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, testUser.getId().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(testItem, headers);

        ItemDtoOutput createdItem = restTemplate.postForObject(
                "/items",
                requestEntity,
                ItemDtoOutput.class
        );

        ItemDto updateData = new ItemDto();
        updateData.setName("Updated Name");

        requestEntity = new HttpEntity<>(updateData, headers);

        ItemDtoOutput updatedItem = restTemplate.patchForObject(
                "/items/" + createdItem.getId(),
                requestEntity,
                ItemDtoOutput.class
        );

        assertEquals("Updated Name", updatedItem.getName());
        assertEquals(createdItem.getDescription(), updatedItem.getDescription());
    }

    @Test
    public void getItemByIdTest() {
        ItemDtoOutput createdItem = restTemplate.postForObject(
                "/items",
                testItem,
                ItemDtoOutput.class
        );

        ItemDtoWithBookingAndComments retrievedItem = restTemplate.getForObject(
                "/items/" + createdItem.getId(),
                ItemDtoWithBookingAndComments.class,
                testUser.getId()
        );

        assertNotNull(retrievedItem);
        assertEquals(createdItem.getId(), retrievedItem.getId());
        assertEquals(createdItem.getName(), retrievedItem.getName());
    }

    @Test
    public void getAllItemsTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, testUser.getId().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(testItem, headers);

        ItemDtoOutput createdItem = restTemplate.postForObject(
                "/items",
                requestEntity,
                ItemDtoOutput.class
        );

        requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<ItemDtoWithBookingAndComments>> response = restTemplate.exchange(
                "/items",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        List<ItemDtoWithBookingAndComments> items = response.getBody();
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(testItem.getName(), items.get(0).getName());
    }

    @Test
    public void searchItemsTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, testUser.getId().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(testItem, headers);

        ItemDtoOutput createdItem = restTemplate.postForObject(
                "/items",
                requestEntity,
                ItemDtoOutput.class
        );

        ResponseEntity<List<ItemDtoOutput>> response = restTemplate.exchange(
                "/items/search?text=Test",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<ItemDtoOutput> items = response.getBody();
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(testItem.getName(), items.get(0).getName());
    }

    @Test
    public void addCommentTest() throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, testUser.getId().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(testItem, headers);

        ItemDtoOutput createdItem = restTemplate.postForObject(
                "/items",
                requestEntity,
                ItemDtoOutput.class
        );
        BookingDtoInput input = new BookingDtoInput(null, LocalDateTime.now().minusMinutes(15), LocalDateTime.now().minusMinutes(10), createdItem.getId(), testUser.getId(), BookingStatus.APPROVED);
        HttpEntity<BookingDtoInput> requestBooking = new HttpEntity<>(input, headers);
        BookingDtoOutput bookingDto = restTemplate.postForObject("/bookings", requestBooking, BookingDtoOutput.class);

        HttpEntity<CommentDto> requestEntity1 = new HttpEntity<>(testComment, headers);
        CommentDto createdComment = restTemplate.postForObject(
                "/items/" + createdItem.getId() + "/comment",
                requestEntity1,
                CommentDto.class
        );

        assertNotNull(createdComment);
        assertEquals(testComment.getText(), createdComment.getText());
        assertNotNull(createdComment.getCreated());
    }

    @Test
    public void createItem_ShouldReturnBadRequest_WhenDescriptionIsEmpty() {
        testItem.setDescription("");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/items",
                testItem,
                String.class
        );

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createItem_ShouldReturnBadRequest_WhenAvailableIsNull() {
        testItem.setAvailable(null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/items",
                testItem,
                String.class
        );

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void getItemWithBookingsAndCommentsTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, testUser.getId().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        ItemDtoOutput createdItem = restTemplate.postForObject(
                "/items",
                new HttpEntity<>(testItem, headers),
                ItemDtoOutput.class
        );

        BookingDtoInput bookingInput = new BookingDtoInput();
        bookingInput.setItemId(createdItem.getId());
        bookingInput.setStart(LocalDateTime.now().minusMinutes(35));
        bookingInput.setEnd(LocalDateTime.now().minusMinutes(30));
        bookingInput.setStatus(BookingStatus.WAITING);

        BookingDtoOutput createdBooking = restTemplate.postForObject(
                "/bookings",
                new HttpEntity<>(bookingInput, headers),
                BookingDtoOutput.class
        );

        HttpHeaders ownerHeaders = new HttpHeaders();
        ownerHeaders.set(USER_ID_HEADER, testUser.getId().toString());

        restTemplate.patchForObject(
                "/bookings/" + createdBooking.getId() + "?approved=true",
                new HttpEntity<>(ownerHeaders),
                BookingDtoOutput.class
        );

        CommentDto commentInput = new CommentDto();
        commentInput.setText("Great item!");

        CommentDto createdComment = restTemplate.postForObject(
                "/items/" + createdItem.getId() + "/comment",
                new HttpEntity<>(commentInput, headers),
                CommentDto.class
        );

        ResponseEntity<ItemDtoWithBookingAndComments> response = restTemplate.exchange(
                "/items/" + createdItem.getId(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ItemDtoWithBookingAndComments.class
        );

        ItemDtoWithBookingAndComments itemWithDetails = response.getBody();
        assertNotNull(itemWithDetails);
        assertEquals(createdItem.getId(), itemWithDetails.getId());

        assertNotNull(itemWithDetails.getLastBooking());
        assertEquals(createdBooking.getId(), itemWithDetails.getLastBooking().id());

        assertFalse(itemWithDetails.getComments().isEmpty());
        assertEquals("Great item!", itemWithDetails.getComments().get(0).getText());
    }
}