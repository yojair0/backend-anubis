package com.anubis.dto;

import jakarta.validation.constraints.NotNull;
import com.anubis.model.Role;

public class ChangeRoleRequest {
    
    @NotNull(message = "Rol es requerido")
    private Role role;

    public ChangeRoleRequest() {}

    public ChangeRoleRequest(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "ChangeRoleRequest{" +
                "role=" + role +
                '}';
    }
}