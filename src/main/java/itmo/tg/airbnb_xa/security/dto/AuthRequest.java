package itmo.tg.airbnb_xa.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(@NotNull @Email String email, @NotNull String password) {
}
