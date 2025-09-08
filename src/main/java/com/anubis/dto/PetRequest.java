package com.anubis.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PetRequest {
    
    @NotBlank(message = "Nombre es requerido")
    private String name;
    
    @NotBlank(message = "Especie es requerida")
    private String species;
    
    private String breed;
    
    @NotNull(message = "Edad es requerida")
    @Min(value = 0, message = "La edad debe ser mayor o igual a 0")
    private Integer age;
    
    @NotBlank(message = "Género es requerido")
    private String gender;
    
    private String size;
    
    @NotBlank(message = "Descripción es requerida")
    private String description;
    
    private List<String> imageUrls;

    // Constructores
    public PetRequest() {}

    public PetRequest(String name, String species, String breed, Integer age, 
                     String gender, String size, String description, List<String> imageUrls) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.size = size;
        this.description = description;
        this.imageUrls = imageUrls;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
