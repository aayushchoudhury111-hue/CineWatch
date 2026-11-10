package com.example.bookmyshow.Controller;

import com.example.bookmyshow.DTO.AuthRequest;
import com.example.bookmyshow.DTO.UserDto;
import com.example.bookmyshow.Service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public static final String USER_ID = "authenticatedUserId";
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public UserDto register(@RequestBody AuthRequest request, HttpSession session) {
        UserDto user = authService.register(request);
        session.setAttribute(USER_ID, user.getId());
        return user;
    }

    @PostMapping("/login")
    public UserDto login(@RequestBody AuthRequest request, HttpSession session) {
        UserDto user = authService.login(request);
        session.setAttribute(USER_ID, user.getId());
        return user;
    }

    @GetMapping("/me")
    public UserDto me(HttpSession session) {
        Object id = session.getAttribute(USER_ID);
        if (!(id instanceof Long userId)) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return authService.getUser(userId);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
