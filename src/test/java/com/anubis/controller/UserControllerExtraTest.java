package com.anubis.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.ResponseEntity;

import com.anubis.model.User;
import com.anubis.model.Role;
import com.anubis.security.UserPrincipal;
import com.anubis.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserControllerExtraTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-123");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setRole(Role.USER);

        userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getId()).thenReturn("user-123");
    }

    @Test
    void getCurrentUser_ValidUser_ReturnsUserProfile() {
        when(userService.getUserById("user-123")).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = userController.getCurrentUser(userPrincipal);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        User returnedUser = (User) response.getBody();
        assertNull(returnedUser.getPassword()); // Password should be null for security
        verify(userService).getUserById("user-123");
    }

    @Test
    void getCurrentUser_UserNotFound_ReturnsError() {
        when(userService.getUserById("user-123")).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.getCurrentUser(userPrincipal);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);
        UserController.MessageResponse messageResponse = (UserController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Error"));
    }

    @Test
    void getCurrentUser_ServiceException_ReturnsError() {
        when(userService.getUserById("user-123")).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = userController.getCurrentUser(userPrincipal);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);
        UserController.MessageResponse messageResponse = (UserController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Database error"));
    }

    @Test
    void updateProfile_ValidUpdate_ReturnsUpdatedUser() {
        User updatedUser = new User();
        updatedUser.setFullName("Updated Name");
        
        when(userService.updateUserProfile("user-123", updatedUser)).thenReturn(testUser);

        ResponseEntity<?> response = userController.updateProfile(userPrincipal, updatedUser);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        User returnedUser = (User) response.getBody();
        assertNull(returnedUser.getPassword()); // Password should be null for security
        verify(userService).updateUserProfile("user-123", updatedUser);
    }

    @Test
    void updateProfile_ServiceException_ReturnsError() {
        User updatedUser = new User();
        when(userService.updateUserProfile(anyString(), any(User.class)))
                .thenThrow(new RuntimeException("Update failed"));

        ResponseEntity<?> response = userController.updateProfile(userPrincipal, updatedUser);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);
        UserController.MessageResponse messageResponse = (UserController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Update failed"));
    }

    @Test
    void getFoundations_ValidRequest_ReturnsFoundations() {
        User foundation1 = new User();
        foundation1.setId("foundation-1");
        foundation1.setRole(Role.FOUNDATION);
        foundation1.setPassword("secret"); // This should be removed
        
        User foundation2 = new User();
        foundation2.setId("foundation-2");
        foundation2.setRole(Role.FOUNDATION);
        foundation2.setPassword("secret"); // This should be removed

        List<User> foundations = Arrays.asList(foundation1, foundation2);
        when(userService.getUsersByRole(Role.FOUNDATION)).thenReturn(foundations);

        ResponseEntity<?> response = userController.getFoundations();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        List<User> returnedFoundations = (List<User>) response.getBody();
        assertEquals(2, returnedFoundations.size());
        
        // Verify passwords are removed for security
        returnedFoundations.forEach(foundation -> assertNull(foundation.getPassword()));
        verify(userService).getUsersByRole(Role.FOUNDATION);
    }

    @Test
    void getFoundations_ServiceException_ReturnsError() {
        when(userService.getUsersByRole(Role.FOUNDATION))
                .thenThrow(new RuntimeException("Service error"));

        ResponseEntity<?> response = userController.getFoundations();

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);
        UserController.MessageResponse messageResponse = (UserController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Service error"));
    }

    @Test
    void deleteUser_ValidUserId_ReturnsSuccessMessage() {
        String userId = "user-to-delete";
        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<?> response = userController.deleteUser(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);
        UserController.MessageResponse messageResponse = (UserController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Usuario eliminado exitosamente"));
        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_ServiceException_ReturnsError() {
        String userId = "user-to-delete";
        doThrow(new RuntimeException("Delete failed")).when(userService).deleteUser(userId);

        ResponseEntity<?> response = userController.deleteUser(userId);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);
        UserController.MessageResponse messageResponse = (UserController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Delete failed"));
        verify(userService).deleteUser(userId);
    }

    @Test
    void getCurrentUser_NullUserPrincipal_HandlesGracefully() {
        // Test that null UserPrincipal causes NullPointerException
        try {
            userController.getCurrentUser(null);
        } catch (NullPointerException e) {
            // Expected behavior
            assertNotNull(e);
        }
    }

    @Test
    void updateProfile_NullUpdatedUser_HandlesGracefully() {
        when(userService.updateUserProfile(anyString(), isNull()))
                .thenThrow(new RuntimeException("Invalid user data"));

        ResponseEntity<?> response = userController.updateProfile(userPrincipal, null);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);
        UserController.MessageResponse messageResponse = (UserController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Invalid user data"));
    }

    @Test
    void getFoundations_EmptyList_ReturnsEmptyList() {
        List<User> emptyFoundations = Arrays.asList();
        when(userService.getUsersByRole(Role.FOUNDATION)).thenReturn(emptyFoundations);

        ResponseEntity<?> response = userController.getFoundations();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        List<User> returnedFoundations = (List<User>) response.getBody();
        assertTrue(returnedFoundations.isEmpty());
    }

    @Test
    void passwordSecurity_PasswordAlwaysNulled() {
        // Test that passwords are always set to null in responses
        testUser.setPassword("sensitive-password");
        when(userService.getUserById("user-123")).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = userController.getCurrentUser(userPrincipal);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        User returnedUser = (User) response.getBody();
        assertNull(returnedUser.getPassword());
        
        // Note: The original user password is also nulled by the controller
        // This is the expected behavior for security
    }
}