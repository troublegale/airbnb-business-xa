package itmo.tg.airbnb_xa.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.tg.airbnb_xa.business.dto.GuestComplaintResponseDTO;
import itmo.tg.airbnb_xa.business.dto.HostDamageComplaintResponseDTO;
import itmo.tg.airbnb_xa.business.dto.HostJustificationResponseDTO;
import itmo.tg.airbnb_xa.business.exception.exceptions.TicketAlreadyResolvedException;
import itmo.tg.airbnb_xa.business.service.GuestComplaintService;
import itmo.tg.airbnb_xa.business.service.HostDamageComplaintService;
import itmo.tg.airbnb_xa.business.service.HostJustificationService;
import itmo.tg.airbnb_xa.security.service.UserService;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Administration",
        description = "Manage tickets: guest complaints, host damage complaints and host justifications"
)
public class AdminController {

    private final GuestComplaintService guestComplaintService;
    private final HostDamageComplaintService hostDamageComplaintService;
    private final HostJustificationService hostJustificationService;
    private final UserService userService;

    @GetMapping("/guest-complaints")
    @Operation(summary = "Get all guest complaints")
    public List<GuestComplaintResponseDTO> getGuestComplaints(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "all") @Pattern(regexp = "all|pending|resolved") String filter) {
        return guestComplaintService.getList(page, pageSize, filter);
    }

    @GetMapping("/guest-complaints/{id}")
    @Operation(summary = "Get a guest complaint by id")
    public GuestComplaintResponseDTO getGuestComplaint(@PathVariable Long id) {
        return guestComplaintService.get(id);
    }

    @PostMapping("/guest-complaints/{id}")
    @Operation(summary = "Approve a guest complaint")
    public GuestComplaintResponseDTO approveGuestComplaint(@PathVariable Long id) {
        return guestComplaintService.approve(id, userService.getCurrentUser());
    }

    @DeleteMapping("/guest-complaints/{id}")
    @Operation(summary = "Reject a guest complaint")
    public GuestComplaintResponseDTO rejectGuestComplaint(@PathVariable Long id) {
        return guestComplaintService.reject(id, userService.getCurrentUser());
    }

    @GetMapping("/damage-complaints")
    @Operation(summary = "Get all damage complaints")
    public List<HostDamageComplaintResponseDTO> getDamageComplaints(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "all") @Pattern(regexp = "all|pending|resolved") String filter) {
        return hostDamageComplaintService.getList(page, pageSize, filter);
    }

    @GetMapping("/damage-complaints/{id}")
    @Operation(summary = "Get a damage complaint by id")
    public HostDamageComplaintResponseDTO getDamageComplaint(@PathVariable Long id) {
        return hostDamageComplaintService.get(id);
    }

    @PostMapping("/damage-complaints/{id}")
    @Operation(summary = "Approve a damage complaint")
    public HostDamageComplaintResponseDTO approveHostDamageComplaint(@PathVariable Long id) {
        return hostDamageComplaintService.approve(id, userService.getCurrentUser());
    }

    @DeleteMapping("/damage-complaints/{id}")
    @Operation(summary = "Reject a damage complaint")
    public HostDamageComplaintResponseDTO rejectHostDamageComplaint(@PathVariable Long id) {
        return hostDamageComplaintService.reject(id, userService.getCurrentUser());
    }

    @GetMapping("/justifications")
    @Operation(summary = "Get all host justifications")
    public List<HostJustificationResponseDTO> getHostJustifications(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "all") @Pattern(regexp = "all|pending|resolved") String filter) {
        return hostJustificationService.getList(page, pageSize, filter);
    }

    @GetMapping("/justifications/{id}")
    @Operation(summary = "Get a host justification by id")
    public HostJustificationResponseDTO getHostJustification(@PathVariable Long id) {
        return hostJustificationService.get(id);
    }

    @PostMapping("/justifications/{id}")
    @Operation(summary = "Approve a host justification")
    public HostJustificationResponseDTO approveHostJustification(@PathVariable Long id) {
        return hostJustificationService.approve(id, userService.getCurrentUser());
    }

    @DeleteMapping("/justifications/{id}")
    @Operation(summary = "Reject a host justification")
    public HostJustificationResponseDTO rejectHostJustification(@PathVariable Long id) {
        return hostJustificationService.reject(id, userService.getCurrentUser());
    }

    @ExceptionHandler(TicketAlreadyResolvedException.class)
    public ResponseEntity<String> handleTicketAlreadyResolvedException(TicketAlreadyResolvedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}
