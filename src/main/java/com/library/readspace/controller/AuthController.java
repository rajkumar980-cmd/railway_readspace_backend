package com.library.readspace.controller;

import com.library.readspace.dto.AuthResponse;
import com.library.readspace.dto.LoginRequest;
import com.library.readspace.dto.SignupRequest;
import com.library.readspace.model.User;
import com.library.readspace.repository.UserRepository;
import com.library.readspace.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            if (authentication.isAuthenticated()) {
                String token = jwtUtil.generateToken(request.getEmail());
                User user = userRepository.findByEmail(request.getEmail()).get();
                return ResponseEntity.ok(new AuthResponse(true, null, user, token));
            } else {
                return ResponseEntity.ok(new AuthResponse(false, "Invalid credentials", null, null));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new AuthResponse(false, "Invalid credentials", null, null));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.ok(new AuthResponse(false, "Email already in use", null, null));
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword()); // In production, hash this!
        newUser.setRole("student");
        newUser.setJoined(LocalDate.now());
        newUser.setDownloads(0);
        newUser.setStatus("active");

        userRepository.save(newUser);
        
        String token = jwtUtil.generateToken(newUser.getEmail());

        return ResponseEntity.ok(new AuthResponse(true, null, newUser, token));
    }
}

