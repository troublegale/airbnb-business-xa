package itmo.tg.airbnb_xa.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.tg.airbnb_xa.business.dto.HostJustificationRequestDTO;
import itmo.tg.airbnb_xa.business.dto.HostJustificationResponseDTO;
import itmo.tg.airbnb_xa.business.service.HostJustificationService;
import itmo.tg.airbnb_xa.security.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/justifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Host justifications",
        description = "Oppose guest complaints and retract penalties"
)
public class HostJustificationController {

    private final HostJustificationService hostJustificationService;
    private final UserService userService;

    @GetMapping("/my")
    @Operation(summary = "Get justifications published by you")
    public List<HostJustificationResponseDTO> getOwned(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "all") @Pattern(regexp = "all|pending|resolved") String filter) {
        return hostJustificationService.getOwned(userService.getCurrentUser(), page, pageSize, filter);
    }

    @PostMapping
    @Operation(summary = "Publish a justification")
    public HostJustificationResponseDTO publish(
            @RequestBody @Valid HostJustificationRequestDTO dto) {
        return hostJustificationService.create(dto, userService.getCurrentUser());
    }

}
