package com.anubis.dto;

import com.anubis.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AuthResponseTest {

    private AuthResponse authResponse;
    private Role testRole;

    @BeforeEach
    void setUp() {
        authResponse = new AuthResponse();
        testRole = Role.USER; // Assuming USER is a valid role
    }

    @Test
    void defaultConstructor_InitializesObject() {
        AuthResponse response = new AuthResponse();
        assertNotNull(response);
        assertEquals("Bearer", response.getType()); // Default type is Bearer
    }

    @Test
    void constructor_WithAllParameters_SetsValuesCorrectly() {
        String token = "jwt-token-123";
        String id = "user-123";
        String email = "test@example.com";
        String fullName = "Test User";

        AuthResponse response = new AuthResponse(token, id, email, fullName, testRole);

        assertEquals(token, response.getToken());
        assertEquals(id, response.getId());
        assertEquals(email, response.getEmail());
        assertEquals(fullName, response.getFullName());
        assertEquals(testRole, response.getRole());
        assertEquals("Bearer", response.getType());
    }

    @Test
    void setToken_ValidToken_SetsCorrectly() {
        String token = "valid-token-123";
        authResponse.setToken(token);
        assertEquals(token, authResponse.getToken());
    }

    @Test
    void setToken_NullToken_SetsNull() {
        authResponse.setToken(null);
        assertNull(authResponse.getToken());
    }

    @Test
    void setType_ValidType_SetsCorrectly() {
        String type = "Custom";
        authResponse.setType(type);
        assertEquals(type, authResponse.getType());
    }

    @Test
    void setId_ValidId_SetsCorrectly() {
        String id = "user-456";
        authResponse.setId(id);
        assertEquals(id, authResponse.getId());
    }

    @Test
    void setEmail_ValidEmail_SetsCorrectly() {
        String email = "valid@example.com";
        authResponse.setEmail(email);
        assertEquals(email, authResponse.getEmail());
    }

    @Test
    void setFullName_ValidName_SetsCorrectly() {
        String fullName = "John Doe";
        authResponse.setFullName(fullName);
        assertEquals(fullName, authResponse.getFullName());
    }

    @Test
    void setRole_ValidRole_SetsCorrectly() {
        authResponse.setRole(testRole);
        assertEquals(testRole, authResponse.getRole());
    }

    @Test
    void getters_ReturnCorrectValues() {
        String token = "getter-token";
        String type = "Custom";
        String id = "getter-id";
        String email = "getter@example.com";
        String fullName = "Getter User";

        authResponse.setToken(token);
        authResponse.setType(type);
        authResponse.setId(id);
        authResponse.setEmail(email);
        authResponse.setFullName(fullName);
        authResponse.setRole(testRole);

        assertEquals(token, authResponse.getToken());
        assertEquals(type, authResponse.getType());
        assertEquals(id, authResponse.getId());
        assertEquals(email, authResponse.getEmail());
        assertEquals(fullName, authResponse.getFullName());
        assertEquals(testRole, authResponse.getRole());
    }

    @Test
    void setNullValues_HandledProperly() {
        authResponse.setToken(null);
        authResponse.setId(null);
        authResponse.setEmail(null);
        authResponse.setFullName(null);
        authResponse.setRole(null);

        assertNull(authResponse.getToken());
        assertNull(authResponse.getId());
        assertNull(authResponse.getEmail());
        assertNull(authResponse.getFullName());
        assertNull(authResponse.getRole());
    }

    @Test
    void defaultType_IsBearerToken() {
        AuthResponse response = new AuthResponse();
        assertEquals("Bearer", response.getType());
    }

    @Test
    void validation_SpecialCharacters_HandledProperly() {
        String specialToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";
        String specialEmail = "test+tag@example-domain.org";
        String specialName = "María-José O'Connor";

        authResponse.setToken(specialToken);
        authResponse.setEmail(specialEmail);
        authResponse.setFullName(specialName);

        assertEquals(specialToken, authResponse.getToken());
        assertEquals(specialEmail, authResponse.getEmail());
        assertEquals(specialName, authResponse.getFullName());
    }

    @Test
    void immutability_AfterCreation_CanBeModified() {
        AuthResponse response = new AuthResponse("initial-token", "initial-id", 
                "initial@example.com", "Initial User", testRole);
        
        assertEquals("initial-token", response.getToken());
        assertEquals("initial-id", response.getId());

        response.setToken("modified-token");
        response.setId("modified-id");
        
        assertEquals("modified-token", response.getToken());
        assertEquals("modified-id", response.getId());
    }

    @Test
    void roleHandling_DifferentRoles_WorksCorrectly() {
        // Test with different role values if available
        authResponse.setRole(Role.USER);
        assertEquals(Role.USER, authResponse.getRole());
        
        // Test role null handling
        authResponse.setRole(null);
        assertNull(authResponse.getRole());
    }
}