package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.model.User;
import com.example.ecommerce_backend.repository.UserRepository;
import com.example.ecommerce_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/account")
    public User createAccount(@RequestBody Map<String, String> payload) {
        /*
           Expected payload:
           {
             "username": "string",
             "firstname": "string",
             "email": "string",
             "password": "string"
           }
         */
        User user = User.builder()
                .username(payload.get("username"))
                .firstname(payload.get("firstname"))
                .email(payload.get("email"))
                .password(passwordEncoder.encode(payload.get("password")))
                .build();
        return userRepository.save(user);
    }

    @PostMapping("/token")
    public Map<String, String> login(@RequestBody Map<String, String> payload) {
        /*
           Expected payload:
           {
             "email": "string",
             "password": "string"
           }
         */
        String email = payload.get("email");
        String rawPassword = payload.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail());
                return Map.of("token", token);
            }
        }
        throw new RuntimeException("Invalid credentials");
    }
}
