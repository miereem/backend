package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Role;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username,
                                           @RequestParam String password,
                                           @RequestParam(defaultValue = "USER") Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(Collections.singleton(role));
        userService.register(user);
        return ResponseEntity.ok("User registered successfully");
    }
}

