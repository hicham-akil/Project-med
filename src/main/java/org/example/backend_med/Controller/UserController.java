package org.example.backend_med.Controller;

import org.example.backend_med.Dto.UpdateUserRequest;
import org.example.backend_med.Models.Patient;
import org.example.backend_med.Models.Medecin;
import org.example.backend_med.Models.Utlisateur;
import org.example.backend_med.Services.ImageUploadService;
import org.example.backend_med.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")

public class UserController {


    private final UserService userService;
    private final ImageUploadService imageUploadService;  // Add this

    public UserController(UserService userService, ImageUploadService imageUploadService) {
        this.userService = userService;
        this.imageUploadService = imageUploadService;  // Add this
    }


    @GetMapping("/{id}")
    public Utlisateur getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Utlisateur> updateUser(
            @PathVariable Long id,
            @RequestPart("data") UpdateUserRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String imageUrl = (image != null && !image.isEmpty())
                ? imageUploadService.uploadImage(image)
                : null;

        return ResponseEntity.ok(userService.updateUser(id, request, imageUrl));
    }

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
