package itmo.tg.airbnb_xa.security.service;

import itmo.tg.airbnb_xa.security.exception.UsernameTakenException;
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
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameTakenException(String.format("Username %s already taken", user.getUsername()));
        }
        userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User %s not found", username))
        );
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public UserDetailsService getUserDetailsService() {
        return this::getByUsername;
    }

}
