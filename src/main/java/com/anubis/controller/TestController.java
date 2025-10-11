package com.anubis.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> publicTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Public endpoint working");
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pets-test")
    public ResponseEntity<Map<String, Object>> petsTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Pets test endpoint working");
        response.put("status", "success");
        response.put("pets", new String[]{"test-pet-1", "test-pet-2"});
        return ResponseEntity.ok(response);
    }
}