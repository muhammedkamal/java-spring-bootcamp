package com.flairs.taskapi.auth;

import com.flairs.taskapi.exception.DuplicateException;
import com.flairs.taskapi.exception.NotFoundException;
import com.flairs.taskapi.model.Role;
import com.flairs.taskapi.model.User;
import com.flairs.taskapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokens;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, TokenService tokens) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokens = tokens;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().strip().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new DuplicateException("An account with this email already exists");
        }
        // Everybody who signs up is a USER. Nobody can choose ADMIN from the outside.
        User user = users.save(new User(email, passwordEncoder.encode(request.password()), request.displayName().strip(), Role.USER));
        log.info("Registered user {}", user.getId());
        return UserResponse.of(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = users.findByEmailIgnoreCase(request.email().strip()).orElseThrow(InvalidCredentialsException::new);
        // matches() hashes the given password with the salt stored inside the hash and compares
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.info("Failed login for user {}", user.getId());
            throw new InvalidCredentialsException();
        }
        return tokens.issue(user);
    }

    public UserResponse me(Long userId) {
        return UserResponse.of(users.findById(userId).orElseThrow(() -> new NotFoundException("User", userId)));
    }
}
