package com.anubis.dto;

import java.time.LocalDateTime;

import com.anubis.model.ApplicationStatus;

public class ApplicationDetailResponse {
    
    private String id;
    private String userId;
    private String userFullName;
    private String userEmail;
    private String userPhone;
    private String petId;
    private String petName;
    private String petSpecies;
    private String petBreed;
    private String message;
    private ApplicationStatus status;
    private String foundationResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Campos adicionales de la aplicación
    private String reason;
    private String experience;
    private String livingSpace;
    private Boolean hasOtherPets;
    private String workSchedule;

    // Constructores
    public ApplicationDetailResponse() {}

    public ApplicationDetailResponse(String id, String userId, String userFullName, String userEmail, 
                                   String userPhone, String petId, String petName, String petSpecies, 
                                   String petBreed, String message, ApplicationStatus status, 
                                   String foundationResponse, LocalDateTime createdAt, LocalDateTime updatedAt,
                                   String reason, String experience, String livingSpace, Boolean hasOtherPets, String workSchedule) {
        this.id = id;
        this.userId = userId;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.petId = petId;
        this.petName = petName;
        this.petSpecies = petSpecies;
        this.petBreed = petBreed;
        this.message = message;
        this.status = status;
        this.foundationResponse = foundationResponse;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reason = reason;
        this.experience = experience;
        this.livingSpace = livingSpace;
        this.hasOtherPets = hasOtherPets;
        this.workSchedule = workSchedule;
    }

    // Constructor que toma objetos Application, Pet y User
    public ApplicationDetailResponse(com.anubis.model.Application application, 
                                   com.anubis.model.Pet pet, 
                                   com.anubis.model.User user) {
        this.id = application.getId();
        this.userId = application.getUserId();
        this.petId = application.getPetId();
        this.reason = application.getReason();
        this.experience = application.getExperience();
        this.livingSpace = application.getLivingSpace();
        this.hasOtherPets = application.isHasOtherPets();
        this.workSchedule = application.getWorkSchedule();
        this.status = application.getStatus();
        this.foundationResponse = application.getFoundationResponse();
        this.createdAt = application.getCreatedAt();
        this.updatedAt = application.getUpdatedAt();
        
        if (user != null) {
            this.userFullName = user.getFullName();
            this.userEmail = user.getEmail();
            this.userPhone = user.getPhone();
        }
        
        if (pet != null) {
            this.petName = pet.getName();
            this.petSpecies = pet.getSpecies();
            this.petBreed = pet.getBreed();
        }
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

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetSpecies() {
        return petSpecies;
    }

    public void setPetSpecies(String petSpecies) {
        this.petSpecies = petSpecies;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
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
    }

    public String getFoundationResponse() {
        return foundationResponse;
    }

    public void setFoundationResponse(String foundationResponse) {
        this.foundationResponse = foundationResponse;
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

    // Getters y setters para los nuevos campos
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

    public Boolean getHasOtherPets() {
        return hasOtherPets;
    }

    public void setHasOtherPets(Boolean hasOtherPets) {
        this.hasOtherPets = hasOtherPets;
    }

    public String getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(String workSchedule) {
        this.workSchedule = workSchedule;
    }

    @Override
    public String toString() {
        return "ApplicationDetailResponse{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", petId='" + petId + '\'' +
                ", petName='" + petName + '\'' +
                ", petSpecies='" + petSpecies + '\'' +
                ", petBreed='" + petBreed + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", foundationResponse='" + foundationResponse + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", reason='" + reason + '\'' +
                ", experience='" + experience + '\'' +
                ", livingSpace='" + livingSpace + '\'' +
                ", hasOtherPets=" + hasOtherPets +
                ", workSchedule='" + workSchedule + '\'' +
                '}';
    }
}