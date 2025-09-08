package com.anubis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class PasswordResetRequest {
    
    @NotBlank(message = "Email es requerido")
    @Email(message = "Formato de email inválido")
    private String email;

    // Constructor vacío
    public PasswordResetRequest() {}

    // Constructor con parámetros
    public PasswordResetRequest(String email) {
        this.email = email;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
