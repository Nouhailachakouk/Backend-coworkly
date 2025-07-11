package com.example.temperatureserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.temperatureserver.dto.MessageResponse;
import com.example.temperatureserver.model.User;
import com.example.temperatureserver.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Check if email already exists
        if (userRepository.existsByEmailAsInt(user.getEmail()) == 1) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Cet email est déjà utilisé."));
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        savedUser.setPassword(null); // Do not expose password in the response

        return ResponseEntity.ok(savedUser);
    }
}
