package com.anubis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationRequestExtraTest {

    private ApplicationRequest applicationRequest;

    @BeforeEach
    void setUp() {
        applicationRequest = new ApplicationRequest();
    }

    @Test
    void defaultConstructor_CreatesEmptyRequest() {
        assertNotNull(applicationRequest);
        assertNull(applicationRequest.getPetId());
        assertNull(applicationRequest.getReason());
        assertNull(applicationRequest.getExperience());
        assertNull(applicationRequest.getLivingSpace());
        assertNull(applicationRequest.getHasOtherPets());
        assertNull(applicationRequest.getWorkSchedule());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        String petId = "pet-123";
        String reason = "I love animals";
        String experience = "5 years with dogs";
        String livingSpace = "House with yard";
        Boolean hasOtherPets = true;
        String workSchedule = "9-5 weekdays";

        applicationRequest.setPetId(petId);
        applicationRequest.setReason(reason);
        applicationRequest.setExperience(experience);
        applicationRequest.setLivingSpace(livingSpace);
        applicationRequest.setHasOtherPets(hasOtherPets);
        applicationRequest.setWorkSchedule(workSchedule);

        assertEquals(petId, applicationRequest.getPetId());
        assertEquals(reason, applicationRequest.getReason());
        assertEquals(experience, applicationRequest.getExperience());
        assertEquals(livingSpace, applicationRequest.getLivingSpace());
        assertEquals(hasOtherPets, applicationRequest.getHasOtherPets());
        assertEquals(workSchedule, applicationRequest.getWorkSchedule());
    }

    @Test
    void reason_HandlesSpecialCharacters() {
        String specialReason = "I want to adopt because: 1) Experience, 2) Love for animals!";
        applicationRequest.setReason(specialReason);
        assertEquals(specialReason, applicationRequest.getReason());
    }

    @Test
    void experience_HandlesVariousInputs() {
        String[] experiences = {"No experience", "5 years", "Lifelong pet owner", "Professional trainer"};
        
        for (String exp : experiences) {
            applicationRequest.setExperience(exp);
            assertEquals(exp, applicationRequest.getExperience());
        }
    }

    @Test
    void livingSpace_HandlesVariousTypes() {
        String[] spaces = {"Apartment", "House", "Farm", "Condo with balcony"};
        
        for (String space : spaces) {
            applicationRequest.setLivingSpace(space);
            assertEquals(space, applicationRequest.getLivingSpace());
        }
    }

    @Test
    void hasOtherPets_HandlesBooleanValues() {
        applicationRequest.setHasOtherPets(true);
        assertTrue(applicationRequest.getHasOtherPets());
        
        applicationRequest.setHasOtherPets(false);
        assertFalse(applicationRequest.getHasOtherPets());
        
        applicationRequest.setHasOtherPets(null);
        assertNull(applicationRequest.getHasOtherPets());
    }

    @Test
    void workSchedule_HandlesVariousSchedules() {
        String[] schedules = {"9-5", "Remote work", "Part-time", "Flexible hours", "Retired"};
        
        for (String schedule : schedules) {
            applicationRequest.setWorkSchedule(schedule);
            assertEquals(schedule, applicationRequest.getWorkSchedule());
        }
    }

    @Test
    void petId_HandlesValidIds() {
        String[] petIds = {"pet-123", "pet_456", "pet789", "pet-abc-def-123"};
        
        for (String petId : petIds) {
            applicationRequest.setPetId(petId);
            assertEquals(petId, applicationRequest.getPetId());
        }
    }

    @Test
    void allFields_CanBeSetToNull() {
        // Set all fields to values first
        applicationRequest.setPetId("pet-123");
        applicationRequest.setReason("reason");
        applicationRequest.setExperience("experience");
        applicationRequest.setLivingSpace("house");
        applicationRequest.setHasOtherPets(true);
        applicationRequest.setWorkSchedule("9-5");

        // Then set all to null
        applicationRequest.setPetId(null);
        applicationRequest.setReason(null);
        applicationRequest.setExperience(null);
        applicationRequest.setLivingSpace(null);
        applicationRequest.setHasOtherPets(null);
        applicationRequest.setWorkSchedule(null);

        assertNull(applicationRequest.getPetId());
        assertNull(applicationRequest.getReason());
        assertNull(applicationRequest.getExperience());
        assertNull(applicationRequest.getLivingSpace());
        assertNull(applicationRequest.getHasOtherPets());
        assertNull(applicationRequest.getWorkSchedule());
    }

    @Test
    void longText_HandlesLargeStrings() {
        String longReason = "This is a very long reason for adoption that spans multiple sentences and contains detailed information about why I want to adopt this pet. ".repeat(3);
        String longExperience = "I have extensive experience with pets including dogs, cats, birds, and other animals over many years. ".repeat(2);
        
        applicationRequest.setReason(longReason);
        applicationRequest.setExperience(longExperience);
        
        assertEquals(longReason, applicationRequest.getReason());
        assertEquals(longExperience, applicationRequest.getExperience());
    }
}