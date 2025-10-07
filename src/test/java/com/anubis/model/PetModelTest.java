package com.anubis.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PetModelTest {

    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = new Pet();
    }

    @Test
    void defaultConstructor_CreatesEmptyPet() {
        assertNotNull(pet);
        assertNull(pet.getId());
        assertNull(pet.getName());
        assertNull(pet.getSpecies());
        assertNull(pet.getBreed());
        assertNull(pet.getAge());
        assertNull(pet.getSize());
        assertNull(pet.getDescription());
        assertEquals(PetStatus.AVAILABLE, pet.getStatus());
        assertNull(pet.getFoundationId());
        assertNotNull(pet.getCreatedAt());
        assertNotNull(pet.getUpdatedAt());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        String id = "pet123";
        String name = "Buddy";
        String species = "Dog";
        String breed = "Golden Retriever";
        Integer age = 3;
        String size = "Large";
        String description = "Friendly dog";
        PetStatus status = PetStatus.AVAILABLE;
        String foundationId = "foundation123";
        LocalDateTime now = LocalDateTime.now();

        pet.setId(id);
        pet.setName(name);
        pet.setSpecies(species);
        pet.setBreed(breed);
        pet.setAge(age);
        pet.setSize(size);
        pet.setDescription(description);
        pet.setStatus(status);
        pet.setFoundationId(foundationId);
        pet.setCreatedAt(now);
        pet.setUpdatedAt(now);

        assertEquals(id, pet.getId());
        assertEquals(name, pet.getName());
        assertEquals(species, pet.getSpecies());
        assertEquals(breed, pet.getBreed());
        assertEquals(age, pet.getAge());
        assertEquals(size, pet.getSize());
        assertEquals(description, pet.getDescription());
        assertEquals(status, pet.getStatus());
        assertEquals(foundationId, pet.getFoundationId());
        assertEquals(now, pet.getCreatedAt());
        assertEquals(now, pet.getUpdatedAt());
    }

    @Test
    void petStatus_EnumValues_AreCorrect() {
        PetStatus[] statuses = PetStatus.values();
        assertTrue(statuses.length >= 3);
        
        // Test that we can set each status
        for (PetStatus status : statuses) {
            pet.setStatus(status);
            assertEquals(status, pet.getStatus());
        }
    }

    @Test
    void name_HandlesSpecialCharacters() {
        String specialName = "Mñau-Miau 123!";
        pet.setName(specialName);
        assertEquals(specialName, pet.getName());
    }

    @Test
    void species_HandlesCommonTypes() {
        String[] species = {"Dog", "Cat", "Bird", "Rabbit", "Hamster"};
        
        for (String specie : species) {
            pet.setSpecies(specie);
            assertEquals(specie, pet.getSpecies());
        }
    }

    @Test
    void breed_HandlesVariousBreeds() {
        String[] breeds = {"Golden Retriever", "Siamese", "Mixed", "Unknown", "Persian Cat"};
        
        for (String breed : breeds) {
            pet.setBreed(breed);
            assertEquals(breed, pet.getBreed());
        }
    }

    @Test
    void age_HandlesVariousAges() {
        Integer[] ages = {0, 1, 5, 10, 15, 20};
        
        for (Integer age : ages) {
            pet.setAge(age);
            assertEquals(age, pet.getAge());
        }
    }

    @Test
    void age_HandlesMissingAge() {
        pet.setAge(null);
        assertNull(pet.getAge());
    }

    @Test
    void size_HandlesStandardSizes() {
        String[] sizes = {"Small", "Medium", "Large", "Extra Large"};
        
        for (String size : sizes) {
            pet.setSize(size);
            assertEquals(size, pet.getSize());
        }
    }

    @Test
    void description_HandlesLongText() {
        String longDescription = "This is a very long description of a pet that includes many details about its personality, habits, and characteristics. ".repeat(3);
        pet.setDescription(longDescription);
        assertEquals(longDescription, pet.getDescription());
    }

    @Test
    void description_HandlesEmptyAndNull() {
        pet.setDescription("");
        assertEquals("", pet.getDescription());
        
        pet.setDescription(null);
        assertNull(pet.getDescription());
    }

    @Test
    void foundationId_HandlesValidIds() {
        String foundationId = "foundation-uuid-123-456";
        pet.setFoundationId(foundationId);
        assertEquals(foundationId, pet.getFoundationId());
    }

    @Test
    void dateFields_HandleTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earlier = now.minusDays(1);
        
        pet.setCreatedAt(earlier);
        pet.setUpdatedAt(now);
        
        assertEquals(earlier, pet.getCreatedAt());
        assertEquals(now, pet.getUpdatedAt());
        assertTrue(pet.getUpdatedAt().isAfter(pet.getCreatedAt()));
    }

    @Test
    void dateFields_HandleNullValues() {
        pet.setCreatedAt(null);
        pet.setUpdatedAt(null);
        
        assertNull(pet.getCreatedAt());
        assertNull(pet.getUpdatedAt());
    }

    @Test
    void fullPetCreation_WithAllFields() {
        String id = "pet-full-123";
        String name = "Max";
        String species = "Dog";
        String breed = "Labrador";
        Integer age = 2;
        String size = "Medium";
        String description = "Very friendly and energetic";
        PetStatus status = PetStatus.AVAILABLE;
        String foundationId = "foundation-abc";
        LocalDateTime created = LocalDateTime.now().minusDays(5);
        LocalDateTime updated = LocalDateTime.now();

        pet.setId(id);
        pet.setName(name);
        pet.setSpecies(species);
        pet.setBreed(breed);
        pet.setAge(age);
        pet.setSize(size);
        pet.setDescription(description);
        pet.setStatus(status);
        pet.setFoundationId(foundationId);
        pet.setCreatedAt(created);
        pet.setUpdatedAt(updated);

        // Verify all fields are set correctly
        assertEquals(id, pet.getId());
        assertEquals(name, pet.getName());
        assertEquals(species, pet.getSpecies());
        assertEquals(breed, pet.getBreed());
        assertEquals(age, pet.getAge());
        assertEquals(size, pet.getSize());
        assertEquals(description, pet.getDescription());
        assertEquals(status, pet.getStatus());
        assertEquals(foundationId, pet.getFoundationId());
        assertEquals(created, pet.getCreatedAt());
        assertEquals(updated, pet.getUpdatedAt());
    }
}