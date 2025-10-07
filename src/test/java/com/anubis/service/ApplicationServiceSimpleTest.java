package com.anubis.service;

import com.anubis.dto.ApplicationRequest;
import com.anubis.model.Application;
import com.anubis.model.ApplicationStatus;
import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import com.anubis.model.User;
import com.anubis.repository.ApplicationRepository;
import com.anubis.repository.PetRepository;
import com.anubis.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationServiceSimpleTest {

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

    private Application testApplication;
    private Pet testPet;
    private User testUser;

    @BeforeEach
    void setUp() {
        testApplication = new Application();
        testApplication.setId("app-123");
        testApplication.setUserId("user-123");
        testApplication.setPetId("pet-123");
        testApplication.setStatus(ApplicationStatus.PENDING);

        testPet = new Pet();
        testPet.setId("pet-123");
        testPet.setName("Buddy");
        testPet.setStatus(PetStatus.AVAILABLE);
        testPet.setFoundationId("foundation-123");

        testUser = new User();
        testUser.setId("user-123");
        testUser.setEmail("test@example.com");
    }

    @Test
    void getAllApplications_ReturnsAllApplications() {
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findAll()).thenReturn(applications);

        List<Application> result = applicationService.getAllApplications();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepository).findAll();
    }

    @Test
    void getApplicationById_ValidId_ReturnsApplication() {
        String appId = "app-123";
        when(applicationRepository.findById(appId)).thenReturn(Optional.of(testApplication));

        Optional<Application> result = applicationService.getApplicationById(appId);

        assertTrue(result.isPresent());
        assertEquals(appId, result.get().getId());
        verify(applicationRepository).findById(appId);
    }

    @Test
    void getApplicationById_InvalidId_ReturnsEmpty() {
        String appId = "invalid-id";
        when(applicationRepository.findById(appId)).thenReturn(Optional.empty());

        Optional<Application> result = applicationService.getApplicationById(appId);

        assertFalse(result.isPresent());
        verify(applicationRepository).findById(appId);
    }

    @Test
    void getApplicationsByUser_ValidUserId_ReturnsApplications() {
        String userId = "user-123";
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByUserId(userId)).thenReturn(applications);

        List<Application> result = applicationService.getApplicationsByUser(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepository).findByUserId(userId);
    }

    @Test
    void getApplicationsByPet_ValidPetId_ReturnsApplications() {
        String petId = "pet-123";
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByPetId(petId)).thenReturn(applications);

        List<Application> result = applicationService.getApplicationsByPet(petId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepository).findByPetId(petId);
    }

    @Test
    void getApplicationsByStatus_ValidStatus_ReturnsApplications() {
        ApplicationStatus status = ApplicationStatus.PENDING;
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByStatus(status)).thenReturn(applications);

        List<Application> result = applicationService.getApplicationsByStatus(status);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepository).findByStatus(status);
    }

    @Test
    void countApplicationsByStatus_ValidStatus_ReturnsCount() {
        ApplicationStatus status = ApplicationStatus.PENDING;
        List<Application> applications = Arrays.asList(testApplication);
        when(applicationRepository.findByStatus(status)).thenReturn(applications);

        long result = applicationService.countApplicationsByStatus(status);

        assertEquals(1L, result);
        verify(applicationRepository).findByStatus(status);
    }

    @Test
    void getApplicationsByFoundation_ValidFoundationId_ReturnsApplications() {
        String foundationId = "foundation-123";
        List<Pet> pets = Arrays.asList(testPet);
        List<Application> applications = Arrays.asList(testApplication);
        
        when(petRepository.findByFoundationId(foundationId)).thenReturn(pets);
        when(applicationRepository.findByPetId("pet-123")).thenReturn(applications);

        List<Application> result = applicationService.getApplicationsByFoundation(foundationId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(petRepository).findByFoundationId(foundationId);
        verify(applicationRepository).findByPetId("pet-123");
    }

    @Test
    void deleteApplication_ValidIdAndFoundation_DeletesSuccessfully() {
        String appId = "app-123";
        String foundationId = "foundation-123";
        
        when(applicationRepository.findById(appId)).thenReturn(Optional.of(testApplication));
        when(petRepository.findById("pet-123")).thenReturn(Optional.of(testPet));
        doNothing().when(applicationRepository).deleteById(appId);

        assertDoesNotThrow(() -> {
            applicationService.deleteApplication(appId, foundationId);
        });

        verify(applicationRepository).findById(appId);
        verify(petRepository).findById("pet-123");
        verify(applicationRepository).deleteById(appId);
    }

    @Test
    void deleteApplicationAsAdmin_ValidId_DeletesSuccessfully() {
        String appId = "app-123";
        
        when(applicationRepository.findById(appId)).thenReturn(Optional.of(testApplication));
        doNothing().when(applicationRepository).deleteById(appId);

        assertDoesNotThrow(() -> {
            applicationService.deleteApplicationAsAdmin(appId);
        });

        verify(applicationRepository).findById(appId);
        verify(applicationRepository).deleteById(appId);
    }
}