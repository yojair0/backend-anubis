package com.anubis.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    @Test
    public void testRegisterRequestCreation() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setPhone("123456789");
        
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("Test User", request.getFullName());
        assertEquals("123456789", request.getPhone());
    }

    @Test
    public void testRegisterRequestConstructor() {
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "Test User", "123456789");
        
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("Test User", request.getFullName());
        assertEquals("123456789", request.getPhone());
    }
}