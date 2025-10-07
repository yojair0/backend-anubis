package com.anubis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PasswordResetRequestTest {

    private PasswordResetRequest passwordResetRequest;

    @BeforeEach
    void setUp() {
        passwordResetRequest = new PasswordResetRequest();
    }

    @Test
    void defaultConstructor_InitializesObject() {
        PasswordResetRequest request = new PasswordResetRequest();
        assertNotNull(request);
    }

    @Test
    void constructor_WithEmail_SetsValueCorrectly() {
        String email = "test@example.com";

        PasswordResetRequest request = new PasswordResetRequest(email);

        assertEquals(email, request.getEmail());
    }

    @Test
    void setEmail_ValidEmail_SetsCorrectly() {
        String email = "valid@example.com";
        passwordResetRequest.setEmail(email);
        assertEquals(email, passwordResetRequest.getEmail());
    }

    @Test
    void setEmail_EmptyString_SetsEmptyString() {
        String email = "";
        passwordResetRequest.setEmail(email);
        assertEquals(email, passwordResetRequest.getEmail());
    }

    @Test
    void setEmail_NullValue_SetsNull() {
        passwordResetRequest.setEmail(null);
        assertNull(passwordResetRequest.getEmail());
    }

    @Test
    void getEmail_AfterSetting_ReturnsCorrectValue() {
        String email = "getter@example.com";
        passwordResetRequest.setEmail(email);
        assertEquals(email, passwordResetRequest.getEmail());
    }

    @Test
    void validation_SpecialEmailFormats_HandledProperly() {
        String[] specialEmails = {
            "test+tag@example.com",
            "user.name@domain-name.org",
            "simple@localhost",
            "test@sub.domain.com",
            "user_123@example.co.uk"
        };

        for (String email : specialEmails) {
            passwordResetRequest.setEmail(email);
            assertEquals(email, passwordResetRequest.getEmail());
        }
    }

    @Test
    void validation_EdgeCases_HandledCorrectly() {
        // Very long email
        String longEmail = "very.long.email.address.for.testing.purposes@very-long-domain-name.example.com";
        passwordResetRequest.setEmail(longEmail);
        assertEquals(longEmail, passwordResetRequest.getEmail());

        // Email with numbers
        String numericEmail = "user123@domain456.com";
        passwordResetRequest.setEmail(numericEmail);
        assertEquals(numericEmail, passwordResetRequest.getEmail());
    }

    @Test
    void fluent_SettersIfImplemented_WorkCorrectly() {
        assertDoesNotThrow(() -> {
            PasswordResetRequest request = new PasswordResetRequest();
            request.setEmail("fluent@example.com");
            
            assertEquals("fluent@example.com", request.getEmail());
        });
    }

    @Test
    void immutability_AfterCreation_CanBeModified() {
        PasswordResetRequest request = new PasswordResetRequest("initial@example.com");
        assertEquals("initial@example.com", request.getEmail());

        request.setEmail("modified@example.com");
        assertEquals("modified@example.com", request.getEmail());
    }

    @Test
    void constructor_WithNullValue_HandlesCorrectly() {
        PasswordResetRequest request = new PasswordResetRequest(null);
        assertNull(request.getEmail());
    }

    @Test
    void emailValidation_MultipleFormats_AllWork() {
        String[] validEmails = {
            "simple@example.com",
            "user.first.last@subdomain.example.org",
            "user+tag@example.co.uk",
            "admin@company-name.com",
            "test123@numeric123.org"
        };

        for (String email : validEmails) {
            PasswordResetRequest request = new PasswordResetRequest(email);
            assertEquals(email, request.getEmail());
            
            // Also test setter
            passwordResetRequest.setEmail(email);
            assertEquals(email, passwordResetRequest.getEmail());
        }
    }

    @Test
    void emailHandling_SpecialCharacters_WorksCorrectly() {
        String specialEmail = "tëst@ëxämplë.cöm";
        passwordResetRequest.setEmail(specialEmail);
        assertEquals(specialEmail, passwordResetRequest.getEmail());
    }
}