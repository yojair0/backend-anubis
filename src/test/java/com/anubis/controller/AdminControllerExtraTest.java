package com.anubis.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.ResponseEntity;

import com.anubis.dto.AdminRegisterRequest;
import com.anubis.dto.ApplicationDetailResponse;
import com.anubis.dto.ApplicationStatusRequest;
import com.anubis.dto.AuthResponse;
import com.anubis.dto.ChangeRoleRequest;
import com.anubis.model.Application;
import com.anubis.model.User;
import com.anubis.model.Role;
import com.anubis.repository.ApplicationRepository;
import com.anubis.repository.PetRepository;
import com.anubis.repository.UserRepository;
import com.anubis.service.ApplicationService;
import com.anubis.service.AuthService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class AdminControllerExtraTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private AdminController adminController;

    private User testUser;
    private Application testApplication;
    private ApplicationDetailResponse testApplicationDetail;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-123");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);

        testApplication = new Application();
        testApplication.setId("app-123");

        testApplicationDetail = new ApplicationDetailResponse();
        testApplicationDetail.setId("app-123");
    }

    @Test
    void getAllUsers_Success_ReturnsUserList() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = adminController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_Exception_Returns500() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<User>> response = adminController.getAllUsers();

        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getAllApplications_Success_ReturnsApplicationList() {
        List<ApplicationDetailResponse> applications = Arrays.asList(testApplicationDetail);
        when(applicationService.getAllDetailedApplications()).thenReturn(applications);

        ResponseEntity<List<ApplicationDetailResponse>> response = adminController.getAllApplications();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(applicationService).getAllDetailedApplications();
    }

    @Test
    void getAllApplications_Exception_Returns500() {
        when(applicationService.getAllDetailedApplications()).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<List<ApplicationDetailResponse>> response = adminController.getAllApplications();

        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void updateApplicationStatus_Success_ReturnsUpdatedApplication() {
        String applicationId = "app-123";
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        when(applicationService.updateApplicationStatusAsAdmin(applicationId, request))
                .thenReturn(testApplication);

        ResponseEntity<Application> response = adminController.updateApplicationStatus(applicationId, request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("app-123", response.getBody().getId());
        verify(applicationService).updateApplicationStatusAsAdmin(applicationId, request);
    }

    @Test
    void updateApplicationStatus_Exception_Returns500() {
        String applicationId = "app-123";
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        when(applicationService.updateApplicationStatusAsAdmin(applicationId, request))
                .thenThrow(new RuntimeException("Update failed"));

        ResponseEntity<Application> response = adminController.updateApplicationStatus(applicationId, request);

        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void deleteApplication_Success_ReturnsSuccessMessage() {
        String applicationId = "app-123";
        doNothing().when(applicationService).deleteApplicationAsAdmin(applicationId);

        ResponseEntity<?> response = adminController.deleteApplication(applicationId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Postulación eliminada exitosamente"));
        verify(applicationService).deleteApplicationAsAdmin(applicationId);
    }

    @Test
    void deleteApplication_Exception_Returns500() {
        String applicationId = "app-123";
        doThrow(new RuntimeException("Delete failed")).when(applicationService).deleteApplicationAsAdmin(applicationId);

        ResponseEntity<?> response = adminController.deleteApplication(applicationId);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error al eliminar postulación"));
    }

    @Test
    void getApplicationById_Found_ReturnsApplication() {
        String applicationId = "app-123";
        when(applicationService.getDetailedApplicationById(applicationId))
                .thenReturn(Optional.of(testApplicationDetail));

        ResponseEntity<ApplicationDetailResponse> response = adminController.getApplicationById(applicationId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("app-123", response.getBody().getId());
        verify(applicationService).getDetailedApplicationById(applicationId);
    }

    @Test
    void getApplicationById_NotFound_Returns404() {
        String applicationId = "app-123";
        when(applicationService.getDetailedApplicationById(applicationId))
                .thenReturn(Optional.empty());

        ResponseEntity<ApplicationDetailResponse> response = adminController.getApplicationById(applicationId);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getApplicationById_Exception_Returns404() {
        String applicationId = "app-123";
        when(applicationService.getDetailedApplicationById(applicationId))
                .thenThrow(new RuntimeException("Service error"));

        ResponseEntity<ApplicationDetailResponse> response = adminController.getApplicationById(applicationId);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void deleteUser_Success_ReturnsSuccessMessage() {
        String userId = "user-123";
        testUser.setRole(Role.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = adminController.deleteUser(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Usuario eliminado exitosamente"));
        verify(userRepository).deleteById(userId);
        verify(applicationRepository).deleteByUserId(userId);
    }

    @Test
    void deleteUser_FoundationUser_DeletesPetsAndUser() {
        String userId = "foundation-123";
        User foundationUser = new User();
        foundationUser.setId(userId);
        foundationUser.setRole(Role.FOUNDATION);
        when(userRepository.findById(userId)).thenReturn(Optional.of(foundationUser));

        ResponseEntity<?> response = adminController.deleteUser(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Usuario eliminado exitosamente"));
        verify(userRepository).deleteById(userId);
        verify(applicationRepository).deleteByUserId(userId);
        verify(petRepository).deleteByFoundationId(userId); // Special for foundation users
    }

    @Test
    void deleteUser_NotFound_Returns404() {
        String userId = "user-123";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = adminController.deleteUser(userId);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Usuario no encontrado"));
    }

    @Test
    void deleteUser_Exception_Returns500() {
        String userId = "user-123";
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Delete error")).when(userRepository).deleteById(userId);

        ResponseEntity<?> response = adminController.deleteUser(userId);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error al eliminar usuario"));
    }

    @Test
    void getStatistics_Success_ReturnsStatistics() {
        when(userRepository.count()).thenReturn(10L);
        when(applicationRepository.count()).thenReturn(25L);
        when(petRepository.count()).thenReturn(15L);

        ResponseEntity<?> response = adminController.getStatistics();

        assertEquals(200, response.getStatusCodeValue());
        String body = response.getBody().toString();
        assertTrue(body.contains("Usuarios: 10"));
        assertTrue(body.contains("Postulaciones: 25"));
        assertTrue(body.contains("Mascotas: 15"));
    }

    @Test
    void getStatistics_Exception_Returns500() {
        when(userRepository.count()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = adminController.getStatistics();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error al obtener estadísticas"));
    }

    @Test
    void countData_Success_ReturnsCountData() {
        when(userRepository.count()).thenReturn(5L);
        when(applicationRepository.count()).thenReturn(12L);

        ResponseEntity<?> response = adminController.countData();

        assertEquals(200, response.getStatusCodeValue());
        String body = response.getBody().toString();
        assertTrue(body.contains("Usuarios: 5"));
        assertTrue(body.contains("Postulaciones: 12"));
    }

    @Test
    void countData_Exception_Returns500() {
        when(userRepository.count()).thenThrow(new RuntimeException("Count error"));

        ResponseEntity<?> response = adminController.countData();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error al contar datos"));
    }

    @Test
    void createUserWithRole_Success_ReturnsAuthResponse() {
        AdminRegisterRequest request = new AdminRegisterRequest();
        request.setEmail("admin@test.com");
        AuthResponse authResponse = new AuthResponse("token", "user-123", "test@example.com", "Test User", Role.USER);
        when(authService.adminRegister(request)).thenReturn(authResponse);

        ResponseEntity<?> response = adminController.createUserWithRole(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(authService).adminRegister(request);
    }

    @Test
    void createUserWithRole_Exception_ReturnsBadRequest() {
        AdminRegisterRequest request = new AdminRegisterRequest();
        when(authService.adminRegister(request)).thenThrow(new RuntimeException("Registration failed"));

        ResponseEntity<?> response = adminController.createUserWithRole(request);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AdminController.MessageResponse);
        AdminController.MessageResponse messageResponse = (AdminController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Error"));
    }

    @Test
    void changeUserRole_Success_ReturnsSuccessMessage() {
        String userId = "user-123";
        ChangeRoleRequest roleRequest = new ChangeRoleRequest();
        roleRequest.setRole(Role.ADMIN);
        
        testUser.setRole(Role.ADMIN);
        when(authService.changeUserRole(userId, Role.ADMIN)).thenReturn(testUser);

        ResponseEntity<?> response = adminController.changeUserRole(userId, roleRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AdminController.MessageResponse);
        AdminController.MessageResponse messageResponse = (AdminController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Rol actualizado exitosamente"));
        verify(authService).changeUserRole(userId, Role.ADMIN);
    }

    @Test
    void changeUserRole_Exception_ReturnsBadRequest() {
        String userId = "user-123";
        ChangeRoleRequest roleRequest = new ChangeRoleRequest();
        roleRequest.setRole(Role.ADMIN);
        
        when(authService.changeUserRole(userId, Role.ADMIN))
                .thenThrow(new RuntimeException("Role change failed"));

        ResponseEntity<?> response = adminController.changeUserRole(userId, roleRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AdminController.MessageResponse);
        AdminController.MessageResponse messageResponse = (AdminController.MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Error"));
    }

    @Test
    void messageResponse_Construction_WorksCorrectly() {
        AdminController.MessageResponse messageResponse = 
                new AdminController.MessageResponse("Test message");

        assertEquals("Test message", messageResponse.getMessage());

        messageResponse.setMessage("Updated message");
        assertEquals("Updated message", messageResponse.getMessage());
    }

    @Test
    void deleteUser_EdgeCaseEmptyOptional_Returns404() {
        String userId = "nonexistent-user";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = adminController.deleteUser(userId);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Usuario no encontrado"));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getApplicationById_EmptyOptional_Returns404() {
        String applicationId = "nonexistent-app";
        when(applicationService.getDetailedApplicationById(applicationId))
                .thenReturn(Optional.empty());

        ResponseEntity<ApplicationDetailResponse> response = adminController.getApplicationById(applicationId);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}