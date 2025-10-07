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

import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import com.anubis.repository.PetRepository;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private Pet testPet1;
    private Pet testPet2;
    private Pet adoptedPet;

    @BeforeEach
    void setUp() {
        // Setup test pets
        testPet1 = new Pet();
        testPet1.setId("pet1");
        testPet1.setName("Buddy");
        testPet1.setSpecies("Dog");
        testPet1.setBreed("Golden Retriever");
        testPet1.setAge(3);
        testPet1.setGender("Male");
        testPet1.setSize("Large");
        testPet1.setDescription("Friendly and energetic dog");
        testPet1.setStatus(PetStatus.AVAILABLE);
        testPet1.setFoundationId("foundation1");
        testPet1.setActive(true);
        testPet1.setCreatedAt(LocalDateTime.now().minusDays(5));
        testPet1.setImageUrls(Arrays.asList("image1.jpg", "image2.jpg"));

        testPet2 = new Pet();
        testPet2.setId("pet2");
        testPet2.setName("Whiskers");
        testPet2.setSpecies("Cat");
        testPet2.setBreed("Persian");
        testPet2.setAge(2);
        testPet2.setGender("Female");
        testPet2.setSize("Medium");
        testPet2.setDescription("Calm and loving cat");
        testPet2.setStatus(PetStatus.AVAILABLE);
        testPet2.setFoundationId("foundation2");
        testPet2.setActive(true);
        testPet2.setCreatedAt(LocalDateTime.now().minusDays(3));
        testPet2.setImageUrls(Arrays.asList("cat1.jpg"));

        adoptedPet = new Pet();
        adoptedPet.setId("pet3");
        adoptedPet.setName("Rex");
        adoptedPet.setSpecies("Dog");
        adoptedPet.setBreed("Labrador");
        adoptedPet.setAge(5);
        adoptedPet.setGender("Male");
        adoptedPet.setSize("Large");
        adoptedPet.setDescription("Adopted dog");
        adoptedPet.setStatus(PetStatus.ADOPTED);
        adoptedPet.setFoundationId("foundation1");
        adoptedPet.setActive(true);
        adoptedPet.setCreatedAt(LocalDateTime.now().minusDays(10));
    }

    // ========== GET ALL PETS TESTS ==========
    @Test
    void testGetAllPets_Success() {
        // Given
        List<Pet> expectedPets = Arrays.asList(testPet1, testPet2, adoptedPet);
        when(petRepository.findByActiveTrue()).thenReturn(expectedPets);

        // When
        List<Pet> result = petService.getAllPets();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedPets, result);
        verify(petRepository).findByActiveTrue();
    }

    @Test
    void testGetAllActivePets_Success() {
        // Given
        List<Pet> expectedPets = Arrays.asList(testPet1, testPet2);
        when(petRepository.findByActiveTrue()).thenReturn(expectedPets);

        // When
        List<Pet> result = petService.getAllActivePets();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(petRepository).findByActiveTrue();
    }

    @Test
    void testGetAllPets_EmptyList() {
        // Given
        when(petRepository.findByActiveTrue()).thenReturn(Arrays.asList());

        // When
        List<Pet> result = petService.getAllPets();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(petRepository).findByActiveTrue();
    }

    // ========== GET AVAILABLE PETS TESTS ==========
    @Test
    void testGetAvailablePets_Success() {
        // Given
        List<Pet> availablePets = Arrays.asList(testPet1, testPet2);
        when(petRepository.findByStatusAndActiveTrue(PetStatus.AVAILABLE)).thenReturn(availablePets);

        // When
        List<Pet> result = petService.getAvailablePets();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testPet1));
        assertTrue(result.contains(testPet2));
        assertFalse(result.contains(adoptedPet));
        verify(petRepository).findByStatusAndActiveTrue(PetStatus.AVAILABLE);
    }

    @Test
    void testGetAvailablePets_NoAvailablePets() {
        // Given
        when(petRepository.findByStatusAndActiveTrue(PetStatus.AVAILABLE)).thenReturn(Arrays.asList());

        // When
        List<Pet> result = petService.getAvailablePets();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(petRepository).findByStatusAndActiveTrue(PetStatus.AVAILABLE);
    }

    // ========== GET PET BY ID TESTS ==========
    @Test
    void testGetPetById_Success() {
        // Given
        when(petRepository.findById("pet1")).thenReturn(Optional.of(testPet1));

        // When
        Optional<Pet> result = petService.getPetById("pet1");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPet1, result.get());
        verify(petRepository).findById("pet1");
    }

    @Test
    void testGetPetById_NotFound() {
        // Given
        when(petRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<Pet> result = petService.getPetById("nonexistent");

        // Then
        assertFalse(result.isPresent());
        verify(petRepository).findById("nonexistent");
    }

    // ========== GET PETS BY FOUNDATION TESTS ==========
    @Test
    void testGetPetsByFoundation_Success() {
        // Given
        List<Pet> foundationPets = Arrays.asList(testPet1, adoptedPet);
        when(petRepository.findByFoundationIdAndActiveTrue("foundation1")).thenReturn(foundationPets);

        // When
        List<Pet> result = petService.getPetsByFoundation("foundation1");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testPet1));
        assertTrue(result.contains(adoptedPet));
        verify(petRepository).findByFoundationIdAndActiveTrue("foundation1");
    }

    @Test
    void testGetPetsByFoundation_NoFoundationPets() {
        // Given
        when(petRepository.findByFoundationIdAndActiveTrue("foundation3")).thenReturn(Arrays.asList());

        // When
        List<Pet> result = petService.getPetsByFoundation("foundation3");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(petRepository).findByFoundationIdAndActiveTrue("foundation3");
    }

    // ========== GET PETS BY SPECIES TESTS ==========
    @Test
    void testGetPetsBySpecies_Dogs() {
        // Given
        List<Pet> dogs = Arrays.asList(testPet1, adoptedPet);
        when(petRepository.findBySpeciesAndActiveTrue("Dog")).thenReturn(dogs);

        // When
        List<Pet> result = petService.getPetsBySpecies("Dog");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(petRepository).findBySpeciesAndActiveTrue("Dog");
    }

    @Test
    void testGetPetsBySpecies_Cats() {
        // Given
        List<Pet> cats = Arrays.asList(testPet2);
        when(petRepository.findBySpeciesAndActiveTrue("Cat")).thenReturn(cats);

        // When
        List<Pet> result = petService.getPetsBySpecies("Cat");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPet2, result.get(0));
        verify(petRepository).findBySpeciesAndActiveTrue("Cat");
    }

    // ========== GET PETS BY STATUS TESTS ==========
    @Test
    void testGetPetsByStatus_Available() {
        // Given
        List<Pet> availablePets = Arrays.asList(testPet1, testPet2);
        when(petRepository.findByStatusAndActiveTrue(PetStatus.AVAILABLE)).thenReturn(availablePets);

        // When
        List<Pet> result = petService.getPetsByStatus(PetStatus.AVAILABLE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(petRepository).findByStatusAndActiveTrue(PetStatus.AVAILABLE);
    }

    @Test
    void testGetPetsByStatus_Adopted() {
        // Given
        List<Pet> adoptedPets = Arrays.asList(adoptedPet);
        when(petRepository.findByStatusAndActiveTrue(PetStatus.ADOPTED)).thenReturn(adoptedPets);

        // When
        List<Pet> result = petService.getPetsByStatus(PetStatus.ADOPTED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(adoptedPet, result.get(0));
        verify(petRepository).findByStatusAndActiveTrue(PetStatus.ADOPTED);
    }

    // ========== SAVE PET TESTS ==========
    @Test
    void testSavePet_Success() {
        // Given
        when(petRepository.save(testPet1)).thenReturn(testPet1);

        // When
        Pet result = petService.savePet(testPet1);

        // Then
        assertNotNull(result);
        assertEquals(testPet1, result);
        verify(petRepository).save(testPet1);
    }

    // ========== CREATE PET TESTS ==========
    @Test
    void testCreatePet_Success() {
        // Given
        Pet newPet = new Pet();
        newPet.setName("Bella");
        newPet.setSpecies("Dog");
        newPet.setBreed("Beagle");
        
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            pet.setId("new-pet-id");
            return pet;
        });

        // When
        Pet result = petService.createPet(newPet);

        // Then
        assertNotNull(result);
        assertEquals("Bella", result.getName());
        assertEquals(PetStatus.AVAILABLE, result.getStatus());
        assertTrue(result.isActive());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(petRepository).save(newPet);
    }

    @Test
    void testCreatePet_SetsDefaultValues() {
        // Given
        Pet newPet = new Pet();
        newPet.setName("Test Pet");
        
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pet result = petService.createPet(newPet);

        // Then
        assertEquals(PetStatus.AVAILABLE, result.getStatus());
        assertTrue(result.isActive());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(petRepository).save(newPet);
    }

    // ========== UPDATE PET TESTS ==========
    @Test
    void testUpdatePet_Success() {
        // Given
        Pet updatedDetails = new Pet();
        updatedDetails.setName("Updated Buddy");
        updatedDetails.setSpecies("Dog");
        updatedDetails.setBreed("Golden Retriever Mix");
        updatedDetails.setAge(4);
        updatedDetails.setGender("Male");
        updatedDetails.setSize("Large");
        updatedDetails.setDescription("Updated friendly dog");
        updatedDetails.setImageUrls(Arrays.asList("updated1.jpg", "updated2.jpg"));

        when(petRepository.findById("pet1")).thenReturn(Optional.of(testPet1));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pet result = petService.updatePet("pet1", updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Buddy", result.getName());
        assertEquals("Golden Retriever Mix", result.getBreed());
        assertEquals(4, result.getAge());
        assertEquals("Updated friendly dog", result.getDescription());
        assertNotNull(result.getUpdatedAt());
        verify(petRepository).findById("pet1");
        verify(petRepository).save(testPet1);
    }

    @Test
    void testUpdatePet_PetNotFound() {
        // Given
        Pet updatedDetails = new Pet();
        updatedDetails.setName("Updated Name");

        when(petRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            petService.updatePet("nonexistent", updatedDetails);
        });

        assertEquals("Mascota no encontrada con id: nonexistent", exception.getMessage());
        verify(petRepository).findById("nonexistent");
        verify(petRepository, never()).save(any(Pet.class));
    }

    // ========== UPDATE PET STATUS TESTS ==========
    @Test
    void testUpdatePetStatus_Success() {
        // Given
        when(petRepository.findById("pet1")).thenReturn(Optional.of(testPet1));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pet result = petService.updatePetStatus("pet1", PetStatus.ADOPTED);

        // Then
        assertNotNull(result);
        assertEquals(PetStatus.ADOPTED, result.getStatus());
        assertNotNull(result.getUpdatedAt());
        verify(petRepository).findById("pet1");
        verify(petRepository).save(testPet1);
    }

    @Test
    void testUpdatePetStatus_PetNotFound() {
        // Given
        when(petRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            petService.updatePetStatus("nonexistent", PetStatus.ADOPTED);
        });

        assertEquals("Mascota no encontrada con id: nonexistent", exception.getMessage());
        verify(petRepository).findById("nonexistent");
        verify(petRepository, never()).save(any(Pet.class));
    }

    // ========== DELETE PET TESTS ==========
    @Test
    void testDeletePet_Success() {
        // Given
        when(petRepository.findById("pet1")).thenReturn(Optional.of(testPet1));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        petService.deletePet("pet1");

        // Then
        assertFalse(testPet1.isActive()); // Soft delete
        assertNotNull(testPet1.getUpdatedAt());
        verify(petRepository).findById("pet1");
        verify(petRepository).save(testPet1);
    }

    @Test
    void testDeletePet_PetNotFound() {
        // Given
        when(petRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            petService.deletePet("nonexistent");
        });

        assertEquals("Mascota no encontrada con id: nonexistent", exception.getMessage());
        verify(petRepository).findById("nonexistent");
        verify(petRepository, never()).save(any(Pet.class));
    }

    // ========== EDGE CASES AND ERROR HANDLING ==========
    @Test
    void testCreatePet_NullPet() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            petService.createPet(null);
        });
    }

    @Test
    void testUpdatePet_NullId() {
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            petService.updatePet(null, new Pet());
        });
    }

    @Test
    void testDeletePet_NullId() {
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            petService.deletePet(null);
        });
    }

    @Test
    void testUpdatePetStatus_ValidStatusTransitions() {
        // Test all valid status transitions
        when(petRepository.findById("pet1")).thenReturn(Optional.of(testPet1));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Available to Adopted
        petService.updatePetStatus("pet1", PetStatus.ADOPTED);
        assertEquals(PetStatus.ADOPTED, testPet1.getStatus());

        // Adopted to Available (if return)
        petService.updatePetStatus("pet1", PetStatus.AVAILABLE);
        assertEquals(PetStatus.AVAILABLE, testPet1.getStatus());

        verify(petRepository, times(2)).findById("pet1");
        verify(petRepository, times(2)).save(testPet1);
    }
}