package com.anubis.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.anubis.dto.AuthResponse;
import com.anubis.dto.LoginRequest;
import com.anubis.dto.RegisterRequest;
import com.anubis.model.Role;
import com.anubis.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthControllerIntegrationTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("New User");
        registerRequest.setPhone("123456789");

        authResponse = new AuthResponse();
        authResponse.setToken("jwt.token.here");
        authResponse.setId("user123");
        authResponse.setEmail("test@example.com");
        authResponse.setFullName("Test User");
        authResponse.setRole(Role.USER);
    }

    // ========== LOGIN TESTS ==========
    @Test
    void testLogin_Success() {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<?> response = authController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthResponse);
        
        AuthResponse responseBody = (AuthResponse) response.getBody();
        assertEquals("jwt.token.here", responseBody.getToken());
        assertEquals("user123", responseBody.getId());
        assertEquals("test@example.com", responseBody.getEmail());
        assertEquals("Test User", responseBody.getFullName());
        assertEquals(Role.USER, responseBody.getRole());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Given
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Credenciales inválidas"));

        // When
        ResponseEntity<?> response = authController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthController.MessageResponse);
        
        AuthController.MessageResponse messageResponse = (AuthController.MessageResponse) response.getBody();
        assertEquals("Error: Credenciales inválidas", messageResponse.getMessage());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void testLogin_ServiceException() {
        // Given
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Error interno del servidor"));

        // When
        ResponseEntity<?> response = authController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthController.MessageResponse);

        verify(authService).login(any(LoginRequest.class));
    }

    // ========== REGISTER TESTS ==========
    @Test
    void testRegister_Success() {
        // Given
        String successMessage = "Código de verificación enviado. Revisa tu email.";
        when(authService.register(any(RegisterRequest.class))).thenReturn(successMessage);

        // When
        ResponseEntity<?> response = authController.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthController.MessageResponse);
        
        AuthController.MessageResponse messageResponse = (AuthController.MessageResponse) response.getBody();
        assertEquals(successMessage, messageResponse.getMessage());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("El email ya está registrado"));

        // When
        ResponseEntity<?> response = authController.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthController.MessageResponse);
        
        AuthController.MessageResponse messageResponse = (AuthController.MessageResponse) response.getBody();
        assertEquals("Error: El email ya está registrado", messageResponse.getMessage());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void testRegister_ServiceException() {
        // Given
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("Error del servicio de email"));

        // When
        ResponseEntity<?> response = authController.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthController.MessageResponse);

        verify(authService).register(any(RegisterRequest.class));
    }

    // ========== EDGE CASES AND VALIDATION TESTS ==========
    @Test
    void testLogin_NullRequest() {
        // Given
        when(authService.login(null)).thenThrow(new RuntimeException("Request inválido"));

        // When
        ResponseEntity<?> response = authController.login(null);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        verify(authService).login(null);
    }

    @Test
    void testRegister_NullRequest() {
        // Given
        when(authService.register(null)).thenThrow(new RuntimeException("Request inválido"));

        // When
        ResponseEntity<?> response = authController.register(null);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        verify(authService).register(null);
    }

    // ========== BUSINESS LOGIC VALIDATION TESTS ==========
    @Test
    void testLogin_EmptyEmail() {
        // Given
        loginRequest.setEmail("");
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Email no puede estar vacío"));

        // When
        ResponseEntity<?> response = authController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void testLogin_EmptyPassword() {
        // Given
        loginRequest.setPassword("");
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Password no puede estar vacío"));

        // When
        ResponseEntity<?> response = authController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void testRegister_InvalidEmailFormat() {
        // Given
        registerRequest.setEmail("invalid-email");
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("Formato de email inválido"));

        // When
        ResponseEntity<?> response = authController.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void testRegister_WeakPassword() {
        // Given
        registerRequest.setPassword("123");
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("Password demasiado débil"));

        // When
        ResponseEntity<?> response = authController.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        verify(authService).register(any(RegisterRequest.class));
    }

    // ========== RESPONSE STRUCTURE VALIDATION ==========
    @Test
    void testLogin_ResponseStructure() {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<?> response = authController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AuthResponse);
        
        AuthResponse auth = (AuthResponse) response.getBody();
        assertNotNull(auth.getToken());
        assertNotNull(auth.getId());
        assertNotNull(auth.getEmail());
        assertNotNull(auth.getFullName());
        assertNotNull(auth.getRole());
        assertEquals("Bearer", auth.getType());
    }

    @Test
    void testRegister_ResponseStructure() {
        // Given
        String message = "Registro exitoso";
        when(authService.register(any(RegisterRequest.class))).thenReturn(message);

        // When
        ResponseEntity<?> response = authController.register(registerRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AuthController.MessageResponse);
        
        AuthController.MessageResponse msgResponse = (AuthController.MessageResponse) response.getBody();
        assertNotNull(msgResponse.getMessage());
        assertEquals(message, msgResponse.getMessage());
    }

    // ========== PERFORMANCE AND LOAD TESTS ==========
    @Test
    void testLogin_MultipleRequests() {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When & Then - Simulating multiple concurrent requests
        for (int i = 0; i < 10; i++) {
            ResponseEntity<?> response = authController.login(loginRequest);
            assertNotNull(response);
            assertEquals(200, response.getStatusCodeValue());
        }

        verify(authService, times(10)).login(any(LoginRequest.class));
    }

    @Test
    void testRegister_MultipleRequests() {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn("Success");

        // When & Then - Simulating multiple concurrent requests
        for (int i = 0; i < 5; i++) {
            ResponseEntity<?> response = authController.register(registerRequest);
            assertNotNull(response);
            assertEquals(200, response.getStatusCodeValue());
        }

        verify(authService, times(5)).register(any(RegisterRequest.class));
    }
}