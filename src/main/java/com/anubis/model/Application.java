package com.anubis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "applications")
public class Application {
    
    @Id
    private String id;
    
    private String userId; // ID del usuario que postula
    
    private String petId; // ID de la mascota
    
    private String message; // Mensaje de la postulación
    
    private ApplicationStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String foundationResponse; // Respuesta opcional de la fundación

    // Constructores
    public Application() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ApplicationStatus.PENDING;
    }

    public Application(String userId, String petId, String message) {
        this();
        this.userId = userId;
        this.petId = petId;
        this.message = message;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFoundationResponse() {
        return foundationResponse;
    }

    public void setFoundationResponse(String foundationResponse) {
        this.foundationResponse = foundationResponse;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", petId='" + petId + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
