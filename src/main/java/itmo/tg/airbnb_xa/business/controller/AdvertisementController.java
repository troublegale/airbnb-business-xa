package itmo.tg.airbnb_xa.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.tg.airbnb_xa.business.dto.*;
import itmo.tg.airbnb_xa.business.exception.exceptions.ActiveBookingsException;
import itmo.tg.airbnb_xa.business.service.AdvertisementBlockService;
import itmo.tg.airbnb_xa.business.service.AdvertisementService;
import itmo.tg.airbnb_xa.business.service.GuestComplaintService;
import itmo.tg.airbnb_xa.security.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/advertisements")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Advertisements",
        description = "Create and update advertisements, see bookings and complaints"
)
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final AdvertisementBlockService advertisementBlockService;
    private final UserService userService;
    private final GuestComplaintService guestComplaintService;

    @GetMapping
    @Operation(summary = "Get all advertisements")
    public List<AdvertisementResponseDTO> getAll(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "false") @Valid Boolean active
    ) {
        return advertisementService.getAll(page, pageSize, active);
    }

    @GetMapping("/my")
    @Operation(summary = "Get advertisements hosted by you")
    public List<AdvertisementResponseDTO> getOwned(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "false") @Valid Boolean active) {
        return advertisementService.getOwned(userService.getCurrentUser(), page, pageSize, active);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an advertisement by id")
    public AdvertisementResponseDTO get(@PathVariable Long id) {
        return advertisementService.get(id);
    }

    @GetMapping("/{id}/complaints")
    @Operation(summary = "Get complaints for an advertisement")
    public List<GuestComplaintResponseDTO> getComplaintForAdvert(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "true") @Valid Boolean approved) {
        return guestComplaintService.getForAdvertisement(id, page, pageSize, approved, userService.getCurrentUser());
    }

    @GetMapping("/{id}/blocks")
    @Operation(summary = "Get blocks for an advertisement")
    public List<AdvertisementBlockDTO> getBlocksForAdvert(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize) {
        return advertisementBlockService.getForAdvert(id, page, pageSize, userService.getCurrentUser());
    }

    @GetMapping("/{id}/bookings")
    @Operation(summary = "Get bookings for an advertisement")
    public List<BookingResponseDTO> getBookings(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "false") @Valid Boolean active) {
        return advertisementService.getBookings(id, page, pageSize, active);
    }

    @PostMapping
    @Operation(summary = "Create an advertisement")
    public AdvertisementResponseDTO create(
            @RequestBody @Valid AdvertisementRequestDTO dto) {
        return advertisementService.create(dto, userService.getCurrentUser());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an advertisement")
    public AdvertisementResponseDTO update(
            @PathVariable Long id,
            @RequestBody @Valid AdvertisementRequestDTO dto) {
        return advertisementService.update(id, dto, userService.getCurrentUser());
    }

    @ExceptionHandler(ActiveBookingsException.class)
    public ResponseEntity<String> handleActiveBookingsException(ActiveBookingsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}
