package itmo.tg.airbnb_xa.security.dto;

import itmo.tg.airbnb_xa.security.model.Role;

public record AuthResponse(String email, String token, Role role) {
}
