package com.anubis.dto;

import jakarta.validation.constraints.NotBlank;

public class ApplicationRequest {
    
    @NotBlank(message = "Pet ID es requerido")
    private String petId;
    
    @NotBlank(message = "Mensaje es requerido")
    private String message;

    // Constructores
    public ApplicationRequest() {}

    public ApplicationRequest(String petId, String message) {
        this.petId = petId;
        this.message = message;
    }

    // Getters y Setters
    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
