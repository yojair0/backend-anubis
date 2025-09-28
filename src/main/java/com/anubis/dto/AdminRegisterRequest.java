package com.anubis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.anubis.model.Role;

public class AdminRegisterRequest {
    
    @NotBlank(message = "Email es requerido")
    @Email(message = "Formato de email inválido")
    private String email;
    
    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    private String password;
    
    @NotBlank(message = "Nombre completo es requerido")
    private String fullName;
    
    @NotBlank(message = "Teléfono es requerido")
    private String phone;
    
    @NotNull(message = "Rol es requerido")
    private Role role;

    public AdminRegisterRequest() {}

    public AdminRegisterRequest(String email, String password, String fullName, String phone, Role role) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AdminRegisterRequest{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                '}';
    }
}