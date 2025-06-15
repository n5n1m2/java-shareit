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
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.in.ItemDto;
import ru.practicum.shareit.item.dto.out.ItemDtoOutput;
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
public class BookingControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private BookingDtoInput testBooking;
    private Integer ownerId;
    private Integer bookerId;
    private Integer itemId;

    @BeforeEach
    public void setup() {
        restTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory()
        );

        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();

        ownerId = createUser("owner@test.com", "Owner");
        bookerId = createUser("booker@test.com", "Booker");
        itemId = createItem(ownerId);

        testBooking = new BookingDtoInput();
        testBooking.setItemId(itemId);
        testBooking.setStart(LocalDateTime.now().plusHours(1));
        testBooking.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void createBookingTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, bookerId.toString());

        BookingDtoOutput createdBooking = restTemplate.postForObject(
                "/bookings",
                new HttpEntity<>(testBooking, headers),
                BookingDtoOutput.class
        );

        assertNotNull(createdBooking);
        assertEquals(testBooking.getItemId(), createdBooking.getItem().getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
    }

    @Test
    public void approveBookingTest() {
        BookingDtoOutput booking = createTestBooking();

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, ownerId.toString());

        BookingDtoOutput approvedBooking = restTemplate.patchForObject(
                "/bookings/" + booking.getId() + "?approved=true",
                new HttpEntity<>(headers),
                BookingDtoOutput.class
        );

        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    public void getBookingByIdTest() {
        BookingDtoOutput createdBooking = createTestBooking();

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, bookerId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<BookingDtoOutput> response = restTemplate.exchange(
                "/bookings/" + createdBooking.getId(),
                HttpMethod.GET,
                requestEntity,
                BookingDtoOutput.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BookingDtoOutput retrievedBooking = response.getBody();
        assertNotNull(retrievedBooking);
        assertEquals(createdBooking.getId(), retrievedBooking.getId());
    }

    @Test
    public void getUserBookingsTest() {
        createTestBooking();

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, bookerId.toString());

        ResponseEntity<List<BookingDtoOutput>> response = restTemplate.exchange(
                "/bookings?state=ALL",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );

        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void getOwnerBookingsTest() {
        createTestBooking();

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, ownerId.toString());

        ResponseEntity<List<BookingDtoOutput>> response = restTemplate.exchange(
                "/bookings/owner?state=ALL",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );

        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void createBookingFailTest() {
        ItemDto update = new ItemDto();
        update.setAvailable(false);

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, ownerId.toString());
        restTemplate.patchForObject(
                "/items/" + itemId,
                new HttpEntity<>(update, headers),
                ItemDtoOutput.class
        );
        headers.set(USER_ID_HEADER, bookerId.toString());
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/bookings",
                new HttpEntity<>(testBooking, headers),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private BookingDtoOutput createTestBooking() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, bookerId.toString());

        return restTemplate.postForObject(
                "/bookings",
                new HttpEntity<>(testBooking, headers),
                BookingDtoOutput.class
        );
    }

    private Integer createUser(String email, String name) {
        UserDto userDto = new UserDto(null, name, email);
        UserDto createdUser = restTemplate.postForObject(
                "/users",
                new HttpEntity<>(userDto),
                UserDto.class
        );
        return createdUser.getId();
    }

    private Integer createItem(Integer ownerId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, ownerId.toString());

        ItemDtoOutput createdItem = restTemplate.postForObject(
                "/items",
                new HttpEntity<>(itemDto, headers),
                ItemDtoOutput.class
        );
        return createdItem.getId();
    }
}