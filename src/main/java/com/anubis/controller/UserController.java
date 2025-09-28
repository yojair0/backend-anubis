package com.anubis.controller;

import com.anubis.model.User;
import com.anubis.model.Role;
import com.anubis.security.UserPrincipal;
import com.anubis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userService.getUserById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            user.setPassword(null);
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody User updatedUser) {
        try {
            User user = userService.updateUserProfile(userPrincipal.getId(), updatedUser);
            user.setPassword(null);
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/foundations")
    public ResponseEntity<?> getFoundations() {
        try {
            List<User> foundations = userService.getUsersByRole(Role.FOUNDATION);
            foundations.forEach(foundation -> foundation.setPassword(null));
            
            return ResponseEntity.ok(foundations);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new MessageResponse("Usuario eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            users.forEach(user -> user.setPassword(null));
            
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/role")
    @PreAuthorize("hasRole('USER') or hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentUserRole(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userService.getUserById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            return ResponseEntity.ok(new RoleResponse(user.getRole()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    public static class RoleResponse {
        private Role role;

        public RoleResponse(Role role) {
            this.role = role;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }
    }

    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
