package com.anubis.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUpEmailService() {
        ReflectionTestUtils.setField(emailService, "appName", "Anubis Test Platform");
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@anubis.com");
    }

    @Test
    void sendVerificationCodeEmail_Success() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String verificationCode = "123456";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendVerificationCodeEmail(email, fullName, verificationCode));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationCodeEmail_EmailSendingException() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String verificationCode = "123456";

        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            emailService.sendVerificationCodeEmail(email, fullName, verificationCode));
        assertEquals("Error enviando email de verificación", exception.getMessage());
    }

    @Test
    void sendVerificationEmail_Success() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String verificationToken = "abc123def456";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendVerificationEmail(email, fullName, verificationToken));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmail_EmailSendingException() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String verificationToken = "abc123def456";

        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // When (no exception thrown, error is caught internally)
        assertDoesNotThrow(() -> emailService.sendVerificationEmail(email, fullName, verificationToken));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendApplicationStatusEmail_AcceptedStatus() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String petName = "Buddy";
        String status = "ACCEPTED";
        String foundationResponse = "Gran candidato para la adopción";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> 
            emailService.sendApplicationStatusEmail(email, fullName, petName, status, foundationResponse));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendApplicationStatusEmail_RejectedStatus() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String petName = "Buddy";
        String status = "REJECTED";
        String foundationResponse = "Necesita más experiencia";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> 
            emailService.sendApplicationStatusEmail(email, fullName, petName, status, foundationResponse));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendApplicationStatusEmail_RejectedStatusSpanish() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String petName = "Buddy";
        String status = "RECHAZADA";
        String foundationResponse = "No cumple requisitos";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> 
            emailService.sendApplicationStatusEmail(email, fullName, petName, status, foundationResponse));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendApplicationStatusEmail_RejectedWithNullResponse() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String petName = "Buddy";
        String status = "REJECTED";
        String foundationResponse = null;

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> 
            emailService.sendApplicationStatusEmail(email, fullName, petName, status, foundationResponse));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendApplicationStatusEmail_WithException() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String petName = "Buddy";
        String status = "ACCEPTED";
        String foundationResponse = "Aprobado";

        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // When (no exception thrown, error is caught internally)
        assertDoesNotThrow(() -> 
            emailService.sendApplicationStatusEmail(email, fullName, petName, status, foundationResponse));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_Success() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String resetToken = "reset123token456";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(email, fullName, resetToken));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_WithException() {
        // Given
        String email = "user@example.com";
        String fullName = "Test User";
        String resetToken = "reset123token456";

        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // When (no exception thrown, error is caught internally)
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(email, fullName, resetToken));

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationCodeEmail_ValidatesParameters() {
        // Given
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertDoesNotThrow(() -> emailService.sendVerificationCodeEmail("test@test.com", "Test User", "123456"));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmail_ValidatesParameters() {
        // Given
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertDoesNotThrow(() -> emailService.sendVerificationEmail("test@test.com", "Test User", "token123"));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_ValidatesParameters() {
        // Given
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail("test@test.com", "Test User", "reset123"));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendApplicationStatusEmail_ValidatesAllParameters() {
        // Given
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertDoesNotThrow(() -> 
            emailService.sendApplicationStatusEmail("test@test.com", "Test User", "Buddy", "ACCEPTED", "Approved"));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}