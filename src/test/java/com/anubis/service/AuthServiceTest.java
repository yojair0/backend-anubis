package com.anubis.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.anubis.dto.AuthResponse;
import com.anubis.dto.LoginRequest;
import com.anubis.dto.RegisterRequest;
import com.anubis.model.PendingRegistration;
import com.anubis.model.Role;
import com.anubis.model.User;
import com.anubis.repository.PendingRegistrationRepository;
import com.anubis.repository.UserRepository;
import com.anubis.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private EmailService emailService;

    @Mock
    private PendingRegistrationRepository pendingRegistrationRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new User();
        testUser.setId("user123");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("New User");
        registerRequest.setPhone("123456789");
    }

    // ========== LOGIN TESTS ==========
    @Test
    void testLogin_Success() {
        // Given
        String expectedToken = "jwt.token.here";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        AuthResponse result = authService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result.getToken());
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getFullName(), result.getFullName());
        assertEquals(testUser.getRole(), result.getRole());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(tokenProvider);
        verifyNoInteractions(userRepository);
    }

    @Test
    void testLogin_UserNotFound() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
    }

    // ========== REGISTER TESTS ==========
    @Test
    void testRegister_Success() {
        // Given
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(pendingRegistrationRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(pendingRegistrationRepository.save(any(PendingRegistration.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = authService.register(registerRequest);

        // Then
        assertEquals("Código de verificación enviado. Revisa tu email.", result);

        verify(userRepository).existsByEmail("newuser@example.com");
        verify(pendingRegistrationRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(pendingRegistrationRepository).save(any(PendingRegistration.class));
        verify(emailService).sendVerificationCodeEmail(
            eq("newuser@example.com"), 
            eq("New User"), 
            anyString()
        );
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("El email ya está registrado", exception.getMessage());
        verify(userRepository).existsByEmail("newuser@example.com");
        verifyNoInteractions(pendingRegistrationRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(emailService);
    }

    @Test
    void testRegister_PendingRegistrationExists() {
        // Given
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(pendingRegistrationRepository.existsByEmail("newuser@example.com")).thenReturn(true);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(pendingRegistrationRepository.save(any(PendingRegistration.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = authService.register(registerRequest);

        // Then
        assertEquals("Código de verificación enviado. Revisa tu email.", result);

        verify(pendingRegistrationRepository).existsByEmail("newuser@example.com");
        verify(pendingRegistrationRepository).deleteByEmail("newuser@example.com");
        verify(pendingRegistrationRepository).save(any(PendingRegistration.class));
        verify(emailService).sendVerificationCodeEmail(
            eq("newuser@example.com"), 
            eq("New User"), 
            anyString()
        );
    }

    @Test
    void testRegister_ValidationOfSavedPendingRegistration() {
        // Given
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(pendingRegistrationRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        
        PendingRegistration savedRegistration = null;
        when(pendingRegistrationRepository.save(any(PendingRegistration.class)))
            .thenAnswer(invocation -> {
                PendingRegistration registration = invocation.getArgument(0);
                // Validate the registration object
                assertNotNull(registration.getEmail());
                assertNotNull(registration.getPassword());
                assertNotNull(registration.getFullName());
                assertNotNull(registration.getPhone());
                assertNotNull(registration.getRole());
                assertNotNull(registration.getVerificationCode());
                assertNotNull(registration.getVerificationCodeExpiry());
                
                assertEquals("newuser@example.com", registration.getEmail());
                assertEquals("encodedPassword", registration.getPassword());
                assertEquals("New User", registration.getFullName());
                assertEquals("123456789", registration.getPhone());
                assertEquals(Role.USER, registration.getRole());
                assertTrue(registration.getVerificationCode().matches("\\d{6}"));
                assertTrue(registration.getVerificationCodeExpiry().isAfter(LocalDateTime.now()));
                
                return registration;
            });

        // When
        authService.register(registerRequest);

        // Then - validations are in the answer callback above
        verify(pendingRegistrationRepository).save(any(PendingRegistration.class));
    }

    // ========== ERROR HANDLING TESTS ==========
    @Test
    void testLogin_NullRequest() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            authService.login(null);
        });
    }

    @Test
    void testRegister_NullRequest() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            authService.register(null);
        });
    }

    // ========== INTEGRATION-LIKE TESTS ==========
    @Test
    void testRegister_EmailServiceFailure() {
        // Given
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(pendingRegistrationRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(pendingRegistrationRepository.save(any(PendingRegistration.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        doThrow(new RuntimeException("Email service unavailable"))
            .when(emailService).sendVerificationCodeEmail(anyString(), anyString(), anyString());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Email service unavailable", exception.getMessage());
        verify(pendingRegistrationRepository).save(any(PendingRegistration.class));
    }
}