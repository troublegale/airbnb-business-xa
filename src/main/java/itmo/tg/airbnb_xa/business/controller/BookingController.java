package itmo.tg.airbnb_xa.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.tg.airbnb_xa.business.dto.BookingRequestDTO;
import itmo.tg.airbnb_xa.business.dto.BookingResponseDTO;
import itmo.tg.airbnb_xa.business.exception.exceptions.AdvertisementBlockedException;
import itmo.tg.airbnb_xa.business.exception.exceptions.BookOwnAdvertisementException;
import itmo.tg.airbnb_xa.business.exception.exceptions.BookingDatesConflictException;
import itmo.tg.airbnb_xa.business.exception.exceptions.InvalidBookingDatesException;
import itmo.tg.airbnb_xa.business.service.BookingService;
import itmo.tg.airbnb_xa.security.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Bookings",
        description = "Create bookings for advertisements, cancel bookings"
)
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all bookings")
    public List<BookingResponseDTO> getAll(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "false") @Valid Boolean active) {
        return bookingService.getAll(page, pageSize, active);
    }

    @GetMapping("/my")
    @Operation(summary = "Get bookings created by you")
    public List<BookingResponseDTO> getOwned(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "false") @Valid Boolean active) {
        return bookingService.getOwned(userService.getCurrentUser(), page, pageSize, active);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a booking by id")
    public BookingResponseDTO get(@PathVariable Long id) {
        return bookingService.get(id);
    }

    @PostMapping
    @Operation(summary = "Create a booking")
    public BookingResponseDTO create(
            @RequestBody @Valid BookingRequestDTO dto) {
        return bookingService.create(dto, userService.getCurrentUser());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a booking")
    public String cancel(@PathVariable Long id) {
        return bookingService.cancel(id, userService.getCurrentUser());
    }

    @ExceptionHandler(BookOwnAdvertisementException.class)
    public ResponseEntity<String> handleBookOwnAdvertisementException(BookOwnAdvertisementException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(BookingDatesConflictException.class)
    public ResponseEntity<String> handleBookingDatesConflictException(BookingDatesConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidBookingDatesException.class)
    public ResponseEntity<String> handleInvalidBookingDatesException(InvalidBookingDatesException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AdvertisementBlockedException.class)
    public ResponseEntity<String> handleAdvertisementBlockedException(AdvertisementBlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}
