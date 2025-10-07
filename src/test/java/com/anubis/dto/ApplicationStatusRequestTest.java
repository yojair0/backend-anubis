package com.anubis.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.anubis.model.ApplicationStatus;

public class ApplicationStatusRequestTest {

    @Test
    void testSettersAndGetters() {
        // Given
        ApplicationStatusRequest request = new ApplicationStatusRequest();

        // When
        request.setStatus(ApplicationStatus.ACCEPTED);

        // Then
        assertEquals(ApplicationStatus.ACCEPTED, request.getStatus());
    }
}