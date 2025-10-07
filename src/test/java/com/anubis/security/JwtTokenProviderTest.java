package com.anubis.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private final String testSecret = "mySecretKey123456789012345678901234567890123456789012345678901234567890";
    private final long testExpiration = 86400000; // 1 day

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", testExpiration);
    }

    @Test
    void generateToken_WithAuthentication_Success() {
        // Given
        String userId = "user123";
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getId()).thenReturn(userId);

        // When
        String token = jwtTokenProvider.generateToken(authentication);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void generateToken_WithUserId_Success() {
        // Given
        String userId = "user456";

        // When
        String token = jwtTokenProvider.generateToken(userId);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void getUserIdFromToken_ValidToken_ReturnsUserId() {
        // Given
        String userId = "testUser";
        String token = jwtTokenProvider.generateToken(userId);

        // When
        String extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        String token = jwtTokenProvider.generateToken("user123");

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Given
        // Create a token with very short expiration (1 millisecond)
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 1L);
        String token = jwtTokenProvider.generateToken("user123");
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_MalformedToken_ReturnsFalse() {
        // Given
        String malformedToken = "this.is.malformed";

        // When
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_EmptyToken_ReturnsFalse() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_NullToken_ReturnsFalse() {
        // Given
        String nullToken = null;

        // When
        boolean isValid = jwtTokenProvider.validateToken(nullToken);

        // Then
        assertFalse(isValid);
    }



    @Test
    void generateToken_MultipleTokens_ShouldBeDifferent() {
        // Given
        String userId = "user123";

        // When
        String token1 = jwtTokenProvider.generateToken(userId);
        // Wait a bit to ensure different issued time
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String token2 = jwtTokenProvider.generateToken(userId);

        // Then
        assertNotEquals(token1, token2);
        assertTrue(jwtTokenProvider.validateToken(token1));
        assertTrue(jwtTokenProvider.validateToken(token2));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token1));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token2));
    }

    @Test
    void getUserIdFromToken_InvalidToken_ThrowsException() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            jwtTokenProvider.getUserIdFromToken(invalidToken);
        });
        assertNotNull(exception);
    }

    @Test
    void generateToken_WithDifferentUserIds_ProducesValidTokens() {
        // Given
        String userId1 = "user1";
        String userId2 = "user2";

        // When
        String token1 = jwtTokenProvider.generateToken(userId1);
        String token2 = jwtTokenProvider.generateToken(userId2);

        // Then
        assertTrue(jwtTokenProvider.validateToken(token1));
        assertTrue(jwtTokenProvider.validateToken(token2));
        assertEquals(userId1, jwtTokenProvider.getUserIdFromToken(token1));
        assertEquals(userId2, jwtTokenProvider.getUserIdFromToken(token2));
        assertNotEquals(token1, token2);
    }

    @Test
    void validateToken_UnsupportedJwtToken_ReturnsFalse() {
        // Given
        // Create a token with unsupported algorithm (using none algorithm won't work with our setup)
        String unsupportedToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0.eyJzdWIiOiJ1c2VyMTIzIn0.";

        // When
        boolean isValid = jwtTokenProvider.validateToken(unsupportedToken);

        // Then
        assertFalse(isValid);
    }
}