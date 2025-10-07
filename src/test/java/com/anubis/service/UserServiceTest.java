package com.anubis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.anubis.model.Role;
import com.anubis.model.User;
import com.anubis.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser1;
    private User testUser2;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Setup test users
        testUser1 = new User();
        testUser1.setId("user1");
        testUser1.setEmail("user1@example.com");
        testUser1.setFullName("Test User 1");
        testUser1.setPhone("123456789");
        testUser1.setRole(Role.USER);
        testUser1.setActive(true);
        testUser1.setCreatedAt(LocalDateTime.now().minusDays(1));

        testUser2 = new User();
        testUser2.setId("user2");
        testUser2.setEmail("user2@example.com");
        testUser2.setFullName("Test User 2");
        testUser2.setPhone("987654321");
        testUser2.setRole(Role.USER);
        testUser2.setActive(true);
        testUser2.setCreatedAt(LocalDateTime.now().minusDays(2));

        adminUser = new User();
        adminUser.setId("admin1");
        adminUser.setEmail("admin@example.com");
        adminUser.setFullName("Admin User");
        adminUser.setPhone("555000111");
        adminUser.setRole(Role.ADMIN);
        adminUser.setActive(true);
        adminUser.setCreatedAt(LocalDateTime.now().minusDays(3));
    }

    // ========== GET ALL USERS TESTS ==========
    @Test
    void testGetAllUsers_Success() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser1, testUser2, adminUser);
        when(userRepository.findByActiveTrue()).thenReturn(expectedUsers);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository).findByActiveTrue();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        // Given
        when(userRepository.findByActiveTrue()).thenReturn(Arrays.asList());

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findByActiveTrue();
    }

    // ========== GET USERS BY ROLE TESTS ==========
    @Test
    void testGetUsersByRole_Users() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser1, testUser2);
        when(userRepository.findByRoleAndActiveTrue(Role.USER)).thenReturn(expectedUsers);

        // When
        List<User> result = userService.getUsersByRole(Role.USER);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testUser1));
        assertTrue(result.contains(testUser2));
        assertFalse(result.contains(adminUser));
        verify(userRepository).findByRoleAndActiveTrue(Role.USER);
    }

    @Test
    void testGetUsersByRole_Admins() {
        // Given
        List<User> expectedAdmins = Arrays.asList(adminUser);
        when(userRepository.findByRoleAndActiveTrue(Role.ADMIN)).thenReturn(expectedAdmins);

        // When
        List<User> result = userService.getUsersByRole(Role.ADMIN);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(adminUser, result.get(0));
        verify(userRepository).findByRoleAndActiveTrue(Role.ADMIN);
    }

    @Test
    void testGetUsersByRole_NoUsersFound() {
        // Given
        when(userRepository.findByRoleAndActiveTrue(Role.USER)).thenReturn(Arrays.asList());

        // When
        List<User> result = userService.getUsersByRole(Role.USER);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findByRoleAndActiveTrue(Role.USER);
    }

    // ========== GET USER BY ID TESTS ==========
    @Test
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser1));

        // When
        Optional<User> result = userService.getUserById("user1");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser1, result.get());
        verify(userRepository).findById("user1");
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById("nonexistent");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById("nonexistent");
    }

    // ========== GET USER BY EMAIL TESTS ==========
    @Test
    void testGetUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(testUser1));

        // When
        Optional<User> result = userService.getUserByEmail("user1@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser1, result.get());
        verify(userRepository).findByEmail("user1@example.com");
    }

    @Test
    void testGetUserByEmail_NotFound() {
        // Given
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserByEmail("notfound@example.com");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("notfound@example.com");
    }

    // ========== UPDATE USER PROFILE TESTS ==========
    @Test
    void testUpdateUserProfile_Success() {
        // Given
        User updatedData = new User();
        updatedData.setFullName("Updated Name");
        updatedData.setPhone("999888777");

        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.updateUserProfile("user1", updatedData);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getFullName());
        assertEquals("999888777", result.getPhone());
        assertNotNull(result.getUpdatedAt());
        verify(userRepository).findById("user1");
        verify(userRepository).save(testUser1);
    }

    @Test
    void testUpdateUserProfile_PartialUpdate() {
        // Given
        User updatedData = new User();
        updatedData.setFullName("Updated Name Only");
        // No phone update

        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.updateUserProfile("user1", updatedData);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name Only", result.getFullName());
        assertEquals("123456789", result.getPhone()); // Original phone unchanged
        assertNotNull(result.getUpdatedAt());
        verify(userRepository).findById("user1");
        verify(userRepository).save(testUser1);
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
        // Given
        User updatedData = new User();
        updatedData.setFullName("Updated Name");
        
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserProfile("nonexistent", updatedData);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository).findById("nonexistent");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserProfile_NullValues() {
        // Given
        User updatedData = new User();
        // All fields are null

        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String originalName = testUser1.getFullName();
        String originalPhone = testUser1.getPhone();

        // When
        User result = userService.updateUserProfile("user1", updatedData);

        // Then
        assertNotNull(result);
        assertEquals(originalName, result.getFullName()); // No change
        assertEquals(originalPhone, result.getPhone()); // No change
        assertNotNull(result.getUpdatedAt()); // Should still update timestamp
        verify(userRepository).save(testUser1);
    }

    // ========== DELETE USER TESTS ==========
    @Test
    void testDeleteUser_Success() {
        // Given
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        userService.deleteUser("user1");

        // Then
        assertFalse(testUser1.isActive()); // Soft delete
        assertNotNull(testUser1.getUpdatedAt());
        verify(userRepository).findById("user1");
        verify(userRepository).save(testUser1);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser("nonexistent");
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository).findById("nonexistent");
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== EXISTS BY EMAIL TESTS ==========
    @Test
    void testExistsByEmail_True() {
        // Given
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);

        // When
        boolean result = userService.existsByEmail("user1@example.com");

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("user1@example.com");
    }

    @Test
    void testExistsByEmail_False() {
        // Given
        when(userRepository.existsByEmail("notfound@example.com")).thenReturn(false);

        // When
        boolean result = userService.existsByEmail("notfound@example.com");

        // Then
        assertFalse(result);
        verify(userRepository).existsByEmail("notfound@example.com");
    }

    // ========== COUNT USERS BY ROLE TESTS ==========
    @Test
    void testCountUsersByRole_Users() {
        // Given
        List<User> users = Arrays.asList(testUser1, testUser2);
        when(userRepository.findByRoleAndActiveTrue(Role.USER)).thenReturn(users);

        // When
        long result = userService.countUsersByRole(Role.USER);

        // Then
        assertEquals(2, result);
        verify(userRepository).findByRoleAndActiveTrue(Role.USER);
    }

    @Test
    void testCountUsersByRole_NoUsers() {
        // Given
        when(userRepository.findByRoleAndActiveTrue(Role.ADMIN)).thenReturn(Arrays.asList());

        // When
        long result = userService.countUsersByRole(Role.ADMIN);

        // Then
        assertEquals(0, result);
        verify(userRepository).findByRoleAndActiveTrue(Role.ADMIN);
    }

    // ========== EDGE CASES AND ERROR HANDLING ==========
    @Test
    void testUpdateUserProfile_NullUserId() {
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.updateUserProfile(null, new User());
        });
    }

    @Test
    void testDeleteUser_NullUserId() {
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(null);
        });
    }
}