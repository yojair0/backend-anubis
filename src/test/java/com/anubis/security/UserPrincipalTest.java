package com.anubis.security;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserPrincipalTest {

    private UserPrincipal userPrincipal;
    private Collection<GrantedAuthority> authorities;

    @BeforeEach
    void setUp() {
        authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        userPrincipal = new UserPrincipal("user1", "user@test.com", "password", authorities);
    }

    @Test
    void testGetId() {
        assertEquals("user1", userPrincipal.getId());
    }

    @Test
    void testGetUsername() {
        assertEquals("user@test.com", userPrincipal.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("password", userPrincipal.getPassword());
    }

    @Test
    void testGetAuthorities() {
        assertEquals(authorities, userPrincipal.getAuthorities());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(userPrincipal.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(userPrincipal.isEnabled());
    }
}