package com.anubis.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationModelTest {

    private Application application;

    @BeforeEach
    void setUp() {
        application = new Application();
    }

    @Test
    void constructorWithParameters_CreatesApplicationCorrectly() {
        String userId = "user123";
        String petId = "pet123";
        String reason = "I love pets";
        String experience = "5 years";
        String livingSpace = "House";
        boolean hasOtherPets = true;
        String workSchedule = "9-5";

        Application app = new Application(userId, petId, reason, experience, livingSpace, hasOtherPets, workSchedule);

        assertNotNull(app);
        assertEquals(userId, app.getUserId());
        assertEquals(petId, app.getPetId());
        assertEquals(reason, app.getReason());
        assertEquals(experience, app.getExperience());
        assertEquals(livingSpace, app.getLivingSpace());
        assertEquals(hasOtherPets, app.isHasOtherPets());
        assertEquals(workSchedule, app.getWorkSchedule());
        assertEquals(ApplicationStatus.PENDING, app.getStatus());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        String id = "app123";
        String userId = "user123";
        String petId = "pet123";
        String reason = "Great reason";
        String experience = "Lots of experience";
        String livingSpace = "Apartment";
        boolean hasOtherPets = false;
        String workSchedule = "Flexible";
        ApplicationStatus status = ApplicationStatus.ACCEPTED;
        String response = "Approved!";
        LocalDateTime now = LocalDateTime.now();

        application.setId(id);
        application.setUserId(userId);
        application.setPetId(petId);
        application.setReason(reason);
        application.setExperience(experience);
        application.setLivingSpace(livingSpace);
        application.setHasOtherPets(hasOtherPets);
        application.setWorkSchedule(workSchedule);
        application.setStatus(status);
        application.setFoundationResponse(response);
        application.setCreatedAt(now);
        application.setUpdatedAt(now);

        assertEquals(id, application.getId());
        assertEquals(userId, application.getUserId());
        assertEquals(petId, application.getPetId());
        assertEquals(reason, application.getReason());
        assertEquals(experience, application.getExperience());
        assertEquals(livingSpace, application.getLivingSpace());
        assertEquals(hasOtherPets, application.isHasOtherPets());
        assertEquals(workSchedule, application.getWorkSchedule());
        assertEquals(status, application.getStatus());
        assertEquals(response, application.getFoundationResponse());
        assertEquals(now, application.getCreatedAt());
        assertEquals(now, application.getUpdatedAt());
    }

    @Test
    void defaultConstructor_SetsDefaultValues() {
        assertNotNull(application);
        assertNull(application.getId());
        assertNull(application.getUserId());
        assertNull(application.getPetId());
        assertNull(application.getReason());
        assertNull(application.getExperience());
        assertNull(application.getLivingSpace());
        assertFalse(application.isHasOtherPets());
        assertNull(application.getWorkSchedule());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertNull(application.getFoundationResponse());
        assertNotNull(application.getCreatedAt());
        assertNotNull(application.getUpdatedAt());
    }

    @Test
    void applicationStatus_EnumValues_AreCorrect() {
        ApplicationStatus[] statuses = ApplicationStatus.values();
        assertTrue(statuses.length >= 3);
        
        // Test that we can set each status
        for (ApplicationStatus status : statuses) {
            application.setStatus(status);
            assertEquals(status, application.getStatus());
        }
    }

    @Test
    void reason_HandlesSpecialCharacters() {
        String specialReason = "I want to adopt because: 1) I love animals, 2) I have experience with pets!";
        application.setReason(specialReason);
        assertEquals(specialReason, application.getReason());
    }

    @Test
    void experience_HandlesEmptyAndNullValues() {
        application.setExperience("");
        assertEquals("", application.getExperience());
        
        application.setExperience(null);
        assertNull(application.getExperience());
    }

    @Test
    void livingSpace_HandlesVariousValues() {
        String[] livingSpaces = {"House", "Apartment", "Farm", "Other"};
        
        for (String space : livingSpaces) {
            application.setLivingSpace(space);
            assertEquals(space, application.getLivingSpace());
        }
    }

    @Test
    void hasOtherPets_HandlesBooleanValues() {
        application.setHasOtherPets(true);
        assertTrue(application.isHasOtherPets());
        
        application.setHasOtherPets(false);
        assertFalse(application.isHasOtherPets());
    }

    @Test
    void workSchedule_HandlesVariousSchedules() {
        String[] schedules = {"9-5", "Flexible", "Part-time", "Remote", "Night shift"};
        
        for (String schedule : schedules) {
            application.setWorkSchedule(schedule);
            assertEquals(schedule, application.getWorkSchedule());
        }
    }

    @Test
    void foundationResponse_HandlesLongText() {
        String longResponse = "This is a very long foundation response that contains detailed feedback about the application. ".repeat(5);
        application.setFoundationResponse(longResponse);
        assertEquals(longResponse, application.getFoundationResponse());
    }

    @Test
    void dateFields_HandleLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earlier = now.minusHours(1);
        
        application.setCreatedAt(earlier);
        application.setUpdatedAt(now);
        
        assertEquals(earlier, application.getCreatedAt());
        assertEquals(now, application.getUpdatedAt());
        assertTrue(application.getUpdatedAt().isAfter(application.getCreatedAt()));
    }
}