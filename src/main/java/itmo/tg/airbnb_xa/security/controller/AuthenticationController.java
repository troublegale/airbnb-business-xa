package itmo.tg.airbnb_xa.security.controller;

import itmo.tg.airbnb_xa.security.dto.AuthRequest;
import itmo.tg.airbnb_xa.security.dto.AuthResponse;
import itmo.tg.airbnb_xa.security.exception.UsernameTakenException;
import itmo.tg.airbnb_xa.security.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public AuthResponse signUp(@RequestBody @Valid AuthRequest authRequest) {
        return authenticationService.signUp(authRequest);
    }

    @PostMapping("/sign-in")
    public AuthResponse signIn(@RequestBody @Valid AuthRequest authRequest) {
        return authenticationService.signIn(authRequest);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ignored) {
        return new ResponseEntity<>("Bad auth request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleUsernameTakenException(UsernameTakenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ignored) {
        return new ResponseEntity<>("Wrong password", HttpStatus.FORBIDDEN);
    }

}
