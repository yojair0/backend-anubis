package com.anubis.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anubis.dto.AdminRegisterRequest;
import com.anubis.dto.ApplicationDetailResponse;
import com.anubis.dto.ApplicationStatusRequest;
import com.anubis.dto.AuthResponse;
import com.anubis.dto.ChangeRoleRequest;
import com.anubis.model.Application;
import com.anubis.model.User;
import com.anubis.repository.ApplicationRepository;
import com.anubis.repository.PetRepository;
import com.anubis.repository.UserRepository;
import com.anubis.service.ApplicationService;
import com.anubis.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ===== ENDPOINTS DE APLICACIONES PARA ADMIN =====

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationDetailResponse>> getAllApplications() {
        try {
            List<ApplicationDetailResponse> applications = applicationService.getAllDetailedApplications();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable String applicationId,
            @RequestBody ApplicationStatusRequest request) {
        try {
            Application updatedApplication = applicationService.updateApplicationStatusAsAdmin(applicationId, request);
            return ResponseEntity.ok(updatedApplication);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<?> deleteApplication(@PathVariable String applicationId) {
        try {
            applicationService.deleteApplicationAsAdmin(applicationId);
            return ResponseEntity.ok().body("Postulación eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar postulación: " + e.getMessage());
        }
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApplicationDetailResponse> getApplicationById(@PathVariable String applicationId) {
        try {
            Optional<ApplicationDetailResponse> application = applicationService.getDetailedApplicationById(applicationId);
            if (application.isPresent()) {
                return ResponseEntity.ok(application.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            
            applicationRepository.deleteByUserId(userId);
            
            if ("FOUNDATION".equals(user.get().getRole().name())) {
                petRepository.deleteByFoundationId(userId);
            }
            
            userRepository.deleteById(userId);
            
            return ResponseEntity.ok().body("Usuario eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar usuario: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            long userCount = userRepository.count();
            long applicationCount = applicationRepository.count();
            long petCount = petRepository.count();
            
            return ResponseEntity.ok().body("""
                Estadísticas del sistema:
                Usuarios: %d
                Postulaciones: %d
                Mascotas: %d
                """.formatted(userCount, applicationCount, petCount));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    @GetMapping("/count-data")
    public ResponseEntity<?> countData() {
        try {
            long userCount = userRepository.count();
            long applicationCount = applicationRepository.count();
            
            return ResponseEntity.ok().body("""
                Datos en base:
                Usuarios: %d
                Postulaciones: %d
                """.formatted(userCount, applicationCount));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al contar datos: " + e.getMessage());
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUserWithRole(@Valid @RequestBody AdminRegisterRequest adminRegisterRequest) {
        try {
            AuthResponse response = authService.adminRegister(adminRegisterRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> changeUserRole(@PathVariable String userId, 
                                           @Valid @RequestBody ChangeRoleRequest roleRequest) {
        try {
            User updatedUser = authService.changeUserRole(userId, roleRequest.getRole());
            return ResponseEntity.ok(new MessageResponse(
                "Rol actualizado exitosamente. Usuario: " + updatedUser.getEmail() + 
                " ahora tiene rol: " + updatedUser.getRole()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
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
