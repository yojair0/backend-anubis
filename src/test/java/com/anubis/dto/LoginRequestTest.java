package com.anubis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class LoginRequestTest {

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
    }

    @Test
    void defaultConstructor_InitializesObject() {
        LoginRequest request = new LoginRequest();
        assertNotNull(request);
    }

    @Test
    void constructor_WithEmailAndPassword_SetsValuesCorrectly() {
        String email = "test@example.com";
        String password = "testpassword";

        LoginRequest request = new LoginRequest(email, password);

        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
    }

    @Test
    void setEmail_ValidEmail_SetsCorrectly() {
        String email = "valid@example.com";
        loginRequest.setEmail(email);
        assertEquals(email, loginRequest.getEmail());
    }

    @Test
    void setEmail_EmptyString_SetsEmptyString() {
        String email = "";
        loginRequest.setEmail(email);
        assertEquals(email, loginRequest.getEmail());
    }

    @Test
    void setEmail_NullValue_SetsNull() {
        loginRequest.setEmail(null);
        assertNull(loginRequest.getEmail());
    }

    @Test
    void setPassword_ValidPassword_SetsCorrectly() {
        String password = "validpassword123";
        loginRequest.setPassword(password);
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    void setPassword_EmptyString_SetsEmptyString() {
        String password = "";
        loginRequest.setPassword(password);
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    void setPassword_NullValue_SetsNull() {
        loginRequest.setPassword(null);
        assertNull(loginRequest.getPassword());
    }

    @Test
    void getEmail_AfterSetting_ReturnsCorrectValue() {
        String email = "getter@example.com";
        loginRequest.setEmail(email);
        assertEquals(email, loginRequest.getEmail());
    }

    @Test
    void getPassword_AfterSetting_ReturnsCorrectValue() {
        String password = "getterpassword";
        loginRequest.setPassword(password);
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    void getters_ReturnCorrectValues() {
        String email = "test@example.com";
        String password = "testpassword";

        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        assertEquals(email, loginRequest.getEmail());
        assertEquals(password, loginRequest.getPassword());
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
            loginRequest.setEmail(email);
            assertEquals(email, loginRequest.getEmail());
        }
    }

    @Test
    void validation_SpecialPasswords_HandledProperly() {
        String[] specialPasswords = {
            "P@ssw0rd!123",
            "mySup3rS3cur3P@ssw0rd#",
            "password_with_underscores_123",
            "simple123"
        };

        for (String password : specialPasswords) {
            loginRequest.setPassword(password);
            assertEquals(password, loginRequest.getPassword());
        }
    }

    @Test
    void validation_EdgeCases_HandledCorrectly() {
        // Very long email
        String longEmail = "very.long.email.address.for.testing.purposes@very-long-domain-name.example.com";
        loginRequest.setEmail(longEmail);
        assertEquals(longEmail, loginRequest.getEmail());

        // Very long password
        String longPassword = "VeryLongPasswordForTestingPurposes123!@#";
        loginRequest.setPassword(longPassword);
        assertEquals(longPassword, loginRequest.getPassword());
    }

    @Test
    void fluent_SettersIfImplemented_WorkCorrectly() {
        assertDoesNotThrow(() -> {
            LoginRequest request = new LoginRequest();
            request.setEmail("fluent@example.com");
            request.setPassword("fluentpass");
            
            assertEquals("fluent@example.com", request.getEmail());
            assertEquals("fluentpass", request.getPassword());
        });
    }

    @Test
    void immutability_AfterCreation_CanBeModified() {
        LoginRequest request = new LoginRequest("initial@example.com", "initialpass");
        assertEquals("initial@example.com", request.getEmail());
        assertEquals("initialpass", request.getPassword());

        request.setEmail("modified@example.com");
        request.setPassword("modifiedpass");
        assertEquals("modified@example.com", request.getEmail());
        assertEquals("modifiedpass", request.getPassword());
    }

    @Test
    void constructor_WithNullValues_HandlesCorrectly() {
        LoginRequest request = new LoginRequest(null, null);
        assertNull(request.getEmail());
        assertNull(request.getPassword());
    }

    @Test
    void emailAndPasswordIndependence_WorksCorrectly() {
        String email = "independent@example.com";
        String password = "independentpass";

        loginRequest.setEmail(email);
        assertEquals(email, loginRequest.getEmail());
        assertNull(loginRequest.getPassword()); // Should still be null

        loginRequest.setPassword(password);
        assertEquals(password, loginRequest.getPassword());
        assertEquals(email, loginRequest.getEmail()); // Should still be the same
    }
}