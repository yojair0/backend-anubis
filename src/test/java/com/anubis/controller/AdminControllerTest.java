package com.anubis.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.anubis.model.User;
import com.anubis.repository.ApplicationRepository;
import com.anubis.repository.PetRepository;
import com.anubis.repository.UserRepository;
import com.anubis.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AdminController adminController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user1");
        testUser.setEmail("user@test.com");
        testUser.setFullName("Test User");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        ResponseEntity<List<User>> response = adminController.getAllUsers();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(testUser, response.getBody().get(0));

        verify(userRepository).findAll();
    }

    @Test
    void testGetAllUsers_ServiceException() {
        // Given
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<List<User>> response = adminController.getAllUsers();

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());

        verify(userRepository).findAll();
    }

    @Test
    void testGetStatistics_Success() {
        // Given
        when(userRepository.count()).thenReturn(10L);
        when(applicationRepository.count()).thenReturn(5L);
        when(petRepository.count()).thenReturn(15L);

        // When
        ResponseEntity<?> response = adminController.getStatistics();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        verify(userRepository).count();
        verify(applicationRepository).count();
        verify(petRepository).count();
    }

    @Test
    void testGetStatistics_Exception() {
        // Given
        when(userRepository.count()).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<?> response = adminController.getStatistics();

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());

        verify(userRepository).count();
    }

    @Test
    void testCountData_Success() {
        // Given
        when(userRepository.count()).thenReturn(8L);
        when(applicationRepository.count()).thenReturn(3L);

        // When
        ResponseEntity<?> response = adminController.countData();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        verify(userRepository).count();
        verify(applicationRepository).count();
    }

    @Test
    void testCountData_Exception() {
        // Given
        when(userRepository.count()).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<?> response = adminController.countData();

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());

        verify(userRepository).count();
    }
}