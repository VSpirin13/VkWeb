package com.example.marketplace.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAuthController {@Autowired
private AuthenticationManager authenticationManager;

    @PostMapping("/auth/test-login")
    public ResponseEntity<?> testLogin(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok("Вход в систему прошел успешно!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка входа: " + e.getMessage());
        }
    }

}
