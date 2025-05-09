package itmo.tg.airbnb_xa.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.tg.airbnb_xa.business.dto.FineDTO;
import itmo.tg.airbnb_xa.business.service.FineService;
import itmo.tg.airbnb_xa.security.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fines")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Fines",
        description = "See fines assigned to you"
)
public class FineController {

    private final FineService fineService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all assigned fines (admin only endpoint)")
    public List<FineDTO> getAll(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "false") @Valid Boolean active) {
        return fineService.getAll(page, pageSize, active);
    }

    @GetMapping("/my")
    @Operation(summary = "Get fines assigned to you")
    public List<FineDTO> getAssigned(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "20") @Positive Integer pageSize,
            @RequestParam(defaultValue = "true") @Valid Boolean active) {
        return fineService.getAssignedTo(userService.getCurrentUser(), page, pageSize, active);
    }

}
