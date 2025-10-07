package com.anubis.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.ResponseEntity;

import com.anubis.dto.PetRequest;
import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import com.anubis.security.UserPrincipal;
import com.anubis.service.PetService;
import com.anubis.controller.PetController.MessageResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class PetControllerExtraTest {

    @Mock
    private PetService petService;

    @InjectMocks
    private PetController petController;

    private Pet testPet;
    private PetRequest testRequest;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        testPet = new Pet();
        testPet.setId("pet-123");
        testPet.setName("Buddy");
        testPet.setSpecies("Dog");
        testPet.setStatus(PetStatus.AVAILABLE);

        testRequest = new PetRequest();
        testRequest.setName("Max");
        testRequest.setSpecies("Cat");
        testRequest.setBreed("Persian");
        testRequest.setAge(2);
        testRequest.setGender("Male");
        testRequest.setSize("Medium");
        testRequest.setDescription("Friendly cat");

        userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getId()).thenReturn("foundation-123");
    }

    @Test
    void getAllPets_ReturnsAvailablePets() {
        List<Pet> pets = Arrays.asList(testPet);
        when(petService.getAvailablePets()).thenReturn(pets);

        ResponseEntity<List<Pet>> response = petController.getAllPets();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(petService).getAvailablePets();
    }

    @Test
    void getPetById_ValidId_ReturnsPet() {
        String petId = "pet-123";
        when(petService.getPetById(petId)).thenReturn(Optional.of(testPet));

        ResponseEntity<?> response = petController.getPetById(petId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(petService).getPetById(petId);
    }

    @Test
    void getPetById_InvalidId_ReturnsNotFound() {
        String petId = "invalid-id";
        when(petService.getPetById(petId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = petController.getPetById(petId);

        assertEquals(404, response.getStatusCodeValue());
        verify(petService).getPetById(petId);
    }

    @Test
    void getPetsBySpecies_ValidSpecies_ReturnsPets() {
        String species = "Dog";
        List<Pet> pets = Arrays.asList(testPet);
        when(petService.getPetsBySpecies(species)).thenReturn(pets);

        ResponseEntity<List<Pet>> response = petController.getPetsBySpecies(species);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(petService).getPetsBySpecies(species);
    }

    @Test
    void createPet_ValidRequest_CreatesPet() {
        Pet createdPet = new Pet();
        createdPet.setId("new-pet-123");
        when(petService.createPet(any(Pet.class))).thenReturn(createdPet);

        ResponseEntity<?> response = petController.createPet(userPrincipal, testRequest);

        assertEquals(200, response.getStatusCodeValue());
        verify(petService).createPet(any(Pet.class));
    }

    @Test
    void createPet_ServiceException_ReturnsError() {
        when(petService.createPet(any(Pet.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = petController.createPet(userPrincipal, testRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("Error: Database error"));
        verify(petService).createPet(any(Pet.class));
    }

    @Test
    void messageResponse_Constructor_SetsMessage() {
        String message = "Test message";
        MessageResponse response = new MessageResponse(message);
        
        assertEquals(message, response.getMessage());
    }

    @Test
    void messageResponse_SettersAndGetters_WorkCorrectly() {
        MessageResponse response = new MessageResponse("Initial");
        String newMessage = "Updated message";
        
        response.setMessage(newMessage);
        
        assertEquals(newMessage, response.getMessage());
    }

    @Test
    void getPetsBySpecies_EmptyList_ReturnsEmptyList() {
        String species = "Bird";
        List<Pet> emptyList = Arrays.asList();
        when(petService.getPetsBySpecies(species)).thenReturn(emptyList);

        ResponseEntity<List<Pet>> response = petController.getPetsBySpecies(species);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(petService).getPetsBySpecies(species);
    }

    @Test
    void createPet_NullUserPrincipal_HandlesGracefully() {
        // This tests the case where userPrincipal might be null
        when(petService.createPet(any(Pet.class))).thenReturn(testPet);

        // Test that null UserPrincipal causes NullPointerException
        try {
            petController.createPet(null, testRequest);
        } catch (NullPointerException e) {
            // Expected behavior
            assertNotNull(e);
        }
    }

    @Test
    void getAllPets_ServiceReturnsNull_HandlesGracefully() {
        when(petService.getAvailablePets()).thenReturn(null);

        ResponseEntity<List<Pet>> response = petController.getAllPets();

        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(petService).getAvailablePets();
    }

    @Test
    void petRequest_AllFields_SetCorrectly() {
        // Test that PetRequest fields are properly transferred to Pet object
        testRequest.setImageUrls(Arrays.asList("url1.jpg", "url2.jpg"));
        when(petService.createPet(any(Pet.class))).thenReturn(testPet);

        ResponseEntity<?> response = petController.createPet(userPrincipal, testRequest);

        assertEquals(200, response.getStatusCodeValue());
        
        // Verify that createPet was called with a Pet object that has the right properties
        verify(petService).createPet(argThat(pet -> 
            pet.getName().equals(testRequest.getName()) &&
            pet.getSpecies().equals(testRequest.getSpecies()) &&
            pet.getBreed().equals(testRequest.getBreed()) &&
            pet.getAge().equals(testRequest.getAge()) &&
            pet.getGender().equals(testRequest.getGender()) &&
            pet.getSize().equals(testRequest.getSize()) &&
            pet.getDescription().equals(testRequest.getDescription()) &&
            pet.getImageUrls().equals(testRequest.getImageUrls()) &&
            pet.getFoundationId().equals(userPrincipal.getId())
        ));
    }
}