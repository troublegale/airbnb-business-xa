package itmo.tg.airbnb_xa.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.tg.airbnb_xa.business.dto.HostDamageComplaintRequestDTO;
import itmo.tg.airbnb_xa.business.dto.HostDamageComplaintResponseDTO;
import itmo.tg.airbnb_xa.business.service.HostDamageComplaintService;
import itmo.tg.airbnb_xa.security.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/damage-complaints")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Host damage complaints",
        description = "Publish complaints on guest to receive compensation"
)
public class HostDamageComplaintController {

    private final HostDamageComplaintService hostDamageComplaintService;
    private final UserService userService;

    @GetMapping("/my")
    @Operation(summary = "Get damage complaints published by you")
    public List<HostDamageComplaintResponseDTO> getOwned(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "all") @Pattern(regexp = "all|pending|resolved") String filter) {
        return hostDamageComplaintService.getOwned(userService.getCurrentUser(), page, pageSize, filter);
    }

    @PostMapping

    @Operation(summary = "Publish a damage complaint")
    public HostDamageComplaintResponseDTO publish(
            @RequestBody @Valid HostDamageComplaintRequestDTO dto) {
        return hostDamageComplaintService.create(dto, userService.getCurrentUser());
    }

}
