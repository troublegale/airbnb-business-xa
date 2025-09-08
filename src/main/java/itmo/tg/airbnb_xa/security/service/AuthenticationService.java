package itmo.tg.airbnb_xa.security.service;

import itmo.tg.airbnb_xa.security.dto.AuthRequest;
import itmo.tg.airbnb_xa.security.dto.AuthResponse;
import itmo.tg.airbnb_xa.security.model.Role;
import itmo.tg.airbnb_xa.security.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserService userService;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse signUp(AuthRequest request) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();
        userService.create(user);
        log.info("User #{} registered in the system", user.getId());
        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getEmail(), token, Role.ROLE_USER);
    }

    public AuthResponse signIn(AuthRequest request) {
        if (!userService.existsByUsername(request.email())) {
            throw new UsernameNotFoundException(String.format("User %s not found", request.email()));
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(), request.password()
        ));
        UserDetails userDetails = userService.getUserDetailsService().loadUserByUsername(request.email());
        User user = (User) userDetails;
        Role role = user.getRole();
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(user.getEmail(), token, role);
    }

}