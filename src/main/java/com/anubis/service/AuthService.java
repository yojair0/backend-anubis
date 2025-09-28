package com.anubis.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anubis.dto.AuthResponse;
import com.anubis.dto.LoginRequest;
import com.anubis.dto.RegisterRequest;
import com.anubis.dto.AdminRegisterRequest;
import com.anubis.model.PendingRegistration;
import com.anubis.model.Role;
import com.anubis.model.User;
import com.anubis.repository.PendingRegistrationRepository;
import com.anubis.repository.UserRepository;
import com.anubis.security.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PendingRegistrationRepository pendingRegistrationRepository;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new AuthResponse(jwt, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }

    public String register(RegisterRequest registerRequest) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Verificar si ya hay un registro pendiente
        if (pendingRegistrationRepository.existsByEmail(registerRequest.getEmail())) {
            // Eliminar el registro pendiente anterior
            pendingRegistrationRepository.deleteByEmail(registerRequest.getEmail());
        }

        // Crear registro pendiente (NO usuario final)
        PendingRegistration pendingRegistration = new PendingRegistration();
        pendingRegistration.setEmail(registerRequest.getEmail());
        pendingRegistration.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        pendingRegistration.setFullName(registerRequest.getFullName());
        pendingRegistration.setPhone(registerRequest.getPhone());
        pendingRegistration.setRole(Role.USER); // Siempre asignar rol USER automáticamente
        
        // Generar código de verificación de 6 dígitos
        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        pendingRegistration.setVerificationCode(verificationCode);
        pendingRegistration.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));

        pendingRegistrationRepository.save(pendingRegistration);

        // Enviar email con código de verificación
        emailService.sendVerificationCodeEmail(
            pendingRegistration.getEmail(), 
            pendingRegistration.getFullName(),
            verificationCode
        );

        return "Código de verificación enviado. Revisa tu email.";
    }

    public AuthResponse verifyEmailWithCode(String email, String code) {
        // Buscar en registros pendientes
        PendingRegistration pendingRegistration = pendingRegistrationRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No se encontró registro pendiente"));

        if (pendingRegistration.getVerificationCode() == null || !pendingRegistration.getVerificationCode().equals(code)) {
            throw new RuntimeException("Código de verificación inválido");
        }

        if (pendingRegistration.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código de verificación expirado");
        }

        // AHORA SI: Crear usuario real en la base de datos
        User user = new User();
        user.setEmail(pendingRegistration.getEmail());
        user.setPassword(pendingRegistration.getPassword()); // Ya está encriptada
        user.setFullName(pendingRegistration.getFullName());
        user.setPhone(pendingRegistration.getPhone());
        user.setRole(pendingRegistration.getRole());
        user.setEmailVerified(true); // Ya verificado

        User savedUser = userRepository.save(user);

        // Eliminar registro pendiente
        pendingRegistrationRepository.deleteByEmail(email);

        // Generar token JWT
        String jwt = tokenProvider.generateToken(savedUser.getId());

        return new AuthResponse(jwt, savedUser.getId(), savedUser.getEmail(), 
                              savedUser.getFullName(), savedUser.getRole());
    }

    public boolean verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
            .orElseThrow(() -> new RuntimeException("Token de verificación inválido"));

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);

        return true;
    }

    public boolean resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("El email ya está verificado");
        }

        // Generar nuevo token
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);
        userRepository.save(user);

        // Enviar email
        emailService.sendVerificationEmail(
            user.getEmail(), 
            user.getFullName(), 
            verificationToken
        );

        return true;
    }

    public boolean requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar token de reset
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1)); // Expira en 1 hora
        userRepository.save(user);

        // Enviar email
        emailService.sendPasswordResetEmail(
            user.getEmail(), 
            user.getFullName(), 
            resetToken
        );

        return true;
    }

    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
            .orElseThrow(() -> new RuntimeException("Token de reset inválido"));

        // Verificar que el token no haya expirado
        if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token de reset ha expirado");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);

        return true;
    }

    public AuthResponse adminRegister(AdminRegisterRequest adminRegisterRequest) {
        if (userRepository.existsByEmail(adminRegisterRequest.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        User user = new User();
        user.setEmail(adminRegisterRequest.getEmail());
        user.setPassword(passwordEncoder.encode(adminRegisterRequest.getPassword()));
        user.setFullName(adminRegisterRequest.getFullName());
        user.setPhone(adminRegisterRequest.getPhone());
        user.setRole(adminRegisterRequest.getRole());
        user.setEmailVerified(true);

        User savedUser = userRepository.save(user);

        String jwt = tokenProvider.generateToken(savedUser.getId());

        return new AuthResponse(jwt, savedUser.getId(), savedUser.getEmail(), 
                              savedUser.getFullName(), savedUser.getRole());
    }

    public User changeUserRole(String userId, Role newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Role previousRole = user.getRole();
        
        user.setRole(newRole);
        user.setUpdatedAt(java.time.LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        System.out.println("Rol cambiado para usuario " + user.getEmail() + 
                          ": " + previousRole + " -> " + newRole);

        return updatedUser;
    }
}
