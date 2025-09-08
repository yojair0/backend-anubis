package com.anubis.model;

public enum PetStatus {
    AVAILABLE("AVAILABLE"),
    IN_PROCESS("IN_PROCESS"),
    ADOPTED("ADOPTED");

    private final String value;

    PetStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
