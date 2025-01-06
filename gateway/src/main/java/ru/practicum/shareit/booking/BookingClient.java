package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingById(long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> addNewBooking(long userId, BookItemRequestDto bookItemRequestDto) {
        return post("", userId, bookItemRequestDto);
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, boolean isApproved) {
        return patch("/" + bookingId + "?approved={approved}",
                userId, Map.of("approved", isApproved), null);
    }


    public ResponseEntity<Object> getAllUserBooking(Long userId, State state) {
        return get("?state={state}", userId, Map.of("state", state.name()));
    }


    public ResponseEntity<Object> getAllOwnerBooking(Long ownerId, State state) {
        return get("?state={state}", ownerId, Map.of("state", state.name()));
    }
}