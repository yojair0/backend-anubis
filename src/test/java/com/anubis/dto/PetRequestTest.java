package com.anubis.dto;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PetRequestTest {

    @Test
    void testSettersAndGetters() {
        // Given
        PetRequest request = new PetRequest();

        // When
        request.setName("Max");
        request.setSpecies("Dog");
        request.setBreed("Golden");
        request.setAge(3);
        request.setGender("Male");
        request.setSize("Large");
        request.setDescription("Friendly dog");
        request.setImageUrls(Arrays.asList("url1", "url2"));

        // Then
        assertEquals("Max", request.getName());
        assertEquals("Dog", request.getSpecies());
        assertEquals("Golden", request.getBreed());
        assertEquals(3, request.getAge());
        assertEquals("Male", request.getGender());
        assertEquals("Large", request.getSize());
        assertEquals("Friendly dog", request.getDescription());
        assertEquals(2, request.getImageUrls().size());
        assertTrue(request.getImageUrls().contains("url1"));
    }
}