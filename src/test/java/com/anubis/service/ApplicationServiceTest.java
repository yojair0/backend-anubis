package com.anubis.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.anubis.dto.ApplicationDetailResponse;
import com.anubis.dto.ApplicationRequest;
import com.anubis.dto.ApplicationStatusRequest;
import com.anubis.model.Application;
import com.anubis.model.ApplicationStatus;
import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import com.anubis.model.User;
import com.anubis.repository.ApplicationRepository;
import com.anubis.repository.PetRepository;
import com.anubis.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ApplicationService applicationService;

    private User testUser;
    private Pet testPet;
    private Application testApplication;
    private ApplicationRequest testApplicationRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user123");
        testUser.setFullName("Juan Test");
        testUser.setEmail("test@example.com");

        testPet = new Pet();
        testPet.setId("pet123");
        testPet.setName("Buddy");
        testPet.setStatus(PetStatus.AVAILABLE);
        testPet.setFoundationId("foundation123");

        testApplication = new Application();
        testApplication.setId("app123");
        testApplication.setUserId("user123");
        testApplication.setPetId("pet123");
        testApplication.setStatus(ApplicationStatus.PENDING);
        testApplication.setReason("Me encantan los perros");
        testApplication.setExperience("5 años con mascotas");
        testApplication.setLivingSpace("Casa con jardín");
        testApplication.setHasOtherPets(false);
        testApplication.setWorkSchedule("Medio tiempo");

        testApplicationRequest = new ApplicationRequest();
        testApplicationRequest.setPetId("pet123");
        testApplicationRequest.setReason("Me encantan los perros");
        testApplicationRequest.setExperience("5 años con mascotas");
        testApplicationRequest.setLivingSpace("Casa con jardín");
        testApplicationRequest.setHasOtherPets(false);
        testApplicationRequest.setWorkSchedule("Medio tiempo");
    }

    @Test
    void createApplication_Success() {
        // Given
        when(petRepository.findById("pet123")).thenReturn(Optional.of(testPet));
        when(applicationRepository.existsByUserIdAndPetId("user123", "pet123")).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // When
        Application result = applicationService.createApplication("user123", testApplicationRequest);

        // Then
        assertNotNull(result);
        verify(petRepository).findById("pet123");
        verify(applicationRepository).existsByUserIdAndPetId("user123", "pet123");
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void createApplication_PetNotFound() {
        // Given
        when(petRepository.findById("pet123")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            applicationService.createApplication("user123", testApplicationRequest));
    }

    @Test
    void createApplication_PetNotAvailable() {
        // Given
        testPet.setStatus(PetStatus.ADOPTED);
        when(petRepository.findById("pet123")).thenReturn(Optional.of(testPet));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            applicationService.createApplication("user123", testApplicationRequest));
    }

    @Test
    void createApplication_AlreadyApplied() {
        // Given
        when(petRepository.findById("pet123")).thenReturn(Optional.of(testPet));
        when(applicationRepository.existsByUserIdAndPetId("user123", "pet123")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            applicationService.createApplication("user123", testApplicationRequest));
    }

    @Test
    void getApplicationsByUser_Success() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByUserId("user123")).thenReturn(applications);

        // When
        List<Application> result = applicationService.getApplicationsByUser("user123");

        // Then
        assertEquals(1, result.size());
        assertEquals(testApplication, result.get(0));
    }

    @Test
    void getApplicationsByPet_Success() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByPetId("pet123")).thenReturn(applications);

        // When
        List<Application> result = applicationService.getApplicationsByPet("pet123");

        // Then
        assertEquals(1, result.size());
        assertEquals(testApplication, result.get(0));
    }

    @Test
    void getApplicationsByFoundation_Success() {
        // Given
        List<Pet> pets = Arrays.asList(testPet);
        List<Application> applications = Arrays.asList(testApplication);
        when(petRepository.findByFoundationId("foundation123")).thenReturn(pets);
        when(applicationRepository.findByPetId("pet123")).thenReturn(applications);

        // When
        List<Application> result = applicationService.getApplicationsByFoundation("foundation123");

        // Then
        assertEquals(1, result.size());
        assertEquals(testApplication, result.get(0));
    }

    @Test
    void updateApplicationStatus_AcceptApplication_Success() {
        // Given
        ApplicationStatusRequest statusRequest = new ApplicationStatusRequest();
        statusRequest.setStatus(ApplicationStatus.ACCEPTED);
        statusRequest.setFoundationResponse("Aplicación aprobada");

        Application otherApplication = new Application();
        otherApplication.setId("app456");
        otherApplication.setUserId("user456");
        otherApplication.setPetId("pet123");
        otherApplication.setStatus(ApplicationStatus.PENDING);

        User otherUser = new User();
        otherUser.setId("user456");
        otherUser.setEmail("other@example.com");
        otherUser.setFullName("Other User");

        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        when(petRepository.findById("pet123")).thenReturn(Optional.of(testPet));
        when(applicationRepository.findByPetIdAndStatus("pet123", ApplicationStatus.PENDING))
            .thenReturn(Arrays.asList(testApplication, otherApplication));
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.findById("user456")).thenReturn(Optional.of(otherUser));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // When
        Application result = applicationService.updateApplicationStatus("app123", "foundation123", statusRequest);

        // Then
        assertNotNull(result);
        verify(petRepository).save(testPet);
        verify(emailService, times(2)).sendApplicationStatusEmail(anyString(), anyString(), anyString(), anyString(), anyString());
        assertEquals(PetStatus.IN_PROCESS, testPet.getStatus());
    }

    @Test
    void updateApplicationStatus_ApplicationNotFound() {
        // Given
        ApplicationStatusRequest statusRequest = new ApplicationStatusRequest();
        statusRequest.setStatus(ApplicationStatus.REJECTED);
        statusRequest.setFoundationResponse("No cumple requisitos");

        when(applicationRepository.findById("app123")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            applicationService.updateApplicationStatus("app123", "foundation123", statusRequest));
    }

    @Test
    void updateApplicationStatus_PetNotFound() {
        // Given
        ApplicationStatusRequest statusRequest = new ApplicationStatusRequest();
        statusRequest.setStatus(ApplicationStatus.REJECTED);
        statusRequest.setFoundationResponse("No cumple requisitos");

        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        when(petRepository.findById("pet123")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            applicationService.updateApplicationStatus("app123", "foundation123", statusRequest));
    }

    @Test
    void updateApplicationStatus_NoPermission() {
        // Given
        ApplicationStatusRequest statusRequest = new ApplicationStatusRequest();
        statusRequest.setStatus(ApplicationStatus.REJECTED);
        statusRequest.setFoundationResponse("No cumple requisitos");

        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        when(petRepository.findById("pet123")).thenReturn(Optional.of(testPet));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            applicationService.updateApplicationStatus("app123", "differentFoundation", statusRequest));
    }

    @Test
    void getApplicationById_Found() {
        // Given
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));

        // When
        Optional<Application> result = applicationService.getApplicationById("app123");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testApplication, result.get());
    }

    @Test
    void getApplicationById_NotFound() {
        // Given
        when(applicationRepository.findById("app123")).thenReturn(Optional.empty());

        // When
        Optional<Application> result = applicationService.getApplicationById("app123");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void getApplicationsByStatus_Success() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByStatus(ApplicationStatus.PENDING)).thenReturn(applications);

        // When
        List<Application> result = applicationService.getApplicationsByStatus(ApplicationStatus.PENDING);

        // Then
        assertEquals(1, result.size());
        assertEquals(testApplication, result.get(0));
    }

    @Test
    void countApplicationsByStatus_Success() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByStatus(ApplicationStatus.PENDING)).thenReturn(applications);

        // When
        long result = applicationService.countApplicationsByStatus(ApplicationStatus.PENDING);

        // Then
        assertEquals(1, result);
    }

    @Test
    void getDetailedApplicationsByUser_Success() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByUserId("user123")).thenReturn(applications);
        when(petRepository.findById("pet123")).thenReturn(Optional.of(testPet));
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When
        List<ApplicationDetailResponse> result = applicationService.getDetailedApplicationsByUser("user123");

        // Then
        assertEquals(1, result.size());
        ApplicationDetailResponse response = result.get(0);
        assertEquals(testApplication.getId(), response.getId());
        assertEquals(testPet.getName(), response.getPetName());
        assertEquals(testUser.getFullName(), response.getUserFullName());
    }

    @Test
    void getDetailedApplicationsByFoundation_Success() {
        // Given
        List<Pet> pets = Arrays.asList(testPet);
        List<Application> applications = Arrays.asList(testApplication);
        when(petRepository.findByFoundationId("foundation123")).thenReturn(pets);
        when(applicationRepository.findByPetId("pet123")).thenReturn(applications);
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When
        List<ApplicationDetailResponse> result = applicationService.getDetailedApplicationsByFoundation("foundation123");

        // Then
        assertEquals(1, result.size());
        ApplicationDetailResponse response = result.get(0);
        assertEquals(testApplication.getId(), response.getId());
        assertEquals(testPet.getName(), response.getPetName());
        assertEquals(testUser.getFullName(), response.getUserFullName());
    }

    @Test
    void getDetailedApplicationById_Found() {
        // Given
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        when(petRepository.findById("pet123")).thenReturn(Optional.of(testPet));
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When
        Optional<ApplicationDetailResponse> result = applicationService.getDetailedApplicationById("app123");

        // Then
        assertTrue(result.isPresent());
        ApplicationDetailResponse response = result.get();
        assertEquals(testApplication.getId(), response.getId());
        assertEquals(testPet.getName(), response.getPetName());
        assertEquals(testUser.getFullName(), response.getUserFullName());
    }

    @Test
    void getDetailedApplicationById_NotFound() {
        // Given
        when(applicationRepository.findById("app123")).thenReturn(Optional.empty());

        // When
        Optional<ApplicationDetailResponse> result = applicationService.getDetailedApplicationById("app123");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void getAllApplications_Success() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findAll()).thenReturn(applications);

        // When
        List<Application> result = applicationService.getAllApplications();

        // Then
        assertEquals(1, result.size());
        assertEquals(testApplication, result.get(0));
    }

    @Test
    void getAllDetailedApplications_Success() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findAll()).thenReturn(applications);
        when(petRepository.findById("pet123")).thenReturn(Optional.of(testPet));
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When
        List<ApplicationDetailResponse> result = applicationService.getAllDetailedApplications();

        // Then
        assertEquals(1, result.size());
        ApplicationDetailResponse response = result.get(0);
        assertEquals(testApplication.getId(), response.getId());
        assertEquals(testPet.getName(), response.getPetName());
        assertEquals(testUser.getFullName(), response.getUserFullName());
    }

    @Test
    void updateApplicationStatus_RejectApplication_Success() {
        // Given
        ApplicationStatusRequest statusRequest = new ApplicationStatusRequest();
        statusRequest.setStatus(ApplicationStatus.REJECTED);
        statusRequest.setFoundationResponse("No cumple requisitos");

        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        when(petRepository.findById("pet123")).thenReturn(Optional.of(testPet));
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // When
        Application result = applicationService.updateApplicationStatus("app123", "foundation123", statusRequest);

        // Then
        assertNotNull(result);
        verify(emailService).sendApplicationStatusEmail(
            testUser.getEmail(),
            testUser.getFullName(),
            testPet.getName(),
            "REJECTED",
            "No cumple requisitos"
        );
    }

    @Test
    void getDetailedApplicationsByUser_WithNullPetAndUser() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByUserId("user123")).thenReturn(applications);
        when(petRepository.findById("pet123")).thenReturn(Optional.empty());
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        // When
        List<ApplicationDetailResponse> result = applicationService.getDetailedApplicationsByUser("user123");

        // Then
        assertEquals(1, result.size());
        ApplicationDetailResponse response = result.get(0);
        assertEquals(testApplication.getId(), response.getId());
        assertNull(response.getPetName());
        assertNull(response.getUserFullName());
    }
}