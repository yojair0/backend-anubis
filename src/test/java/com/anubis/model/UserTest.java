package com.anubis.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPhone("123456789");
        user.setPassword("password");
        user.setRole(Role.USER);
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getFullName());
        assertEquals("123456789", user.getPhone());
        assertEquals("password", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    public void testUserConstructor() {
        User user = new User("test@example.com", "password", "Test User", "123456789", Role.USER);
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("Test User", user.getFullName());
        assertEquals("123456789", user.getPhone());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    public void testUserVerification() {
        User user = new User();
        assertFalse(user.isEmailVerified());
        
        user.setEmailVerified(true);
        assertTrue(user.isEmailVerified());
    }
}