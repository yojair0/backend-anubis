package com.anubis.controller;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class HealthControllerSimpleTest {

    @Test
    public void testHealthController() {
        HealthController healthController = new HealthController();
        ResponseEntity<Map<String, Object>> response = healthController.health();
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("Anubis Backend API", response.getBody().get("service"));
        assertEquals("1.0.0", response.getBody().get("version"));
    }
}