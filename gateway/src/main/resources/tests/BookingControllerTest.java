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
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Headers.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient bookingClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    void bookItemValidationFailTest() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto();

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingClient, Mockito.never()).bookItem(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void bookItemValidTest() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.atLeastOnce()).bookItem(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getBookingTest() throws Exception {
        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.atLeastOnce()).getBooking(1L, 1L);
    }

    @Test
    void getBookingsTest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.atLeastOnce()).getBookings(Mockito.eq(1L), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void getMyBookingsTest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.atLeastOnce()).getMyBookings(Mockito.eq(1), Mockito.any());
    }

    @Test
    void updateBookingTest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.atLeastOnce()).updateBooking(1, true, 1);
    }
}
