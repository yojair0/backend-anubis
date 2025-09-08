package com.anubis.dto;

import com.anubis.model.ApplicationStatus;

public class ApplicationStatusRequest {
    
    private ApplicationStatus status;
    private String foundationResponse;

    // Constructores
    public ApplicationStatusRequest() {}

    public ApplicationStatusRequest(ApplicationStatus status, String foundationResponse) {
        this.status = status;
        this.foundationResponse = foundationResponse;
    }

    // Getters y Setters
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
}
