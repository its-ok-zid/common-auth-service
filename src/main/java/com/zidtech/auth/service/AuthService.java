package com.zidtech.auth.service;

import com.zidtech.auth.model.User;
import com.zidtech.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public User signup(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        return userRepo.save(user);
    }

    public User authenticate(String username, String password) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return user;
    }
}
