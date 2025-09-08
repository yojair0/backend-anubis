package com.anubis.controller;

import com.anubis.dto.AuthResponse;
import com.anubis.dto.LoginRequest;
import com.anubis.dto.RegisterRequest;
import com.anubis.dto.PasswordResetRequest;
import com.anubis.dto.PasswordResetConfirmRequest;
import com.anubis.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = authService.register(registerRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        try {
            boolean verified = authService.verifyEmailWithCode(email, code);
            if (verified) {
                return ResponseEntity.ok(new MessageResponse("Email verificado exitosamente"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Error al verificar código"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            boolean verified = authService.verifyEmail(token);
            if (verified) {
                return ResponseEntity.ok(new MessageResponse("Email verificado exitosamente"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Error al verificar email"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        try {
            boolean sent = authService.resendVerificationEmail(email);
            if (sent) {
                return ResponseEntity.ok(new MessageResponse("Email de verificación enviado"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Error al enviar email"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            boolean sent = authService.requestPasswordReset(request.getEmail());
            if (sent) {
                return ResponseEntity.ok(new MessageResponse("Email de recuperación enviado"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Error al enviar email"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
        try {
            boolean reset = authService.resetPassword(request.getToken(), request.getNewPassword());
            if (reset) {
                return ResponseEntity.ok(new MessageResponse("Contraseña actualizada exitosamente"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Error al actualizar contraseña"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
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
