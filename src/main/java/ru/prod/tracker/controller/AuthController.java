package ru.prod.tracker.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prod.tracker.config.JwtTokenProvider;
import ru.prod.tracker.dto.JwtResponse;
import ru.prod.tracker.dto.LoginRequest;
import ru.prod.tracker.mapper.UserMapper;
import ru.prod.tracker.model.User;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userMapper.findByUsername(request.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
        }

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).build();
        }
        String token = tokenProvider.generateToken(request.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}