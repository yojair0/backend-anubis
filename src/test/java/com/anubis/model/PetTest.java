package com.anubis.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PetTest {

    @Test
    public void testPetCreation() {
        Pet pet = new Pet();
        pet.setName("Firulais");
        pet.setSpecies("Dog");
        pet.setBreed("Labrador");
        pet.setAge(3);
        pet.setDescription("Friendly dog");
        
        assertEquals("Firulais", pet.getName());
        assertEquals("Dog", pet.getSpecies());
        assertEquals("Labrador", pet.getBreed());
        assertEquals(3, pet.getAge());
        assertEquals("Friendly dog", pet.getDescription());
    }

    @Test
    public void testPetStatus() {
        Pet pet = new Pet();
        pet.setStatus(PetStatus.AVAILABLE);
        
        assertEquals(PetStatus.AVAILABLE, pet.getStatus());
        
        pet.setStatus(PetStatus.ADOPTED);
        assertEquals(PetStatus.ADOPTED, pet.getStatus());
    }
}