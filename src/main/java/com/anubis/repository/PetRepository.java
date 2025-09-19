package com.anubis.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.anubis.model.Pet;
import com.anubis.model.PetStatus;

@Repository
public interface PetRepository extends MongoRepository<Pet, String> {
    
    List<Pet> findByActiveTrue();
    
    List<Pet> findByFoundationId(String foundationId);
    
    List<Pet> findByFoundationIdAndActiveTrue(String foundationId);
    
    List<Pet> findByStatus(PetStatus status);
    
    List<Pet> findByStatusAndActiveTrue(PetStatus status);
    
    List<Pet> findBySpeciesAndActiveTrue(String species);
    
    void deleteByFoundationId(String foundationId);
}
