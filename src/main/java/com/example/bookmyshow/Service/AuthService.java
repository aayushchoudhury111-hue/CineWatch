package com.example.bookmyshow.Service;

import com.example.bookmyshow.DTO.AuthRequest;
import com.example.bookmyshow.DTO.UserDto;
import com.example.bookmyshow.Entity.Users;
import com.example.bookmyshow.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@Transactional
public class AuthService {
    private final UserRepository users;

    public AuthService(UserRepository users) { this.users = users; }

    public UserDto register(AuthRequest request) {
        String name = request.getName() == null ? "" : request.getName().trim();
        String email = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();
        String password = request.getPassword() == null ? "" : request.getPassword();
        if (name.isBlank() || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Enter your name and a valid email address");
        if (password.length() < 8) throw new IllegalArgumentException("Use a password with at least 8 characters");
        if (users.existsByEmail(email)) throw new IllegalArgumentException("An account with this email already exists");

        Users user = new Users();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(PasswordHasher.hash(password));
        user.setPhoneNumber(request.getPhoneNumber() == null ? "" : request.getPhoneNumber().trim());
        return toDto(users.save(user));
    }

    public UserDto login(AuthRequest request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();
        Users user = users.findByEmailIgnoreCase(email).orElse(null);
        if (user == null || !passwordMatchesAndUpgrade(user, password))
            throw new IllegalArgumentException("Email or password is incorrect");
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        return users.findById(id).map(this::toDto).orElseThrow(() -> new IllegalArgumentException("Your session has expired"));
    }

    private UserDto toDto(Users user) {
        return new UserDto(user.getUserId(), user.getName(), user.getEmail(), user.getPhoneNumber());
    }

    private boolean passwordMatchesAndUpgrade(Users user, String password) {
        String stored = user.getPassword();
        if (stored == null) return false;
        if (PasswordHasher.matches(password, stored)) return true;
        if (stored.startsWith("pbkdf2$")) return false;
        boolean legacyMatch = MessageDigest.isEqual(password.getBytes(StandardCharsets.UTF_8), stored.getBytes(StandardCharsets.UTF_8));
        if (legacyMatch) {
            user.setPassword(PasswordHasher.hash(password));
            users.save(user);
        }
        return legacyMatch;
    }
}
