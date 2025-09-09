package itmo.tg.airbnb_xa.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.tg.airbnb_xa.business.dto.GuestComplaintRequestDTO;
import itmo.tg.airbnb_xa.business.dto.GuestComplaintResponseDTO;
import itmo.tg.airbnb_xa.business.exception.exceptions.BookingAlreadyExpiredException;
import itmo.tg.airbnb_xa.business.service.GuestComplaintService;
import itmo.tg.airbnb_xa.security.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guest-complaints")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Guest complaints",
        description = "Publish complaints on advertisements during bookings"
)
public class GuestComplaintController {

    private final GuestComplaintService guestComplaintService;
    private final UserService userService;

    private final KafkaTemplate<String, GuestComplaintResponseDTO> kafkaTemplate;

    @GetMapping("/my")
    @Operation(summary = "Get complaints published by you")
    public List<GuestComplaintResponseDTO> getOwned(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "all") @Pattern(regexp = "all|pending|resolved") String filter) {
        return guestComplaintService.getOwned(userService.getCurrentUser(), page, pageSize, filter);
    }

    @PostMapping
    @Operation(summary = "Publish a complaint")
    public GuestComplaintResponseDTO publish(
            @RequestBody @Valid GuestComplaintRequestDTO guestComplaintRequestDTO) {
        var response = guestComplaintService.create(guestComplaintRequestDTO, userService.getCurrentUser());
        kafkaTemplate.send("guest-complaints", response);
        return response;
    }

    @ExceptionHandler(BookingAlreadyExpiredException.class)
    public ResponseEntity<String> handleBookingAlreadyExpiredException(BookingAlreadyExpiredException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

}
