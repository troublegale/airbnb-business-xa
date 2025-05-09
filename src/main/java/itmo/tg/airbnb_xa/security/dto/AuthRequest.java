package itmo.tg.airbnb_xa.security.dto;

import jakarta.validation.constraints.NotNull;

public record AuthRequest(@NotNull String username, @NotNull String password) {
}
