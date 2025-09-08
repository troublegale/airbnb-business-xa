package itmo.tg.airbnb_xa.security.service;

import itmo.tg.airbnb_xa.security.exception.EmailTakenException;
import itmo.tg.airbnb_xa.security.model.User;
import itmo.tg.airbnb_xa.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailTakenException(String.format("Email %s already taken", user.getEmail()));
        }
        userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User %s not found", email))
        );
    }

    public Boolean existsByUsername(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByEmail(email);
    }

    public UserDetailsService getUserDetailsService() {
        return this::getByEmail;
    }

}
