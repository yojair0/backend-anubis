package com.anubis.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.anubis.dto.ApplicationRequest;
import com.anubis.dto.ApplicationStatusRequest;
import com.anubis.model.Application;
import com.anubis.model.ApplicationStatus;
import com.anubis.security.UserPrincipal;
import com.anubis.service.ApplicationService;

@ExtendWith(MockitoExtension.class)
public class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    private Application testApplication1;
    private Application testApplication2;
    private ApplicationRequest applicationRequest;
    private ApplicationStatusRequest statusRequest;
    private UserPrincipal userPrincipal;
    private UserPrincipal foundationPrincipal;
    private UserPrincipal adminPrincipal;

    @BeforeEach
    void setUp() {
        testApplication1 = new Application();
        testApplication1.setId("app1");
        testApplication1.setUserId("user1");
        testApplication1.setPetId("pet1");
        testApplication1.setStatus(ApplicationStatus.PENDING);
        testApplication1.setCreatedAt(LocalDateTime.now());

        testApplication2 = new Application();
        testApplication2.setId("app2");
        testApplication2.setUserId("user1");
        testApplication2.setPetId("pet2");
        testApplication2.setStatus(ApplicationStatus.ACCEPTED);
        testApplication2.setCreatedAt(LocalDateTime.now().minusDays(1));

        applicationRequest = new ApplicationRequest();
        applicationRequest.setPetId("pet1");
        applicationRequest.setReason("Me encantaría adoptar esta mascota");
        applicationRequest.setExperience("Tengo 5 años de experiencia con perros");
        applicationRequest.setLivingSpace("Casa con jardín");
        applicationRequest.setHasOtherPets(false);
        applicationRequest.setWorkSchedule("Tiempo completo");

        statusRequest = new ApplicationStatusRequest();
        statusRequest.setStatus(ApplicationStatus.ACCEPTED);

        userPrincipal = new UserPrincipal("user1", "user@test.com", "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

        foundationPrincipal = new UserPrincipal("foundation1", "foundation@test.com", "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_FOUNDATION")));

        adminPrincipal = new UserPrincipal("admin1", "admin@test.com", "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    // ========== CREATE APPLICATION TESTS ==========
    @Test
    void testCreateApplication_Success() {
        // Given
        Application createdApplication = new Application();
        createdApplication.setId("newApp");
        createdApplication.setUserId("user1");
        createdApplication.setPetId("pet1");
        createdApplication.setStatus(ApplicationStatus.PENDING);

        when(applicationService.createApplication("user1", applicationRequest))
                .thenReturn(createdApplication);

        // When
        ResponseEntity<?> response = applicationController.createApplication(userPrincipal, applicationRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(createdApplication, response.getBody());

        verify(applicationService).createApplication("user1", applicationRequest);
    }

    @Test
    void testCreateApplication_ServiceException() {
        // Given
        when(applicationService.createApplication("user1", applicationRequest))
                .thenThrow(new RuntimeException("Ya tienes una aplicación pendiente para esta mascota"));

        // When
        ResponseEntity<?> response = applicationController.createApplication(userPrincipal, applicationRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).createApplication("user1", applicationRequest);
    }

    // ========== GET MY APPLICATIONS TESTS ==========
    @Test
    void testGetMyApplications_Success() {
        // Given
        List<Application> userApplications = Arrays.asList(testApplication1, testApplication2);
        when(applicationService.getApplicationsByUser("user1")).thenReturn(userApplications);

        // When
        ResponseEntity<?> response = applicationController.getMyApplications(userPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        List<Application> returnedApplications = (List<Application>) response.getBody();
        assertNotNull(returnedApplications);
        assertEquals(2, returnedApplications.size());
        assertTrue(returnedApplications.contains(testApplication1));
        assertTrue(returnedApplications.contains(testApplication2));

        verify(applicationService).getApplicationsByUser("user1");
    }

    @Test
    void testGetMyApplications_EmptyList() {
        // Given
        when(applicationService.getApplicationsByUser("user1")).thenReturn(Arrays.asList());

        // When
        ResponseEntity<?> response = applicationController.getMyApplications(userPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        List<Application> returnedApplications = (List<Application>) response.getBody();
        assertNotNull(returnedApplications);
        assertTrue(returnedApplications.isEmpty());

        verify(applicationService).getApplicationsByUser("user1");
    }

    @Test
    void testGetMyApplications_ServiceException() {
        // Given
        when(applicationService.getApplicationsByUser("user1"))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // When
        ResponseEntity<?> response = applicationController.getMyApplications(userPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).getApplicationsByUser("user1");
    }

    // ========== GET APPLICATIONS BY PET TESTS ==========
    @Test
    void testGetApplicationsByPet_Success() {
        // Given
        List<Application> petApplications = Arrays.asList(testApplication1);
        when(applicationService.getApplicationsByPet("pet1")).thenReturn(petApplications);

        // When
        ResponseEntity<?> response = applicationController.getApplicationsByPet("pet1");

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        List<Application> returnedApplications = (List<Application>) response.getBody();
        assertNotNull(returnedApplications);
        assertEquals(1, returnedApplications.size());
        assertEquals(testApplication1, returnedApplications.get(0));

        verify(applicationService).getApplicationsByPet("pet1");
    }

    @Test
    void testGetApplicationsByPet_ServiceException() {
        // Given
        when(applicationService.getApplicationsByPet("pet1"))
                .thenThrow(new RuntimeException("Mascota no encontrada"));

        // When
        ResponseEntity<?> response = applicationController.getApplicationsByPet("pet1");

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).getApplicationsByPet("pet1");
    }

    // ========== GET FOUNDATION APPLICATIONS TESTS ==========
    @Test
    void testGetFoundationApplications_Success() {
        // Given
        List<Application> foundationApplications = Arrays.asList(testApplication1, testApplication2);
        when(applicationService.getApplicationsByFoundation("foundation1"))
                .thenReturn(foundationApplications);

        // When
        ResponseEntity<?> response = applicationController.getFoundationApplications(foundationPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        List<Application> returnedApplications = (List<Application>) response.getBody();
        assertNotNull(returnedApplications);
        assertEquals(2, returnedApplications.size());

        verify(applicationService).getApplicationsByFoundation("foundation1");
    }

    @Test
    void testGetFoundationApplications_ServiceException() {
        // Given
        when(applicationService.getApplicationsByFoundation("foundation1"))
                .thenThrow(new RuntimeException("Error al obtener aplicaciones"));

        // When
        ResponseEntity<?> response = applicationController.getFoundationApplications(foundationPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).getApplicationsByFoundation("foundation1");
    }

    // ========== UPDATE APPLICATION STATUS TESTS ==========
    @Test
    void testUpdateApplicationStatus_Success_Foundation() {
        // Given
        Application updatedApplication = new Application();
        updatedApplication.setId("app1");
        updatedApplication.setStatus(ApplicationStatus.ACCEPTED);

        when(applicationService.updateApplicationStatus("app1", "foundation1", statusRequest))
                .thenReturn(updatedApplication);

        // When
        ResponseEntity<?> response = applicationController.updateApplicationStatus("app1", foundationPrincipal, statusRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedApplication, response.getBody());

        verify(applicationService).updateApplicationStatus("app1", "foundation1", statusRequest);
    }

    @Test
    void testUpdateApplicationStatus_Success_Admin() {
        // Given
        Application updatedApplication = new Application();
        updatedApplication.setId("app1");
        updatedApplication.setStatus(ApplicationStatus.REJECTED);

        when(applicationService.updateApplicationStatusAsAdmin("app1", statusRequest))
                .thenReturn(updatedApplication);

        // When
        ResponseEntity<?> response = applicationController.updateApplicationStatus("app1", adminPrincipal, statusRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedApplication, response.getBody());

        verify(applicationService).updateApplicationStatusAsAdmin("app1", statusRequest);
    }

    @Test
    void testUpdateApplicationStatus_ServiceException() {
        // Given
        when(applicationService.updateApplicationStatus("app1", "foundation1", statusRequest))
                .thenThrow(new RuntimeException("No tienes permisos para esta aplicación"));

        // When
        ResponseEntity<?> response = applicationController.updateApplicationStatus("app1", foundationPrincipal, statusRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).updateApplicationStatus("app1", "foundation1", statusRequest);
    }

    // ========== GET APPLICATION BY ID TESTS ==========
    @Test
    void testGetApplicationById_Success() {
        // Given
        when(applicationService.getApplicationById("app1")).thenReturn(Optional.of(testApplication1));

        // When
        ResponseEntity<?> response = applicationController.getApplicationById("app1");

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testApplication1, response.getBody());

        verify(applicationService).getApplicationById("app1");
    }

    @Test
    void testGetApplicationById_NotFound() {
        // Given
        when(applicationService.getApplicationById("nonexistent")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = applicationController.getApplicationById("nonexistent");

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).getApplicationById("nonexistent");
    }

    // ========== GET ALL APPLICATIONS TESTS ==========
    @Test
    void testGetAllApplications_Success() {
        // Given
        List<Application> allApplications = Arrays.asList(testApplication1, testApplication2);
        when(applicationService.getAllApplications()).thenReturn(allApplications);

        // When
        ResponseEntity<?> response = applicationController.getAllApplications();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        List<Application> returnedApplications = (List<Application>) response.getBody();
        assertNotNull(returnedApplications);
        assertEquals(2, returnedApplications.size());

        verify(applicationService).getAllApplications();
    }

    @Test
    void testGetAllApplications_ServiceException() {
        // Given
        when(applicationService.getAllApplications())
                .thenThrow(new RuntimeException("Error de base de datos"));

        // When
        ResponseEntity<?> response = applicationController.getAllApplications();

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).getAllApplications();
    }

    // ========== RESPONSE STRUCTURE VALIDATION TESTS ==========
    @Test
    void testMessageResponseStructure() {
        ApplicationController.MessageResponse response = new ApplicationController.MessageResponse("Test message");
        assertEquals("Test message", response.getMessage());

        response.setMessage("Updated message");
        assertEquals("Updated message", response.getMessage());
    }

    // ========== EDGE CASES TESTS ==========
    @Test
    void testCreateApplication_NullRequest() {
        // Given
        when(applicationService.createApplication("user1", null))
                .thenThrow(new RuntimeException("Request inválido"));

        // When
        ResponseEntity<?> response = applicationController.createApplication(userPrincipal, null);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).createApplication("user1", null);
    }

    @Test
    void testUpdateApplicationStatus_NullRequest() {
        // Given
        when(applicationService.updateApplicationStatus("app1", "foundation1", null))
                .thenThrow(new RuntimeException("Request inválido"));

        // When
        ResponseEntity<?> response = applicationController.updateApplicationStatus("app1", foundationPrincipal, null);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).updateApplicationStatus("app1", "foundation1", null);
    }

    // ========== VALIDATION TESTS ==========
    @Test
    void testCreateApplication_ValidationError() {
        // Given
        ApplicationRequest invalidRequest = new ApplicationRequest();
        // Missing required fields

        when(applicationService.createApplication("user1", invalidRequest))
                .thenThrow(new RuntimeException("Campos requeridos faltantes"));

        // When
        ResponseEntity<?> response = applicationController.createApplication(userPrincipal, invalidRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).createApplication("user1", invalidRequest);
    }

    @Test
    void testUpdateApplicationStatus_InvalidStatus() {
        // Given
        ApplicationStatusRequest invalidStatusRequest = new ApplicationStatusRequest();
        invalidStatusRequest.setStatus(null);

        when(applicationService.updateApplicationStatus("app1", "foundation1", invalidStatusRequest))
                .thenThrow(new RuntimeException("Estado inválido"));

        // When
        ResponseEntity<?> response = applicationController.updateApplicationStatus("app1", foundationPrincipal, invalidStatusRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApplicationController.MessageResponse);

        verify(applicationService).updateApplicationStatus("app1", "foundation1", invalidStatusRequest);
    }

    // ========== MULTIPLE APPLICATIONS WORKFLOW TESTS ==========
    @Test
    void testMultipleApplicationsWorkflow() {
        // Given - User has multiple applications
        List<Application> userApplications = Arrays.asList(testApplication1, testApplication2);
        when(applicationService.getApplicationsByUser("user1")).thenReturn(userApplications);

        // Given - Foundation has same applications
        when(applicationService.getApplicationsByFoundation("foundation1")).thenReturn(userApplications);

        // Given - Admin can see all
        when(applicationService.getAllApplications()).thenReturn(userApplications);

        // When - Execute multiple operations
        ResponseEntity<?> userAppsResponse = applicationController.getMyApplications(userPrincipal);
        ResponseEntity<?> foundationAppsResponse = applicationController.getFoundationApplications(foundationPrincipal);
        ResponseEntity<?> allAppsResponse = applicationController.getAllApplications();

        // Then - All operations successful
        assertEquals(200, userAppsResponse.getStatusCodeValue());
        assertEquals(200, foundationAppsResponse.getStatusCodeValue());
        assertEquals(200, allAppsResponse.getStatusCodeValue());

        // Verify service calls
        verify(applicationService).getApplicationsByUser("user1");
        verify(applicationService).getApplicationsByFoundation("foundation1");
        verify(applicationService).getAllApplications();
    }

    // ========== STATUS CHANGE WORKFLOW TESTS ==========
    @Test
    void testApplicationStatusWorkflow() {
        // Given - Initial application creation
        Application createdApp = new Application();
        createdApp.setId("workflow-app");
        createdApp.setStatus(ApplicationStatus.PENDING);

        when(applicationService.createApplication("user1", applicationRequest)).thenReturn(createdApp);

        // Given - Status updates
        Application approvedApp = new Application();
        approvedApp.setId("workflow-app");
        approvedApp.setStatus(ApplicationStatus.ACCEPTED);

        Application rejectedApp = new Application();
        rejectedApp.setId("workflow-app");
        rejectedApp.setStatus(ApplicationStatus.REJECTED);

        ApplicationStatusRequest approveRequest = new ApplicationStatusRequest();
        approveRequest.setStatus(ApplicationStatus.ACCEPTED);

        ApplicationStatusRequest rejectRequest = new ApplicationStatusRequest();
        rejectRequest.setStatus(ApplicationStatus.REJECTED);

        when(applicationService.updateApplicationStatus("workflow-app", "foundation1", approveRequest))
                .thenReturn(approvedApp);
        when(applicationService.updateApplicationStatus("workflow-app", "foundation1", rejectRequest))
                .thenReturn(rejectedApp);

        // When - Execute workflow
        ResponseEntity<?> createResponse = applicationController.createApplication(userPrincipal, applicationRequest);
        ResponseEntity<?> approveResponse = applicationController.updateApplicationStatus("workflow-app", foundationPrincipal, approveRequest);
        ResponseEntity<?> rejectResponse = applicationController.updateApplicationStatus("workflow-app", foundationPrincipal, rejectRequest);

        // Then - All operations successful
        assertEquals(200, createResponse.getStatusCodeValue());
        assertEquals(200, approveResponse.getStatusCodeValue());
        assertEquals(200, rejectResponse.getStatusCodeValue());

        // Verify service calls
        verify(applicationService).createApplication("user1", applicationRequest);
        verify(applicationService).updateApplicationStatus("workflow-app", "foundation1", approveRequest);
        verify(applicationService).updateApplicationStatus("workflow-app", "foundation1", rejectRequest);
    }

    // ========== COMPREHENSIVE ERROR HANDLING TESTS ==========
    @Test
    void testErrorHandlingComprehensive() {
        // Given - Various service exceptions
        when(applicationService.createApplication(anyString(), any())).thenThrow(new RuntimeException("Create error"));
        when(applicationService.getApplicationsByUser(anyString())).thenThrow(new RuntimeException("Get user error"));
        when(applicationService.getApplicationsByPet(anyString())).thenThrow(new RuntimeException("Get pet error"));
        when(applicationService.getApplicationsByFoundation(anyString())).thenThrow(new RuntimeException("Get foundation error"));
        when(applicationService.updateApplicationStatus(anyString(), anyString(), any())).thenThrow(new RuntimeException("Update error"));
        when(applicationService.getApplicationById(anyString())).thenThrow(new RuntimeException("Get by ID error"));
        when(applicationService.getAllApplications()).thenThrow(new RuntimeException("Get all error"));

        // When - Execute all operations expecting errors
        ResponseEntity<?> createResponse = applicationController.createApplication(userPrincipal, applicationRequest);
        ResponseEntity<?> getUserResponse = applicationController.getMyApplications(userPrincipal);
        ResponseEntity<?> getPetResponse = applicationController.getApplicationsByPet("pet1");
        ResponseEntity<?> getFoundationResponse = applicationController.getFoundationApplications(foundationPrincipal);
        ResponseEntity<?> updateResponse = applicationController.updateApplicationStatus("app1", foundationPrincipal, statusRequest);
        ResponseEntity<?> getByIdResponse = applicationController.getApplicationById("app1");
        ResponseEntity<?> getAllResponse = applicationController.getAllApplications();

        // Then - All return 400 with error messages
        assertEquals(400, createResponse.getStatusCodeValue());
        assertEquals(400, getUserResponse.getStatusCodeValue());
        assertEquals(400, getPetResponse.getStatusCodeValue());
        assertEquals(400, getFoundationResponse.getStatusCodeValue());
        assertEquals(400, updateResponse.getStatusCodeValue());
        assertEquals(400, getByIdResponse.getStatusCodeValue());
        assertEquals(400, getAllResponse.getStatusCodeValue());

        // Verify all responses are MessageResponse
        assertTrue(createResponse.getBody() instanceof ApplicationController.MessageResponse);
        assertTrue(getUserResponse.getBody() instanceof ApplicationController.MessageResponse);
        assertTrue(getPetResponse.getBody() instanceof ApplicationController.MessageResponse);
        assertTrue(getFoundationResponse.getBody() instanceof ApplicationController.MessageResponse);
        assertTrue(updateResponse.getBody() instanceof ApplicationController.MessageResponse);
        assertTrue(getByIdResponse.getBody() instanceof ApplicationController.MessageResponse);
        assertTrue(getAllResponse.getBody() instanceof ApplicationController.MessageResponse);
    }
}