package org.example.backend_med.Controller;

import org.example.backend_med.Models.Patient;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")

public class UserController {

    private final UserService userService;

    public UserController(UserService service) {
        this.userService = service;
    }

    // -----------------------
    // Get user by ID (role-aware)
    // -----------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -----------------------
    // Update user by ID (role-aware)
    // -----------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Object updatedUser) {
        try {
            Object updated = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -----------------------
    // Delete user by ID (role-aware)
    // -----------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
