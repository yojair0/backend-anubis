package com.anubis.security;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.anubis.model.Role;
import com.anubis.model.User;
import com.anubis.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setId("user123");
        user.setEmail(email);
        user.setFullName("Test User");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEmailVerified(true);
        user.setActive(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals("user123", ((UserPrincipal) userDetails).getId());
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_UserNotExists_ThrowsException() {
        // Given
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername(email)
        );
        
        assertEquals("Usuario no encontrado con email: " + email, exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserById_UserExists_ReturnsUserDetails() {
        // Given
        String userId = "user123";
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPassword("encodedPassword");
        user.setRole(Role.FOUNDATION);
        user.setEmailVerified(true);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        // Then
        assertNotNull(userDetails);
        assertEquals(userId, ((UserPrincipal) userDetails).getId());
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        
        verify(userRepository).findById(userId);
    }

    @Test
    void loadUserById_UserNotExists_ThrowsException() {
        // Given
        String userId = "notfound123";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserById(userId)
        );
        
        assertEquals("Usuario no encontrado con id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void loadUserByUsername_AdminUser_ReturnsUserDetailsWithAdminRole() {
        // Given
        String email = "admin@example.com";
        User user = new User();
        user.setId("admin123");
        user.setEmail(email);
        user.setFullName("Admin User");
        user.setPassword("encodedPassword");
        user.setRole(Role.ADMIN);
        user.setEmailVerified(true);
        user.setActive(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;
        assertEquals("admin123", userPrincipal.getId());
        assertEquals(email, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_InactiveUser_ReturnsUserDetails() {
        // Given
        String email = "inactive@example.com";
        User user = new User();
        user.setId("user123");
        user.setEmail(email);
        user.setFullName("Inactive User");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEmailVerified(false);
        user.setActive(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;
        assertEquals("user123", userPrincipal.getId());
        assertEquals(email, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserById_FoundationUser_ReturnsUserDetailsWithFoundationRole() {
        // Given
        String userId = "foundation123";
        User user = new User();
        user.setId(userId);
        user.setEmail("foundation@example.com");
        user.setFullName("Foundation User");
        user.setPassword("encodedPassword");
        user.setRole(Role.FOUNDATION);
        user.setEmailVerified(true);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        // Then
        assertNotNull(userDetails);
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;
        assertEquals(userId, userPrincipal.getId());
        assertEquals("foundation@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_FOUNDATION")));
        
        verify(userRepository).findById(userId);
    }

    @Test
    void loadUserByUsername_NullEmail_ThrowsException() {
        // Given
        String email = null;
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername(email)
        );
        
        assertEquals("Usuario no encontrado con email: null", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserById_NullId_ThrowsException() {
        // Given
        String userId = null;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserById(userId)
        );
        
        assertEquals("Usuario no encontrado con id: null", exception.getMessage());
        verify(userRepository).findById(userId);
    }
}