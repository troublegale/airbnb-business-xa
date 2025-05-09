package itmo.tg.airbnb_xa.security.dto;

import itmo.tg.airbnb_xa.security.model.Role;

public record AuthResponse(String username, String token, Role role) {
}
