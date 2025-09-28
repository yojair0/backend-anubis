package com.anubis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationRequest {
    
    @NotBlank(message = "El ID de la mascota es requerido")
    private String petId;
    
    @NotBlank(message = "La razón para adoptar es requerida")
    private String reason;
    
    @NotBlank(message = "La experiencia con mascotas es requerida")
    private String experience;
    
    @NotBlank(message = "El espacio de vivienda es requerido")
    private String livingSpace;
    
    @NotNull(message = "Debe especificar si tiene otras mascotas")
    private Boolean hasOtherPets;
    
    @NotBlank(message = "El horario de trabajo es requerido")
    private String workSchedule;

    public ApplicationRequest() {}

    public ApplicationRequest(String petId, String reason, String experience, 
                            String livingSpace, boolean hasOtherPets, String workSchedule) {
        this.petId = petId;
        this.reason = reason;
        this.experience = experience;
        this.livingSpace = livingSpace;
        this.hasOtherPets = hasOtherPets;
        this.workSchedule = workSchedule;
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
        return "ApplicationRequest{" +
                "petId='" + petId + '\'' +
                ", reason='" + reason + '\'' +
                ", experience='" + experience + '\'' +
                ", livingSpace='" + livingSpace + '\'' +
                ", hasOtherPets=" + hasOtherPets +
                ", workSchedule='" + workSchedule + '\'' +
                '}';
    }
}
