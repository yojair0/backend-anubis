package com.anubis.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.anubis.model.Role;
import com.anubis.model.User;
import com.anubis.security.UserPrincipal;
import com.anubis.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private User testFoundation;
    private User adminUser;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user1");
        testUser.setEmail("user1@example.com");
        testUser.setFullName("Test User 1");
        testUser.setRole(Role.USER);
        testUser.setPassword("hashedPassword");

        testFoundation = new User();
        testFoundation.setId("foundation1");
        testFoundation.setEmail("foundation@example.com");
        testFoundation.setFullName("Test Foundation");
        testFoundation.setRole(Role.FOUNDATION);
        testFoundation.setPassword("hashedPassword");

        adminUser = new User();
        adminUser.setId("admin1");
        adminUser.setEmail("admin@example.com");
        adminUser.setFullName("Admin User");
        adminUser.setRole(Role.ADMIN);
        adminUser.setPassword("hashedPassword");

        userPrincipal = new UserPrincipal("user1", "user1@example.com", "hashedPassword", null);
    }

    // ========== GET CURRENT USER TESTS ==========
    @Test
    void testGetCurrentUser_Success() {
        // Given
        when(userService.getUserById("user1")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<?> response = userController.getCurrentUser(userPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User returnedUser = (User) response.getBody();
        assertNotNull(returnedUser);
        assertEquals("user1", returnedUser.getId());
        assertNull(returnedUser.getPassword()); // Password should be null

        verify(userService).getUserById("user1");
    }

    @Test
    void testGetCurrentUser_NotFound() {
        // Given
        when(userService.getUserById("user1")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = userController.getCurrentUser(userPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);

        verify(userService).getUserById("user1");
    }

    // ========== UPDATE PROFILE TESTS ==========
    @Test
    void testUpdateProfile_Success() {
        // Given
        User updateData = new User();
        updateData.setFullName("Updated Name");
        updateData.setPhone("987654321");

        User updatedUser = new User();
        updatedUser.setId("user1");
        updatedUser.setFullName("Updated Name");
        updatedUser.setPhone("987654321");
        updatedUser.setPassword("hashedPassword");

        when(userService.updateUserProfile("user1", updateData)).thenReturn(updatedUser);

        // When
        ResponseEntity<?> response = userController.updateProfile(userPrincipal, updateData);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User returnedUser = (User) response.getBody();
        assertNotNull(returnedUser);
        assertEquals("Updated Name", returnedUser.getFullName());
        assertNull(returnedUser.getPassword()); // Password should be null

        verify(userService).updateUserProfile("user1", updateData);
    }

    @Test
    void testUpdateProfile_ServiceException() {
        // Given
        User updateData = new User();
        updateData.setFullName("Updated Name");

        when(userService.updateUserProfile("user1", updateData))
            .thenThrow(new RuntimeException("Error al actualizar"));

        // When
        ResponseEntity<?> response = userController.updateProfile(userPrincipal, updateData);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);

        verify(userService).updateUserProfile("user1", updateData);
    }

    // ========== GET FOUNDATIONS TESTS ==========
    @Test
    void testGetFoundations_Success() {
        // Given
        List<User> foundations = Arrays.asList(testFoundation);
        when(userService.getUsersByRole(Role.FOUNDATION)).thenReturn(foundations);

        // When
        ResponseEntity<?> response = userController.getFoundations();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        List<User> returnedFoundations = (List<User>) response.getBody();
        assertNotNull(returnedFoundations);
        assertEquals(1, returnedFoundations.size());
        assertEquals(Role.FOUNDATION, returnedFoundations.get(0).getRole());
        assertNull(returnedFoundations.get(0).getPassword()); // Password should be null

        verify(userService).getUsersByRole(Role.FOUNDATION);
    }

    @Test
    void testGetFoundations_ServiceException() {
        // Given
        when(userService.getUsersByRole(Role.FOUNDATION))
            .thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<?> response = userController.getFoundations();

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);

        verify(userService).getUsersByRole(Role.FOUNDATION);
    }

    // ========== DELETE USER TESTS ==========
    @Test
    void testDeleteUser_Success() {
        // Given
        doNothing().when(userService).deleteUser("user1");

        // When
        ResponseEntity<?> response = userController.deleteUser("user1");

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);

        UserController.MessageResponse messageResponse = (UserController.MessageResponse) response.getBody();
        assertEquals("Usuario eliminado exitosamente", messageResponse.getMessage());

        verify(userService).deleteUser("user1");
    }

    @Test
    void testDeleteUser_ServiceException() {
        // Given
        doThrow(new RuntimeException("Usuario no encontrado"))
            .when(userService).deleteUser("nonexistent");

        // When
        ResponseEntity<?> response = userController.deleteUser("nonexistent");

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);

        verify(userService).deleteUser("nonexistent");
    }

    // ========== GET ALL USERS TESTS ==========
    @Test
    void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser, testFoundation, adminUser);
        when(userService.getAllUsers()).thenReturn(users);

        // When
        ResponseEntity<?> response = userController.getAllUsers();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        List<User> returnedUsers = (List<User>) response.getBody();
        assertNotNull(returnedUsers);
        assertEquals(3, returnedUsers.size());

        // Verify passwords are null
        for (User user : returnedUsers) {
            assertNull(user.getPassword());
        }

        verify(userService).getAllUsers();
    }

    @Test
    void testGetAllUsers_ServiceException() {
        // Given
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database connection error"));

        // When
        ResponseEntity<?> response = userController.getAllUsers();

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);

        verify(userService).getAllUsers();
    }

    // ========== GET CURRENT USER ROLE TESTS ==========
    @Test
    void testGetCurrentUserRole_Success() {
        // Given
        when(userService.getUserById("user1")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<?> response = userController.getCurrentUserRole(userPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.RoleResponse);

        UserController.RoleResponse roleResponse = (UserController.RoleResponse) response.getBody();
        assertEquals(Role.USER, roleResponse.getRole());

        verify(userService).getUserById("user1");
    }

    @Test
    void testGetCurrentUserRole_UserNotFound() {
        // Given
        when(userService.getUserById("user1")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = userController.getCurrentUserRole(userPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);

        verify(userService).getUserById("user1");
    }

    // ========== RESPONSE STRUCTURE VALIDATION TESTS ==========
    @Test
    void testMessageResponseStructure() {
        UserController.MessageResponse msgResponse = new UserController.MessageResponse("Test message");
        assertEquals("Test message", msgResponse.getMessage());

        msgResponse.setMessage("Updated message");
        assertEquals("Updated message", msgResponse.getMessage());
    }

    @Test
    void testRoleResponseStructure() {
        UserController.RoleResponse roleResponse = new UserController.RoleResponse(Role.USER);
        assertEquals(Role.USER, roleResponse.getRole());

        roleResponse.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, roleResponse.getRole());
    }

    // ========== EDGE CASES TESTS ==========
    @Test
    void testUpdateProfile_NullData() {
        // Given
        when(userService.updateUserProfile("user1", null))
            .thenThrow(new RuntimeException("Datos inválidos"));

        // When
        ResponseEntity<?> response = userController.updateProfile(userPrincipal, null);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);

        verify(userService).updateUserProfile("user1", null);
    }

    @Test
    void testDeleteUser_EmptyId() {
        // Given
        doThrow(new RuntimeException("ID inválido"))
            .when(userService).deleteUser("");

        // When
        ResponseEntity<?> response = userController.deleteUser("");

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserController.MessageResponse);

        verify(userService).deleteUser("");
    }

    // ========== MULTIPLE USERS VALIDATION TESTS ==========
    @Test
    void testGetAllUsersPasswordsRemoved() {
        // Given
        User user1 = new User();
        user1.setId("user1");
        user1.setPassword("password1");

        User user2 = new User();
        user2.setId("user2");
        user2.setPassword("password2");

        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // When
        ResponseEntity<?> response = userController.getAllUsers();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        List<User> returnedUsers = (List<User>) response.getBody();
        assertNotNull(returnedUsers);

        for (User user : returnedUsers) {
            assertNull(user.getPassword(), "Password should be null for user: " + user.getId());
        }

        verify(userService).getAllUsers();
    }

    @Test
    void testGetFoundationsPasswordsRemoved() {
        // Given
        User foundation1 = new User();
        foundation1.setId("foundation1");
        foundation1.setPassword("foundationPass1");
        foundation1.setRole(Role.FOUNDATION);

        User foundation2 = new User();
        foundation2.setId("foundation2");
        foundation2.setPassword("foundationPass2");
        foundation2.setRole(Role.FOUNDATION);

        List<User> foundations = Arrays.asList(foundation1, foundation2);
        when(userService.getUsersByRole(Role.FOUNDATION)).thenReturn(foundations);

        // When
        ResponseEntity<?> response = userController.getFoundations();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        List<User> returnedFoundations = (List<User>) response.getBody();
        assertNotNull(returnedFoundations);

        for (User foundation : returnedFoundations) {
            assertNull(foundation.getPassword(), "Password should be null for foundation: " + foundation.getId());
        }

        verify(userService).getUsersByRole(Role.FOUNDATION);
    }

    // ========== COMPREHENSIVE OPERATION TESTS ==========
    @Test
    void testCompleteUserWorkflow() {
        // Given
        when(userService.getUserById("user1")).thenReturn(Optional.of(testUser));

        User updateData = new User();
        updateData.setFullName("Updated Name");

        User updatedUser = new User();
        updatedUser.setId("user1");
        updatedUser.setFullName("Updated Name");
        updatedUser.setPassword("hashedPassword");

        when(userService.updateUserProfile("user1", updateData)).thenReturn(updatedUser);
        doNothing().when(userService).deleteUser("user1");

        // When - Get current user
        ResponseEntity<?> getUserResponse = userController.getCurrentUser(userPrincipal);

        // When - Update profile
        ResponseEntity<?> updateResponse = userController.updateProfile(userPrincipal, updateData);

        // When - Get role
        ResponseEntity<?> roleResponse = userController.getCurrentUserRole(userPrincipal);

        // When - Delete user
        ResponseEntity<?> deleteResponse = userController.deleteUser("user1");

        // Then - All operations successful
        assertEquals(200, getUserResponse.getStatusCodeValue());
        assertEquals(200, updateResponse.getStatusCodeValue());
        assertEquals(200, roleResponse.getStatusCodeValue());
        assertEquals(200, deleteResponse.getStatusCodeValue());

        // Verify all service calls
        verify(userService, times(2)).getUserById("user1"); // Called for get and role
        verify(userService).updateUserProfile("user1", updateData);
        verify(userService).deleteUser("user1");
    }
}