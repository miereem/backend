package org.example.controller;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.model.Role;
import org.example.model.User;
import org.example.security.JwtService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        Role role = request.getRole() != null ? request.getRole() : Role.USER;
        user.setRoles(Set.of(role));
        userService.register(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @Data
    public static class UserRegistrationRequest {
        private String username;
        private String password;
        private Role role = Role.USER;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails principal = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(principal);
            User user = userService.findByUsername(request.getUsername());

            return ResponseEntity.ok(new LoginResponse(user.getUsername(), user.getRoles(), token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Неверные данные для входа");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return ResponseEntity.ok(new UserProfileResponse(user.getUsername(), user.getRoles()));
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class LoginResponse {
        private String username;
        private Set<Role> roles;
        private String token;
    }

    @Data
    @AllArgsConstructor
    public static class UserProfileResponse {
        private String username;
        private Set<Role> roles;
    }
}
