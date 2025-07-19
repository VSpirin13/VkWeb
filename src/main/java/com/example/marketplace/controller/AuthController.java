package com.example.marketplace.controller;

import com.example.marketplace.dto.AuthenticationRequest;
import com.example.marketplace.dto.RegistrationRequest;
import com.example.marketplace.model.User;
import com.example.marketplace.security.JWTUtil;
import com.example.marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {
        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return new ResponseEntity<>(Map.of("message", "Пользователь с таким именем уже существует"), HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        User registeredUser = userService.registerUser(user);

        User safeUser = new User();
        safeUser.setId(registeredUser.getId());
        safeUser.setUsername(registeredUser.getUsername());

        return new ResponseEntity<>(safeUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthenticationRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (AuthenticationException authException) {
            return new ResponseEntity<>(Map.of("error", "Неверные учетные данные"), HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(Map.of("token", token));
    }
}
