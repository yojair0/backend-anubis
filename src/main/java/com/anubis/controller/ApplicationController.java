package com.anubis.controller;

import com.anubis.dto.ApplicationRequest;
import com.anubis.dto.ApplicationStatusRequest;
import com.anubis.model.Application;
import com.anubis.security.UserPrincipal;
import com.anubis.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createApplication(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ApplicationRequest request) {
        try {
            Application application = applicationService.createApplication(userPrincipal.getId(), request);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/my-applications")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyApplications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<Application> applications = applicationService.getApplicationsByUser(userPrincipal.getId());
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getApplicationsByPet(@PathVariable String petId) {
        try {
            List<Application> applications = applicationService.getApplicationsByPet(petId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/foundation/my-applications")
    @PreAuthorize("hasRole('FOUNDATION')")
    public ResponseEntity<?> getFoundationApplications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<Application> applications = applicationService.getApplicationsByFoundation(userPrincipal.getId());
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('FOUNDATION')")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable String applicationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ApplicationStatusRequest request) {
        try {
            Application application = applicationService.updateApplicationStatus(
                applicationId, userPrincipal.getId(), request);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getApplicationById(@PathVariable String applicationId) {
        try {
            Application application = applicationService.getApplicationById(applicationId)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllApplications() {
        try {
            // Aquí podrías implementar paginación si es necesario
            return ResponseEntity.ok(new MessageResponse("Endpoint disponible para administradores"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Clase interna para respuestas de mensaje
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
