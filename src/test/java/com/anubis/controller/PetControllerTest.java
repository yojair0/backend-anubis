package com.anubis.controller;

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
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import com.anubis.dto.PetRequest;
import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import com.anubis.security.UserPrincipal;
import com.anubis.service.FileUploadService;
import com.anubis.service.PetService;

@ExtendWith(MockitoExtension.class)
public class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PetController petController;

    private Pet testPet1;
    private Pet testPet2;
    private Pet testDog;
    private PetRequest petRequest;
    private UserPrincipal foundationPrincipal;
    private UserPrincipal adminPrincipal;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        testPet1 = new Pet();
        testPet1.setId("pet1");
        testPet1.setName("Max");
        testPet1.setSpecies("Perro");
        testPet1.setBreed("Golden Retriever");
        testPet1.setAge(3);
        testPet1.setGender("Macho");
        testPet1.setSize("Grande");
        testPet1.setDescription("Perro amigable y juguetón");
        testPet1.setStatus(PetStatus.AVAILABLE);
        testPet1.setFoundationId("foundation1");

        testPet2 = new Pet();
        testPet2.setId("pet2");
        testPet2.setName("Luna");
        testPet2.setSpecies("Gato");
        testPet2.setBreed("Siamés");
        testPet2.setAge(2);
        testPet2.setGender("Hembra");
        testPet2.setSize("Mediano");
        testPet2.setDescription("Gata tranquila y cariñosa");
        testPet2.setStatus(PetStatus.AVAILABLE);
        testPet2.setFoundationId("foundation1");

        testDog = new Pet();
        testDog.setId("pet3");
        testDog.setName("Rex");
        testDog.setSpecies("Perro");
        testDog.setBreed("Pastor Alemán");
        testDog.setAge(4);
        testDog.setGender("Macho");
        testDog.setSize("Grande");
        testDog.setStatus(PetStatus.AVAILABLE);
        testDog.setFoundationId("foundation2");

        petRequest = new PetRequest();
        petRequest.setName("Buddy");
        petRequest.setSpecies("Perro");
        petRequest.setBreed("Labrador");
        petRequest.setAge(2);
        petRequest.setGender("Macho");
        petRequest.setSize("Grande");
        petRequest.setDescription("Perro muy activo");
        petRequest.setImageUrls(Arrays.asList("url1", "url2"));

        foundationPrincipal = new UserPrincipal("foundation1", "foundation@test.com", "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_FOUNDATION")));

        adminPrincipal = new UserPrincipal("admin1", "admin@test.com", "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        userPrincipal = new UserPrincipal("user1", "user@test.com", "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    // ========== GET ALL PETS TESTS ==========
    @Test
    void testGetAllPets_Success() {
        // Given
        List<Pet> availablePets = Arrays.asList(testPet1, testPet2, testDog);
        when(petService.getAvailablePets()).thenReturn(availablePets);

        // When
        ResponseEntity<List<Pet>> response = petController.getAllPets();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3, response.getBody().size());
        assertTrue(response.getBody().contains(testPet1));
        assertTrue(response.getBody().contains(testPet2));
        assertTrue(response.getBody().contains(testDog));

        verify(petService).getAvailablePets();
    }

    @Test
    void testGetAllPets_EmptyList() {
        // Given
        when(petService.getAvailablePets()).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<Pet>> response = petController.getAllPets();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());

        verify(petService).getAvailablePets();
    }

    // ========== GET PET BY ID TESTS ==========
    @Test
    void testGetPetById_Success() {
        // Given
        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));

        // When
        ResponseEntity<?> response = petController.getPetById("pet1");

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testPet1, response.getBody());

        verify(petService).getPetById("pet1");
    }

    @Test
    void testGetPetById_NotFound() {
        // Given
        when(petService.getPetById("nonexistent")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = petController.getPetById("nonexistent");

        // Then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        verify(petService).getPetById("nonexistent");
    }

    // ========== GET PETS BY SPECIES TESTS ==========
    @Test
    void testGetPetsBySpecies_Dogs() {
        // Given
        List<Pet> dogs = Arrays.asList(testPet1, testDog);
        when(petService.getPetsBySpecies("Perro")).thenReturn(dogs);

        // When
        ResponseEntity<List<Pet>> response = petController.getPetsBySpecies("Perro");

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(testPet1));
        assertTrue(response.getBody().contains(testDog));

        verify(petService).getPetsBySpecies("Perro");
    }

    @Test
    void testGetPetsBySpecies_Cats() {
        // Given
        List<Pet> cats = Arrays.asList(testPet2);
        when(petService.getPetsBySpecies("Gato")).thenReturn(cats);

        // When
        ResponseEntity<List<Pet>> response = petController.getPetsBySpecies("Gato");

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(testPet2, response.getBody().get(0));

        verify(petService).getPetsBySpecies("Gato");
    }

    // ========== CREATE PET TESTS ==========
    @Test
    void testCreatePet_Success() {
        // Given
        Pet createdPet = new Pet();
        createdPet.setId("newPet");
        createdPet.setName("Buddy");
        createdPet.setFoundationId("foundation1");

        when(petService.createPet(any(Pet.class))).thenReturn(createdPet);

        // When
        ResponseEntity<?> response = petController.createPet(foundationPrincipal, petRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(createdPet, response.getBody());

        verify(petService).createPet(any(Pet.class));
    }

    @Test
    void testCreatePet_ServiceException() {
        // Given
        when(petService.createPet(any(Pet.class)))
                .thenThrow(new RuntimeException("Error al crear mascota"));

        // When
        ResponseEntity<?> response = petController.createPet(foundationPrincipal, petRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof PetController.MessageResponse);

        verify(petService).createPet(any(Pet.class));
    }

    // ========== UPDATE PET TESTS ==========
    @Test
    void testUpdatePet_Success_AsOwnerFoundation() {
        // Given
        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));

        Pet updatedPet = new Pet();
        updatedPet.setId("pet1");
        updatedPet.setName("Updated Max");

        when(petService.updatePet(eq("pet1"), any(Pet.class))).thenReturn(updatedPet);

        // When
        ResponseEntity<?> response = petController.updatePet("pet1", foundationPrincipal, petRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedPet, response.getBody());

        verify(petService).getPetById("pet1");
        verify(petService).updatePet(eq("pet1"), any(Pet.class));
    }

    @Test
    void testUpdatePet_Success_AsAdmin() {
        // Given
        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));

        Pet updatedPet = new Pet();
        updatedPet.setId("pet1");
        updatedPet.setName("Updated Max");

        when(petService.updatePet(eq("pet1"), any(Pet.class))).thenReturn(updatedPet);

        // When
        ResponseEntity<?> response = petController.updatePet("pet1", adminPrincipal, petRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedPet, response.getBody());

        verify(petService).getPetById("pet1");
        verify(petService).updatePet(eq("pet1"), any(Pet.class));
    }

    @Test
    void testUpdatePet_NotFound() {
        // Given
        when(petService.getPetById("nonexistent")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = petController.updatePet("nonexistent", foundationPrincipal, petRequest);

        // Then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        verify(petService).getPetById("nonexistent");
        verify(petService, never()).updatePet(anyString(), any(Pet.class));
    }

    @Test
    void testUpdatePet_Forbidden_WrongFoundation() {
        // Given
        UserPrincipal differentFoundation = new UserPrincipal("foundation2", "foundation2@test.com", "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_FOUNDATION")));

        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));

        // When
        ResponseEntity<?> response = petController.updatePet("pet1", differentFoundation, petRequest);

        // Then
        assertNotNull(response);
        assertEquals(403, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof PetController.MessageResponse);

        verify(petService).getPetById("pet1");
        verify(petService, never()).updatePet(anyString(), any(Pet.class));
    }

    // ========== UPDATE PET STATUS TESTS ==========
    @Test
    void testUpdatePetStatus_Success() {
        // Given
        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));

        Pet updatedPet = new Pet();
        updatedPet.setId("pet1");
        updatedPet.setStatus(PetStatus.ADOPTED);

        when(petService.updatePetStatus("pet1", PetStatus.ADOPTED)).thenReturn(updatedPet);

        // When
        ResponseEntity<?> response = petController.updatePetStatus("pet1", foundationPrincipal, PetStatus.ADOPTED);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedPet, response.getBody());

        verify(petService).getPetById("pet1");
        verify(petService).updatePetStatus("pet1", PetStatus.ADOPTED);
    }

    @Test
    void testUpdatePetStatus_NotFound() {
        // Given
        when(petService.getPetById("nonexistent")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = petController.updatePetStatus("nonexistent", foundationPrincipal, PetStatus.ADOPTED);

        // Then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        verify(petService).getPetById("nonexistent");
        verify(petService, never()).updatePetStatus(anyString(), any(PetStatus.class));
    }

    @Test
    void testUpdatePetStatus_Forbidden() {
        // Given
        UserPrincipal differentFoundation = new UserPrincipal("foundation2", "foundation2@test.com", "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_FOUNDATION")));

        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));

        // When
        ResponseEntity<?> response = petController.updatePetStatus("pet1", differentFoundation, PetStatus.ADOPTED);

        // Then
        assertNotNull(response);
        assertEquals(403, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof PetController.MessageResponse);

        verify(petService).getPetById("pet1");
        verify(petService, never()).updatePetStatus(anyString(), any(PetStatus.class));
    }

    // ========== DELETE PET TESTS ==========
    @Test
    void testDeletePet_Success() {
        // Given
        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));
        doNothing().when(petService).deletePet("pet1");

        // When
        ResponseEntity<?> response = petController.deletePet("pet1", foundationPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof PetController.MessageResponse);

        PetController.MessageResponse messageResponse = (PetController.MessageResponse) response.getBody();
        assertEquals("Mascota eliminada", messageResponse.getMessage());

        verify(petService).getPetById("pet1");
        verify(petService).deletePet("pet1");
    }

    @Test
    void testDeletePet_NotFound() {
        // Given
        when(petService.getPetById("nonexistent")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = petController.deletePet("nonexistent", foundationPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        verify(petService).getPetById("nonexistent");
        verify(petService, never()).deletePet(anyString());
    }

    @Test
    void testDeletePet_Forbidden() {
        // Given
        UserPrincipal differentFoundation = new UserPrincipal("foundation2", "foundation2@test.com", "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_FOUNDATION")));

        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));

        // When
        ResponseEntity<?> response = petController.deletePet("pet1", differentFoundation);

        // Then
        assertNotNull(response);
        assertEquals(403, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof PetController.MessageResponse);

        verify(petService).getPetById("pet1");
        verify(petService, never()).deletePet(anyString());
    }

    // ========== GET MY PETS TESTS ==========
    @Test
    void testGetMyPets_Success() {
        // Given
        List<Pet> foundationPets = Arrays.asList(testPet1, testPet2);
        when(petService.getPetsByFoundation("foundation1")).thenReturn(foundationPets);

        // When
        ResponseEntity<List<Pet>> response = petController.getMyPets(foundationPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(testPet1));
        assertTrue(response.getBody().contains(testPet2));

        verify(petService).getPetsByFoundation("foundation1");
    }

    @Test
    void testGetMyPets_EmptyList() {
        // Given
        when(petService.getPetsByFoundation("foundation1")).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<Pet>> response = petController.getMyPets(foundationPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());

        verify(petService).getPetsByFoundation("foundation1");
    }

    // ========== UPLOAD IMAGE TESTS ==========
    // Tests de upload de imágenes eliminados - ahora se usan imágenes Base64

    // ========== RESPONSE STRUCTURE VALIDATION TESTS ==========
    @Test
    void testMessageResponseStructure() {
        PetController.MessageResponse response = new PetController.MessageResponse("Test message");
        assertEquals("Test message", response.getMessage());

        response.setMessage("Updated message");
        assertEquals("Updated message", response.getMessage());
    }

    // Test de ImageUploadResponse eliminado - ahora se usan imágenes Base64

    // ========== EDGE CASES TESTS ==========

    @Test
    void testUpdatePet_ServiceException() {
        // Given
        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));
        when(petService.updatePet(eq("pet1"), any(Pet.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<?> response = petController.updatePet("pet1", foundationPrincipal, petRequest);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof PetController.MessageResponse);

        verify(petService).getPetById("pet1");
        verify(petService).updatePet(eq("pet1"), any(Pet.class));
    }

    @Test
    void testUpdatePetStatus_ServiceException() {
        // Given
        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));
        when(petService.updatePetStatus("pet1", PetStatus.ADOPTED))
                .thenThrow(new RuntimeException("Status update error"));

        // When
        ResponseEntity<?> response = petController.updatePetStatus("pet1", foundationPrincipal, PetStatus.ADOPTED);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof PetController.MessageResponse);

        verify(petService).getPetById("pet1");
        verify(petService).updatePetStatus("pet1", PetStatus.ADOPTED);
    }

    @Test
    void testDeletePet_ServiceException() {
        // Given
        when(petService.getPetById("pet1")).thenReturn(Optional.of(testPet1));
        doThrow(new RuntimeException("Delete error")).when(petService).deletePet("pet1");

        // When
        ResponseEntity<?> response = petController.deletePet("pet1", foundationPrincipal);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof PetController.MessageResponse);

        verify(petService).getPetById("pet1");
        verify(petService).deletePet("pet1");
    }

    // ========== COMPREHENSIVE WORKFLOW TESTS ==========
    @Test
    void testCompletePetWorkflow() {
        // Given - Create pet
        Pet createdPet = new Pet();
        createdPet.setId("workflow-pet");
        createdPet.setName("Workflow Pet");
        createdPet.setFoundationId("foundation1");
        createdPet.setStatus(PetStatus.AVAILABLE);

        when(petService.createPet(any(Pet.class))).thenReturn(createdPet);
        when(petService.getPetById("workflow-pet")).thenReturn(Optional.of(createdPet));

        // Given - Update pet
        Pet updatedPet = new Pet();
        updatedPet.setId("workflow-pet");
        updatedPet.setName("Updated Workflow Pet");

        when(petService.updatePet(eq("workflow-pet"), any(Pet.class))).thenReturn(updatedPet);

        // Given - Update status
        Pet statusUpdatedPet = new Pet();
        statusUpdatedPet.setId("workflow-pet");
        statusUpdatedPet.setStatus(PetStatus.ADOPTED);

        when(petService.updatePetStatus("workflow-pet", PetStatus.ADOPTED)).thenReturn(statusUpdatedPet);
        doNothing().when(petService).deletePet("workflow-pet");

        // When - Execute complete workflow
        ResponseEntity<?> createResponse = petController.createPet(foundationPrincipal, petRequest);
        ResponseEntity<?> getPetResponse = petController.getPetById("workflow-pet");
        ResponseEntity<?> updateResponse = petController.updatePet("workflow-pet", foundationPrincipal, petRequest);
        ResponseEntity<?> statusResponse = petController.updatePetStatus("workflow-pet", foundationPrincipal, PetStatus.ADOPTED);
        ResponseEntity<?> deleteResponse = petController.deletePet("workflow-pet", foundationPrincipal);

        // Then - Verify all operations successful
        assertEquals(200, createResponse.getStatusCodeValue());
        assertEquals(200, getPetResponse.getStatusCodeValue());
        assertEquals(200, updateResponse.getStatusCodeValue());
        assertEquals(200, statusResponse.getStatusCodeValue());
        assertEquals(200, deleteResponse.getStatusCodeValue());

        // Verify service calls
        verify(petService).createPet(any(Pet.class));
        verify(petService, times(4)).getPetById("workflow-pet"); // Called for get, update, status, delete
        verify(petService).updatePet(eq("workflow-pet"), any(Pet.class));
        verify(petService).updatePetStatus("workflow-pet", PetStatus.ADOPTED);
        verify(petService).deletePet("workflow-pet");
    }
}