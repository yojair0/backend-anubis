package com.anubis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "applications")
public class Application {
    
    @Id
    private String id;
    
    private String userId;
    
    private String petId;
    
    private String reason;
    
    private String experience;
    
    private String livingSpace;
    
    private boolean hasOtherPets;
    
    private String workSchedule;
    
    private ApplicationStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String foundationResponse;

    public Application() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ApplicationStatus.PENDING;
    }

    public Application(String userId, String petId, String reason, String experience, 
                      String livingSpace, boolean hasOtherPets, String workSchedule) {
        this();
        this.userId = userId;
        this.petId = petId;
        this.reason = reason;
        this.experience = experience;
        this.livingSpace = livingSpace;
        this.hasOtherPets = hasOtherPets;
        this.workSchedule = workSchedule;
    }

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLivingSpace() {
        return livingSpace;
    }

    public void setLivingSpace(String livingSpace) {
        this.livingSpace = livingSpace;
    }

    public boolean isHasOtherPets() {
        return hasOtherPets;
    }

    public void setHasOtherPets(boolean hasOtherPets) {
        this.hasOtherPets = hasOtherPets;
    }

    public String getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(String workSchedule) {
        this.workSchedule = workSchedule;
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
                ", reason='" + reason + '\'' +
                ", experience='" + experience + '\'' +
                ", livingSpace='" + livingSpace + '\'' +
                ", hasOtherPets=" + hasOtherPets +
                ", workSchedule='" + workSchedule + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
