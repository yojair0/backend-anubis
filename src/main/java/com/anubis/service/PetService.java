package com.anubis.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import com.anubis.repository.PetRepository;

@Service
public class PetService {
    
    @Autowired
    private PetRepository petRepository;
    
    public List<Pet> getAllActivePets() {
        return petRepository.findByActiveTrue();
    }
    
    public List<Pet> getAvailablePets() {
        return petRepository.findByStatusAndActiveTrue(PetStatus.AVAILABLE);
    }
    
    public Optional<Pet> getPetById(String id) {
        return petRepository.findById(id);
    }
    
    public List<Pet> getPetsByFoundation(String foundationId) {
        return petRepository.findByFoundationIdAndActiveTrue(foundationId);
    }
    
    public List<Pet> getPetsBySpecies(String species) {
        return petRepository.findBySpeciesAndActiveTrue(species);
    }
    
    public Pet createPet(Pet pet) {
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());
        pet.setActive(true);
        pet.setStatus(PetStatus.AVAILABLE);
        return petRepository.save(pet);
    }
    
    public Pet updatePet(String id, Pet petDetails) {
        Optional<Pet> optionalPet = petRepository.findById(id);
        if (optionalPet.isPresent()) {
            Pet pet = optionalPet.get();
            pet.setName(petDetails.getName());
            pet.setSpecies(petDetails.getSpecies());
            pet.setBreed(petDetails.getBreed());
            pet.setAge(petDetails.getAge());
            pet.setGender(petDetails.getGender());
            pet.setSize(petDetails.getSize());
            pet.setDescription(petDetails.getDescription());
            pet.setImageUrls(petDetails.getImageUrls());
            pet.setUpdatedAt(LocalDateTime.now());
            return petRepository.save(pet);
        }
        throw new RuntimeException("Mascota no encontrada con id: " + id);
    }
    
    public Pet updatePetStatus(String id, PetStatus status) {
        Optional<Pet> optionalPet = petRepository.findById(id);
        if (optionalPet.isPresent()) {
            Pet pet = optionalPet.get();
            pet.setStatus(status);
            pet.setUpdatedAt(LocalDateTime.now());
            return petRepository.save(pet);
        }
        throw new RuntimeException("Mascota no encontrada con id: " + id);
    }
    
    public void deletePet(String id) {
        Optional<Pet> optionalPet = petRepository.findById(id);
        if (optionalPet.isPresent()) {
            Pet pet = optionalPet.get();
            pet.setActive(false);
            pet.setUpdatedAt(LocalDateTime.now());
            petRepository.save(pet);
        } else {
            throw new RuntimeException("Mascota no encontrada con id: " + id);
        }
    }
}
